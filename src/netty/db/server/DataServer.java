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
 *   * Time: 上午10:10  
 *   * To change this template use File | ` | File Templates.  
 **/
public class DataServer {    
	public void bind(int port, final CircularDoubleBufferedQueue<String> circularDoubleBufferedQueue) 
					 throws Exception{
		// EventLoop 代替原来的 ChannelFactory 
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
						//控制写入超时10秒构造参数10表示如果持续10秒钟都没有数据写了，那么就超时。       
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
//        		System.out.println("采用默认端口连接");
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
