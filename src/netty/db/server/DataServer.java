package netty.db.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture; 
import io.netty.channel.ChannelInitializer; 
import io.netty.channel.ChannelOption; 
import io.netty.channel.EventLoopGroup; 
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel; 
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import netty.db.dq.CircularDoubleBufferedQueue;

/**  * Created with IntelliJ IDEA.  
 *   * User: ASUS  * Date: 14-5-7 
 *   * Time: ����10:10  
 *   * To change this template use File | ` | File Templates.  
 **/
public class DataServer {    
	public void bind(int port, final CircularDoubleBufferedQueue<String> circularDoubleBufferedQueue) 
					 throws Exception{
		// EventLoop ����ԭ���� ChannelFactory 
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup(); 
		try {             
			ServerBootstrap serverBootstrap = new ServerBootstrap(); 
			serverBootstrap.group(bossGroup, workerGroup)       
				.channel(NioServerSocketChannel.class)            
				.childHandler(new ChannelInitializer<SocketChannel>() {  
					@Override               
					public void initChannel(SocketChannel ch) throws Exception {
						ByteBuf delimiter = Unpooled.copiedBuffer("#_$@\r\n".getBytes());
						ch.pipeline().addLast(new DelimiterBasedFrameDecoder(10240, delimiter),
								new DataServerHandler(circularDoubleBufferedQueue)  
						//			  	      new WriteTimeoutHandler(10),    
						//����д�볬ʱ10�빹�����10��ʾ�������10���Ӷ�û������д�ˣ���ô�ͳ�ʱ��       
									  	      /*new ReadTimeoutHandler(10)*/);     
						}                   
				}).option(ChannelOption.SO_KEEPALIVE, true);    
			
			ChannelFuture f = serverBootstrap.bind(port).sync(); 
			f.channel().closeFuture().sync();        
			} catch (InterruptedException e) {  
				
		} finally {             
			workerGroup.shutdownGracefully();    
			bossGroup.shutdownGracefully();       
		}  	
	}
	public static void main(String[] args) {  
		
//		 CircularDoubleBufferedQueue<String> circularDoubleBufferedQueue = new
//				 CircularDoubleBufferedQueue<String>(1000);
//        int port = 9090;
//        if(args != null && args.length > 0){
//        	try{
//        		port = Integer.valueOf(args[0]);
//        	}catch(NumberFormatException e){
//        		System.out.println("����Ĭ�϶˿�����");
//        	}
//        }
//        try {
//			new DataServer().bind(port, circularDoubleBufferedQueue);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	} 
}
