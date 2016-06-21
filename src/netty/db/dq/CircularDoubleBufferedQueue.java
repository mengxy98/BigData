package netty.db.dq;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;


/** 
 *  
 * CircularDoubleBufferedQueue.java 
 * 范志强
 * @param <E>2016-4-15 
 */  
public class CircularDoubleBufferedQueue<E> extends AbstractQueue<E>  
implements BlockingQueue<E>, java.io.Serializable  
{  
    private static final long serialVersionUID = 1L;  
    private Logger logger = Logger.getLogger(CircularDoubleBufferedQueue.class.getName());
    
  
    /** The queued items  */  
    private final E[] itemsA;  
    private final E[] itemsB;  
      
    private ReentrantLock readLock, writeLock;  
    private Condition notEmpty;  
    private Condition notFull;  
    private Condition awake;  
      
      
    private E[] writeArray, readArray;  
    private volatile int writeCount, readCount;  
    private int writeArrayHP, writeArrayTP, readArrayHP, readArrayTP;  
      
      
    public CircularDoubleBufferedQueue(int capacity)  
    {  
        if(capacity<=0)  
        {  
            throw new IllegalArgumentException("Queue initial capacity can't less than 0!");  
        }  
          
        itemsA = (E[])new Object[capacity];  
        itemsB = (E[])new Object[capacity];  
  
        readLock = new ReentrantLock();  
        writeLock = new ReentrantLock();  
          
        notEmpty = readLock.newCondition();  
        notFull = writeLock.newCondition();  
        awake = writeLock.newCondition();  
          
        readArray = itemsA;  
        writeArray = itemsB;  
    }  
      
    private void insert(E e)  
    {  
        writeArray[writeArrayTP] = e;  
        ++writeArrayTP;  
        ++writeCount;  
    }  
      
    private E extract()  
    {  
        E e = readArray[readArrayHP];  
        readArray[readArrayHP] = null;  
        ++readArrayHP;  
        --readCount;  
        return e;  
    }  
  
      
    /** 
     *switch condition:  
     *read queue is empty && write queue is not empty 
     *  
     *Notice:This function can only be invoked after readLock is  
         * grabbed,or may cause dead lock 
     * @param timeout 
     * @param isInfinite: whether need to wait forever until some other 
     * thread awake it 
     * @return 
     * @throws InterruptedException 
     */  
    private long queueSwitch(long timeout, boolean isInfinite) throws InterruptedException  
    {  
        writeLock.lock();  
        try  
        {  
            if (writeCount <= 0)  
            {  
                logger.debug("Write Count:" + writeCount + ", Write Queue is empty, do not switch!");  
                try  
                {  
                    logger.debug("Queue is empty, need wait....");  
                    if(isInfinite && timeout<=0)  
                    {  
                        awake.await();  
                        return -1;  
                    }  
                    else  
                    {  
                        return awake.awaitNanos(timeout);  
                    }  
                }  
                catch (InterruptedException ie)  
                {  
                    awake.signal();  
                    throw ie;  
                }  
            }  
            else  
            {  
                E[] tmpArray = readArray;  
                readArray = writeArray;  
                writeArray = tmpArray;  
  
                readCount = writeCount;  
                readArrayHP = 0;  
                readArrayTP = writeArrayTP;  
  
                writeCount = 0;  
                writeArrayHP = readArrayHP;  
                writeArrayTP = 0;  
                  
                notFull.signal();  
                logger.debug("Queue switch successfully!");  
                return -1;  
            }  
        }  
        finally  
        {  
            writeLock.unlock();  
        }  
    }  
  
    //写入数据
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException  
    {  
        if(e == null)  
        {  
            throw new NullPointerException();  
        }  
          
        long nanoTime = unit.toNanos(timeout);  
        writeLock.lockInterruptibly();  
        try  
        {  
            for (;;)  
            {  
                if(writeCount < writeArray.length)  
                {  
                    insert(e);  
                    if (writeCount == 1)  
                    {  
                        awake.signal();  
                    }  
                    return true;  
                }  
                  
                //Time out  
                if(nanoTime<=0)  
                {  
                    logger.debug("offer wait time out!");  
                    return false;  
                }  
                //keep waiting  
                try  
                {  
                    logger.debug("Queue is full, need wait....");  
                    nanoTime = notFull.awaitNanos(nanoTime);  
                }  
                catch(InterruptedException ie)  
                {  
                    notFull.signal();  
                    throw ie;  
                }  
            }  
        }  
        finally  
        {  
            writeLock.unlock();  
        }  
    }  
  
    //
    /** 
     *读者以轮训方式读取数据，如果读缓存为空，则切换，否则持续读取数据"
     *  
     * @param timeout: 
     * @param unit: 用于队列切换 
     * @return 
     * @throws InterruptedException 
     */  
    
    public E poll(long timeout, TimeUnit unit) throws InterruptedException  
    {  
        long nanoTime = unit.toNanos(timeout);  
        readLock.lockInterruptibly();  
          
        try  
        {  
            for(;;)  
            {  
                if(readCount>0)  
                {  
                    return extract();  
                }  
                  
                if(nanoTime<=0)  
                {  
                    logger.debug("poll time out!");  
                    return null;  
                }  
                nanoTime = queueSwitch(nanoTime, false);  
            }  
        }  
        finally  
        {  
            readLock.unlock();  
        }  
    }

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int drainTo(Collection<? super E> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int drainTo(Collection<? super E> arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean offer(E arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void put(E arg0) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int remainingCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public E take() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
  
}

