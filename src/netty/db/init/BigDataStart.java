package netty.db.init;

import netty.db.client.DataClient;
import netty.db.dq.CircularDoubleBufferedQueue;
import netty.db.server.DataServer;

public class BigDataStart {
	public static void main(String[] args) throws Exception{
			 

		CircularDoubleBufferedQueue<String> circularDoubleBufferedQueue = new
				CircularDoubleBufferedQueue<String>(1000);
		int port = 9090;
		
	    //�ͻ�
	    DataClient dataClient = new DataClient(circularDoubleBufferedQueue);
	    Thread t = new Thread(dataClient);
	    t.start();
	    
		//������	    
	    if(args != null && args.length > 0){
	    	try{
	    		port = Integer.valueOf(args[0]);
	    	}catch(NumberFormatException e){
	    		System.out.println("����Ĭ�϶˿�����");
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
