package netty.db.utils;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

//定期写入文件
public class WriteStreamPeriod{
	
	public WriteStreamPeriod(Date beginTime){
		new TimeInterval(beginTime);
		initialWrite = true;
		this.beginTime = beginTime;
		count = 0;
	}
	public WriteStreamPeriod(WriteStreamPeriod writeStreamPeriod){
	}
	
	public void SetFileName(){
		TimeString timeString = new TimeString();
		try {
			fileName = "/mnt/DataSpace/logfile/"+ timeString.getTimeString(timeString.convertDateToStr(beginTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void WritePeriod(String content){
		synchronized(this){		
			//初次写入文件
			if(initialWrite == true){
				SetFileName();				
				WriteFirst(fileName, content);
				count++;
				initialWrite = false;
				return;
			}	
			//后续写入文件
			if(count < CONSTANT.ITEMS_IN_FILE){
				WriteStreamAppend.method1(fileName, content+"\n");
				count++;
				return;		
			}
			//达到写入文件最大值，需要生成新文件
			count = 0;
			beginTime = new Date();	
			while(check(beginTime)){
				try {
					Thread.sleep(10);
					beginTime = new Date();	
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
			SetFileName();
			FileWriter writer;			 
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件       


			try {
				writer = new FileWriter(fileName, true);	
				writer.close(); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			WriteStreamAppend.method1(fileName, content+"\n");
			count++;
		}	
	}
	
	private void WriteFirst(String file, String content){
		if(check(beginTime)){
			WriteStreamAppend.method1(file, content+"\n");
		}
		else{
			FileWriter writer;			 
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件       
			try {
				writer = new FileWriter(file, true);	
				writer.close(); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			WriteStreamAppend.method1(file, content+"\n");
		}
	}
	
	private boolean check(Date datepath){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		String path = new TimeString().getTimeString(sdf.format(datepath)); 
		File file = new File(path);
        if(file.exists()){
        	return true;  	
        }		
	    return false;
    }
	
	private boolean initialWrite;
	private Date beginTime;
	private long count;
	private String fileName;


    
//    public static void main(String[] args) {
//        WriteStreamPeriod t = new WriteStreamPeriod();
//        System.out.println(t.getFileName(new TimeString().getTimeString()));
//    }

}
