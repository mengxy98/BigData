package netty.db.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;



//import net.sf.ehcache.CacheManager;
//import net.sf.ehcache.Cache;
//import net.sf.ehcache.CacheManager;
//import net.sf.ehcache.Element;
import net.sf.json.JSONObject;
import netty.db.client.RedisJClient;
import netty.db.dq.CircularDoubleBufferedQueue;
import netty.db.utils.IDGenerator;
import netty.db.utils.CacheManager;
import netty.db.utils.Cache;
import netty.db.utils.WriteStreamPeriod;

public class DataServerHandler extends ChannelInboundHandlerAdapter {
	private static Map<String, String> cacheMap = new HashMap();
//	CacheManager singletonManager = CacheManager.create();
	
	CacheManager cacheManger = new CacheManager();
	Cache cache = null;
	RedisJClient redisJClient = new RedisJClient();
	public DataServerHandler(
			CircularDoubleBufferedQueue<String> circularDoubleBufferedQueue) {
		this.circularDoubleBufferedQueue = circularDoubleBufferedQueue;
		writeStreamPeriod = new WriteStreamPeriod(new Date());
		memDatabase = null;
//		CacheManager singletonManager = CacheManager.create();
//		cache  = singletonManager.getCache("repeatCache");	
		cache = cacheManger.getCache();
	}

	// ChannelHandlerContext通道处理上下文
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// WriteStreamAppend writeStreamAppend = new WriteStreamAppend();
		try {
//			System.out.println("start read---------------");
			ByteBuf bufData = (ByteBuf) msg;
			byte[] data = new byte[bufData.readableBytes()];
			bufData.readBytes(data);
			String body = new String(data, "UTF-8");
			// body += '}';

			// JSON格式数据解析对象
			body = body.replace("null", "NULL");
			JSONObject jo = JSONObject.fromObject(body);
			jo.put("datauuid", IDGenerator.getUUID());
			writeStreamPeriod.WritePeriod(jo.toString());

			String host = (String) jo.get("website");
			host = host.trim();
			String userid = (String) jo.get("userid");
			userid = userid.trim();
			String reqTime = (String) jo.get("reqtime");
			// System.out.println(host);
			// System.out.println(userid);
			// System.out.println(reqTime);
			
//			Element element = cache.get(host + userid);
			String preTime = cache.get(host + userid);
			if (null == preTime) {
//				cacheMap.put(host + userid, reqTime);
//				element = new Element(host + userid,reqTime);
				String key=host + userid;
				cache.put(key,reqTime);
				cache.expire(key, 86400);
//				redisJClient.send(jo.toString());
				circularDoubleBufferedQueue.offer(jo.toString(), 1,
						TimeUnit.MILLISECONDS);
			} else {
//				String preTime = (String) element.getObjectValue();
				long periodHour = (Long.parseLong(reqTime) - Long
						.parseLong(preTime)) / (1000 * 60 * 60);
				if (periodHour > 24) {
//					element = new Element(host + userid,reqTime);
					String key=host + userid;
					cache.put(key,reqTime);
					cache.expire(key, 86400);
//					cacheMap.put(host + userid, reqTime);
//					redisJClient.send(jo.toString());
					circularDoubleBufferedQueue.offer(jo.toString(), 1,
							TimeUnit.MILLISECONDS);
				}

			}
			// circularDoubleBufferedQueue.offer(body, 1,
			// TimeUnit.MILLISECONDS);
			// body = circularDoubleBufferedQueue.poll(1,
			// TimeUnit.MILLISECONDS);
//			System.out.println("server -----------: " + body);
//			System.out.println("\n");
			bufData.release();
		} catch (Exception ex) {
			
			ex.printStackTrace(System.out);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	private CircularDoubleBufferedQueue<String> circularDoubleBufferedQueue;
	private WriteStreamPeriod writeStreamPeriod;
	private Map<String, String> memDatabase;

	public static void main(String[] args) {
		
		CacheManager cacheManger = new CacheManager();
		Cache cache = cacheManger.getCache();
		cache.put("kkk", "test111");
		System.out.println(cache.get("kkk"));
		
//		CacheManager singletonManager = CacheManager.create();
//		Cache cache  = singletonManager.getCache("repeatCache");
//		Element element = new Element("key", "value");
		
//		cache.put(element);
//		Element element1 = cache.get("key");
//		System.out.println(element1.getObjectValue());
//		String strJson = "{'website': 'Null', 'cookie': ' appsd_mid=-1; g_ut=2; match_mid=-1; pgv_pvid=8158266145; sd_cookie_crttime=1455624206850; pin=testjd; sd_userid=98501455624206850; softdown_mid=-1; softdown_pid=14', 'dstip': '123.103.8.251', 'uuid': 'testjduuid', 'area': 'beijing', 'userid': 'testjd', 'srcport': '56191', 'len': '1248', 'host': 'm.jd.com', 'referer': ' http://i.eqxiu.com/s/HKelrctv', 'accept': ' image/webp, image/png;q=0.9, image/jpeg;q=0.9, image/gif;q=0.9, */*;q=0.8', 'useragent': ' Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; OPPO R7sm Build/LMY47V) AppleWebKit/533.1 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.4 TBS/025490 Mobile Safari/533.1 MicroMessenger/6.3.13.49_r4080b63.740 NetType/cmnet Language/zh_CN', 'srcip': '117.136.27.110', 'path': 'http://pingtcss.qq.com/pingd?dm=i.eqxiu.com&pvi=2588000256&uuid=testjduuid&si=s7796078592&url=/s/HKelrctv&arg=&ty=1&rdm=mp.weixinbridge.com&rurl=/mp/wapredirect&rarg=url%3Dhttp%253A%252F%252Fi.eqxiu.com%252Fs%252FHKelrctv%2523rd&adt=&r2=45828183&r3=-1&r4=1&ext=adid=&pf=&random=1460523686991', 'dstport': '443', 'reqtime': '1460523686002', 'method': 'GET'}";
//
//		JSONObject jo = JSONObject.fromObject(strJson);
//		jo.put("datauuid", IDGenerator.getUUID());
//		Object o = jo.get("website");
//		if (null != o) 
//		{
//		String website = (String) jo.get("website");
//		}
//		String host = (String) jo.get("host");
//		String userid = (String) jo.get("userid");
//		String reqTime = (String) jo.get("reqtime");
//		System.out.println(host);
//		System.out.println(userid);
//		System.out.println(reqTime);
////		String preTime = DataServerHandler.cacheMap.get(host + userid);
////		if (null == preTime) {
////			DataServerHandler.cacheMap.put(host + userid, reqTime);
////		} else {
////			long periodHour = (Long.parseLong(reqTime) - Long
////					.parseLong(preTime)) / (1000 * 60 * 60);
////			if (periodHour > 24) {
////				DataServerHandler.cacheMap.put(host + userid, reqTime);
////			}
////
////		}
//
//		System.out.println(jo);

	}

}
