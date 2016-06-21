package netty.db.init;

import netty.db.client.DataClient;
import netty.db.dq.CircularDoubleBufferedQueue;
import netty.db.server.DataServer;

public class BigDataStart {
	public static void main(String[] args) throws Exception{
			 

		CircularDoubleBufferedQueue<String> circularDoubleBufferedQueue = new
				CircularDoubleBufferedQueue<String>(1000);
		int port = 9090;
		
	    //客户
	    DataClient dataClient = new DataClient(circularDoubleBufferedQueue);
	    Thread t = new Thread(dataClient);
	    t.start();
	    
		//服务器	    
	    if(args != null && args.length > 0){
	    	try{
	    		port = Integer.valueOf(args[0]);
	    	}catch(NumberFormatException e){
	    		System.out.println("采用默认端口连接");
	    	}
	    }
	    try {
			new DataServer().bind(port, circularDoubleBufferedQueue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
