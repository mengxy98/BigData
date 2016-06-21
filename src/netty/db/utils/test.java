package netty.db.utils;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class test {
	public static void main(String[] args) throws Exception{
		final WriteStreamPeriod writeStreamPeriod = new WriteStreamPeriod(new Date());
		while(true){
			writeStreamPeriod.WritePeriod("a"); 
		}
	}
}     
