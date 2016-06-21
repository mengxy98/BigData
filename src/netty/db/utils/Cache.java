package netty.db.utils;

import redis.clients.jedis.Jedis;

public class Cache {
	private Jedis redis = new Jedis("127.0.0.1", 6379);
	public Cache() {
		// TODO Auto-generated constructor stub
		redis.auth("qaz@123");

	}
	
	public String get(String key)
	{
		return redis.get(key);
	}
	public void set(String key,String value)
	{
		redis.set(key, value);
	}
	public void put(String key,String value)
	{
		redis.set(key, value);
	}	
	public void expire(String key,int seconds)
	{
		redis.expire(key, seconds);
	}
}
