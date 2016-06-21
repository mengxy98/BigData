package netty.db.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import netty.db.dq.CircularDoubleBufferedQueue;

public class DataClient implements Runnable {

	public DataClient(
			CircularDoubleBufferedQueue<String> mycircularDoubleBufferedQueue) {
		circularDoubleBufferedQueue = mycircularDoubleBufferedQueue;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// System.out.println("�ͻ��˿�ʼ: " + "\n");
		String strBuf;
		RedisJClient redisJClient = new RedisJClient();
		int i = 0;
		List<String> ltSend = null;
		while (true) {
			try {

				// System.out.println("circularDoubleBufferedQueue: " +
				// circularDoubleBufferedQueue + "\n");
				strBuf = circularDoubleBufferedQueue.poll(1,
						TimeUnit.MILLISECONDS);
				// System.out.println("---------"+strBuf);
				if (strBuf != null) {
					// System.out.println("�ͻ���:--------------------- " + strBuf
					// + "\n");
					if (i == 0) {
						ltSend = new ArrayList<String>();
					}
					i++;
					ltSend.add(strBuf);
					if (i == 10) {
						redisJClient.send(ltSend);
						ltSend = null;
						i = 0;
					}
				} else {
					try {
						if (null != ltSend) {
							redisJClient.send(ltSend);
							ltSend = null;
						}
						i=0;
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private CircularDoubleBufferedQueue<String> circularDoubleBufferedQueue;
}
