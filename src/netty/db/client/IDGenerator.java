package netty.db.client;

import java.util.Date;
import java.util.UUID;



public class IDGenerator {
    
	  private static long num=0; 
	    
	  /**
	   * 随机生成UUID
	   * @return
	   */
	  public static synchronized String getUUID(){
	    UUID uuid=UUID.randomUUID();
	    String str = uuid.toString(); 
	    String uuidStr=str.replace("-", "");
	    return uuidStr;
	  }
	  /**
	   * 根据字符串生成固定UUID
	   * @param name
	   * @return
	   */
	  public static synchronized String getUUID(String name){
	    UUID uuid=UUID.nameUUIDFromBytes(name.getBytes());
	    String str = uuid.toString(); 
	    String uuidStr=str.replace("-", "");
	    return uuidStr;
	  }

	   
	}