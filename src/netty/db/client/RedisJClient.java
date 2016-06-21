package netty.db.client;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedisJClient {
	public static ObjectMapper objectMapper;
	private Jedis redis = new Jedis("111.13.137.170", 19997);
//	 private Jedis redis = new Jedis("192.168.3.201", 6379);
	private TeaCoder tea = new TeaCoder();
	private static final String PUSH_KEY = "qaz@123";
	private Pipeline p = redis.pipelined();

	public RedisJClient() {
		// TODO Auto-generated constructor stub
		// 连接redis
		redis.auth("qaz@123");
	}

	public void send(List<String> strList) {

		if ((null == strList) || strList.size() < 1)
			return;
		System.out.println("send=========" + strList.size());
		Jedis jredis = null;
		try {
			jredis = new Jedis("111.13.137.170", 19997);
			jredis.auth("qaz@123");
			Pipeline pl = jredis.pipelined();
			for (String str : strList) {
				// str = str.replace(",'", ",\"");
				// System.out.println("send********" + str);

				SourceGW sourceGW = getGWBean(str);
				String strOut = getOutString(sourceGW);
				// byte[] encryptInfo = strOut.getBytes();
				byte[] encryptInfo = tea.encryptByTea(strOut);
				System.out.println(str);
				pl.lpush(PUSH_KEY.getBytes(), encryptInfo);
			}
			pl.sync();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
            if(jredis!=null){  	
            	try {
					jredis.disconnect();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }

	}

	public void send(String str) {

		str = str.replace("'", "\"");
		SourceGW sourceGW = getGWBean(str);
		String strOut = getOutString(sourceGW);
		byte[] encryptInfo = tea.encryptByTea(strOut);
		redis.lpush(PUSH_KEY.getBytes(), encryptInfo);
	}

	private String getOutString(SourceGW sourceGW) {
		String strOut = null;
		strOut = new String("{\"type\":\"4GDPI\",\"1\":\"" + sourceGW.getDatauuid()
				+ "\",\"2\":\"" + sourceGW.getSrcip() + "\",\"3\":\""
				+ sourceGW.getSrcport() + "\",\"4\":\"" + sourceGW.getDstip()
				+ "\",\"5\":\"" + sourceGW.getDstport() + "\",\"6\":\""
				+ sourceGW.getMethod() + "\",\"7\":\"" + sourceGW.getReqtime()
				+ "\",\"8\":\"" + sourceGW.getHost() + "\"," + "\"9\":\""
				+ sourceGW.getPath() + "\",\"10\":\"" + sourceGW.getReferer()
				+ "\",\"11\":\"" + sourceGW.getUseragent() + "\",\"12\":\""
				+ sourceGW.getCookie() + "\",\"13\":\"" + sourceGW.getLen() + "\"}");
		return strOut;
	}

	private SourceGW getGWBean(String strJsonSource) {
		if (null == strJsonSource)
			return null;
		SourceGW sourceGW = null;
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		try {
			sourceGW = objectMapper.readValue(strJsonSource, SourceGW.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceGW;
	}

	private void get() {
		List list = redis.lrange("qaz@123".getBytes(), 0, 5);
		for (int i = 0; i < list.size(); i++) {
			String decryptInfoStr = tea.decryptByTea((byte[]) list.get(i));
			System.out.print("解密后的数据：");
			System.out.println(decryptInfoStr);
			System.out.println("-----------");
			// for(byte j : (byte[])list.get(i))
			// System.out.print(j + " ");
		}

	}

	private void test1() {

		String infostr = " __jda=122270672.0xe9c8a161fbe88622.1447241545055.1447241545055.1447241545055.1; __jdu=52809338767131030; __jdv=238571484|direct|-|none|-; __tra=1736810.1167376060.1447241549.1447241549.1447241904.1; __tru=e3d0287e-1457-4f59-bab9-81cbf41975f3; __trv=1736810%7Cdirect%7C-%7Cnone%7C-; __wga=1448421443354.1448421443354.1447382490958.1443479958861.1.90; buy_uin=876753872; cartNum=5; cid=2; jdAddrId=; jdAddrName=; jdpin=wdFgjaaGFxsuRV; mba_muid=1447241548294-96da0c9a378e40da6f; mobilev=html5; mt_subsite=122%252C1447241549%7C%7C; network=wifi; nickname=%u5927%u558A%u633A%u54E5%u4FDD%u5E73%u5B89; openid1=EABE483D41C1CF8E2CE810A62D670057D66A126056D7A271B156040AA2363E3FB971DCEFA7BFB81F6BC69584909C930D; picture_url=http%3A%2F%2Fq.qlogo.cn%2Fqqapp%2F100273020%2F00000000000000000000000018A98150%2F40; pinId=kXqUuxxAaCdEg3yL6089zw; pinsign=750c768d14877921480e22402f6a80cd; PPRD_P=EA.17052.1.1-UUID.0xe9c8a161fbe88622; retina=1; sid=AWWkvACm3bBPascRsfiZI1eS; sk_history=%u534E%u4E3A%u8363%u80006; sq_open_id=00000000000000000000000018A98150; TrackID=rjJWQ1vSJWsBpXPJdlbDVylsHtewhQzHPHHVfqlE5Z1hgh";

		byte[] encryptInfo = tea.encryptByTea(infostr);
		for (byte j : encryptInfo)
			System.out.print(j + " ");
		// String str=null;
		// try {
		// str = new String(encryptInfo,"iso8859-1");
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		redis.del("test.2016.1.21".getBytes());
		redis.lpush("test.2016.1.21".getBytes(), encryptInfo);
		// redis.lpush("sort", str);
		// redis.lpush("sort", "4");
		// redis.lpush("sort", "6");
		// redis.lpush("sort", "3");
		// redis.lpush("sort", "0");

		// List list = redis.sort("sort".getBytes());//默认是升序

		List list = redis.lrange("test.2016.1.21".getBytes(), 0, -1);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(i + "--------------------");
			for (byte j : (byte[]) list.get(i))
				System.out.print(j + " ");
		}

		// redis.auth("redis");//验证密码
		/*
		 * ----------------------------------------------------------------------
		 * -------------------------------------
		 */
		/**
		 * KEY操作
		 * 
		 * //KEYS Set keys =
		 * redis.keys("*");//列出所有的key，查找特定的key如：redis.keys("foo") Iterator
		 * t1=keys.iterator() ; while(t1.hasNext()){ Object obj1=t1.next();
		 * System.out.println(obj1); }
		 * 
		 * //DEL 移除给定的一个或多个key。如果key不存在，则忽略该命令。 redis.del("name1");
		 * 
		 * //TTL 返回给定key的剩余生存时间(time to live)(以秒为单位) redis.ttl("foo");
		 * 
		 * //PERSIST key 移除给定key的生存时间。 redis.persist("foo");
		 * 
		 * //EXISTS 检查给定key是否存在。 redis.exists("foo");
		 * 
		 * //MOVE key db
		 * 将当前数据库(默认为0)的key移动到给定的数据库db当中。如果当前数据库(源数据库)和给定数据库(目标数据库)
		 * 有相同名字的给定key，或者key不存在于当前数据库，那么MOVE没有任何效果。 redis.move("foo",
		 * 1);//将foo这个key，移动到数据库1
		 * 
		 * //RENAME key newkey
		 * 将key改名为newkey。当key和newkey相同或者key不存在时，返回一个错误。当newkey已经存在时
		 * ，RENAME命令将覆盖旧值。 redis.rename("foo", "foonew");
		 * 
		 * //TYPE key 返回key所储存的值的类型。
		 * System.out.println(redis.type("foo"));//none
		 * (key不存在),string(字符串),list(列表),set(集合),zset(有序集),hash(哈希表)
		 * 
		 * //EXPIRE key seconds 为给定key设置生存时间。当key过期时，它会被自动删除。
		 * redis.expire("foo", 5);//5秒过期 //EXPIREAT
		 * EXPIREAT的作用和EXPIRE一样，都用于为key设置生存时间。不同在于EXPIREAT命令接受的时间参数是UNIX时间戳(unix
		 * timestamp)。
		 * 
		 * //一般SORT用法 最简单的SORT使用方法是SORT key。 redis.lpush("sort", "1");
		 * redis.lpush("sort", "4"); redis.lpush("sort", "6");
		 * redis.lpush("sort", "3"); redis.lpush("sort", "0");
		 * 
		 * List list = redis.sort("sort");//默认是升序 for(int
		 * i=0;i<list.size();i++){ System.out.println(list.get(i)); }
		 */
		/*
		 * ----------------------------------------------------------------------
		 * -------------------------------------
		 */
		/**
		 * STRING 操作
		 * 
		 * //SET key value将字符串值value关联到key。 redis.set("name", "wangjun1");
		 * redis.set("id", "123456"); redis.set("address", "guangzhou");
		 * 
		 * //SETEX key seconds value将值value关联到key，并将key的生存时间设为seconds(以秒为单位)。
		 * redis.setex("foo", 5, "haha");
		 * 
		 * //MSET key value [key value ...]同时设置一个或多个key-value对。
		 * redis.mset("haha","111","xixi","222");
		 * 
		 * //redis.flushAll();清空所有的key
		 * System.out.println(redis.dbSize());//dbSize是多少个key的个数
		 * 
		 * //APPEND key value如果key已经存在并且是一个字符串，APPEND命令将value追加到key原来的值之后。
		 * redis.append("foo",
		 * "00");//如果key已经存在并且是一个字符串，APPEND命令将value追加到key原来的值之后。
		 * 
		 * //GET key 返回key所关联的字符串值 redis.get("foo");
		 * 
		 * //MGET key [key ...] 返回所有(一个或多个)给定key的值 List list =
		 * redis.mget("haha","xixi"); for(int i=0;i<list.size();i++){
		 * System.out.println(list.get(i)); }
		 * 
		 * //DECR key将key中储存的数字值减一。 //DECRBY key
		 * decrement将key所储存的值减去减量decrement。 //INCR key 将key中储存的数字值增一。 //INCRBY
		 * key increment 将key所储存的值加上增量increment。
		 */
		/*
		 * ----------------------------------------------------------------------
		 * -------------------------------------
		 */
		/**
		 * Hash 操作
		 * 
		 * //HSET key field value将哈希表key中的域field的值设为value。 redis.hset("website",
		 * "google", "www.google.cn"); redis.hset("website", "baidu",
		 * "www.baidu.com"); redis.hset("website", "sina", "www.sina.com");
		 * 
		 * //HMSET key field value [field value ...] 同时将多个field -
		 * value(域-值)对设置到哈希表key中。 Map map = new HashMap(); map.put("cardid",
		 * "123456"); map.put("username", "jzkangta"); redis.hmset("hash", map);
		 * 
		 * //HGET key field返回哈希表key中给定域field的值。
		 * System.out.println(redis.hget("hash", "username"));
		 * 
		 * //HMGET key field [field ...]返回哈希表key中，一个或多个给定域的值。 List list =
		 * redis.hmget("website","google","baidu","sina"); for(int
		 * i=0;i<list.size();i++){ System.out.println(list.get(i)); }
		 * 
		 * //HGETALL key返回哈希表key中，所有的域和值。 Map<String,String> map =
		 * redis.hgetAll("hash"); for(Map.Entry entry: map.entrySet()) {
		 * System.out.print(entry.getKey() + ":" + entry.getValue() + "\t"); }
		 * 
		 * //HDEL key field [field ...]删除哈希表key中的一个或多个指定域。 //HLEN key
		 * 返回哈希表key中域的数量。 //HEXISTS key field查看哈希表key中，给定域field是否存在。 //HINCRBY
		 * key field increment为哈希表key中的域field的值加上增量increment。 //HKEYS
		 * key返回哈希表key中的所有域。 //HVALS key返回哈希表key中的所有值。
		 */
		/*
		 * ----------------------------------------------------------------------
		 * -------------------------------------
		 */
		/**
		 * LIST 操作 //LPUSH key value [value ...]将值value插入到列表key的表头。
		 * redis.lpush("list", "abc"); redis.lpush("list", "xzc");
		 * redis.lpush("list", "erf"); redis.lpush("list", "bnh");
		 * 
		 * //LRANGE key start
		 * stop返回列表key中指定区间内的元素，区间以偏移量start和stop指定。下标(index)参数start和stop都以0为底
		 * ，也就是说，以0表示列表的第一个元素，以1表示列表的第二个元素，以此类推。你也可以使用负数下标，以-1表示列表的最后一个元素，-2
		 * 表示列表的倒数第二个元素，以此类推。 List list = redis.lrange("list", 0, -1); for(int
		 * i=0;i<list.size();i++){ System.out.println(list.get(i)); }
		 * 
		 * //LLEN key返回列表key的长度。 //LREM key count
		 * value根据参数count的值，移除列表中与参数value相等的元素。
		 */
		/*
		 * ----------------------------------------------------------------------
		 * -------------------------------------
		 */
		/**
		 * SET 操作 //SADD key member [member ...]将member元素加入到集合key当中。
		 * redis.sadd("testSet", "s1"); redis.sadd("testSet", "s2");
		 * redis.sadd("testSet", "s3"); redis.sadd("testSet", "s4");
		 * redis.sadd("testSet", "s5");
		 * 
		 * //SREM key member移除集合中的member元素。 redis.srem("testSet", "s5");
		 * 
		 * //SMEMBERS key返回集合key中的所有成员。 Set set = redis.smembers("testSet");
		 * Iterator t1=set.iterator() ; while(t1.hasNext()){ Object
		 * obj1=t1.next(); System.out.println(obj1); }
		 * 
		 * //SISMEMBER key member判断member元素是否是集合key的成员。是（true），否则（false）
		 * System.out.println(redis.sismember("testSet", "s4"));
		 * 
		 * //SCARD key返回集合key的基数(集合中元素的数量)。 //SMOVE source destination
		 * member将member元素从source集合移动到destination集合。
		 * 
		 * //SINTER key [key ...]返回一个集合的全部成员，该集合是所有给定集合的交集。 //SINTERSTORE
		 * destination key [key
		 * ...]此命令等同于SINTER，但它将结果保存到destination集合，而不是简单地返回结果集 //SUNION key [key
		 * ...]返回一个集合的全部成员，该集合是所有给定集合的并集。 //SUNIONSTORE destination key [key
		 * ...]此命令等同于SUNION，但它将结果保存到destination集合，而不是简单地返回结果集。 //SDIFF key [key
		 * ...]返回一个集合的全部成员，该集合是所有给定集合的差集 。 //SDIFFSTORE destination key [key
		 * ...]此命令等同于SDIFF，但它将结果保存到destination集合，而不是简单地返回结果集。
		 */

	}

	public static void main(String[] args) {
		String strJson = "{'website': 'jd', 'cookie': ' appsd_mid=-1; g_ut=2; match_mid=-1; pgv_pvid=8158266145; sd_cookie_crttime=1455624206850; pin=testjd; sd_userid=98501455624206850; softdown_mid=-1; softdown_pid=14', 'dstip': '123.103.8.251', 'uuid': 'testjduuid', 'area': 'beijing', 'userid': 'testjd', 'srcport': '56191', 'len': '1248', 'host': 'm.jd.com', 'referer': ' http://i.eqxiu.com/s/HKelrctv', 'accept': ' image/webp, image/png;q=0.9, image/jpeg;q=0.9, image/gif;q=0.9, */*;q=0.8', 'useragent': ' Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; OPPO R7sm Build/LMY47V) AppleWebKit/533.1 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.4 TBS/025490 Mobile Safari/533.1 MicroMessenger/6.3.13.49_r4080b63.740 NetType/cmnet Language/zh_CN', 'srcip': '117.136.27.110', 'path': 'http://pingtcss.qq.com/pingd?dm=i.eqxiu.com&pvi=2588000256&uuid=testjduuid&si=s7796078592&url=/s/HKelrctv&arg=&ty=1&rdm=mp.weixinbridge.com&rurl=/mp/wapredirect&rarg=url%3Dhttp%253A%252F%252Fi.eqxiu.com%252Fs%252FHKelrctv%2523rd&adt=&r2=45828183&r3=-1&r4=1&ext=adid=&pf=&random=1460523686991', 'dstport': '443', 'reqtime': '1460523686002', 'method': 'GET'}";

//		RedisJClient t = new RedisJClient();
//		t.send(strJson);
//		t.get();
		strJson = strJson.replace("'", "\"");
//		RedisJClient t1 = new RedisJClient();
//		List<String> lt = new ArrayList<String>();
//		lt.add(strJson);
//		lt.add(strJson);
//		lt.add(strJson);
//		t1.send(lt);
		Jedis redis = new Jedis("192.168.1.210", 6379);
		redis.auth("qaz@123");
		redis.set("1.test", "1");
		redis.set("1.test1", "test1");
		redis.set("2.test", "2");
	
		// Pipeline p = redis.pipelined();
		// p.lpush(PUSH_KEY.getBytes(), strJson.getBytes());
		// p.lpush(PUSH_KEY.getBytes(), strJson.getBytes());
		// p.lpush(PUSH_KEY.getBytes(), strJson.getBytes());
		// p.lpush(PUSH_KEY.getBytes(), strJson.getBytes());
		// p.lpush(PUSH_KEY.getBytes(), strJson.getBytes());
		// p.sync();
		System.out.println("len=======" + redis.keys("1.*"));

		// byte[] aa = {0x00,0x01};
		// redis.lpush(PUSH_KEY.getBytes(), aa);

		// long begin=System.currentTimeMillis();
		// List<String> lt = new ArrayList<String>();
		// for (int i=0; i<10000;i++){
		// lt.add(strJson);
		// // byte[] encryptInfo = tea.encryptByTea(infostr);
		// // t1.send(strJson);
		// // System.out.println(i);
		// }
		// t1.send(lt);
		// long end = System.currentTimeMillis();
		// System.out.println("------------+++++");
		// System.out.println(end-begin);
		// for (int i=0; i<10000;i++){
		// // lt.add(strJson);
		// // byte[] encryptInfo = tea.encryptByTea(infostr);
		// t1.send(strJson);
		// // System.out.println(i);
		// }
		// long end1 = System.currentTimeMillis();
		// System.out.println("------------+++++");
		// System.out.println(end1-end);
		// // t1.send(strJson);
		// t1.get();
		// strJson = strJson.replace("'", "\"");
		// SourceGW sourceGW = t1.getGWBean(strJson);
		// System.out.println(sourceGW.getWebsite());

	}
}
