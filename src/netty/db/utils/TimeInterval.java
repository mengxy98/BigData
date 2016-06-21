package netty.db.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeInterval {
	

	public TimeInterval(Date beginTime) {
		super();
		this.beginTime = beginTime;
	}
	
	public TimeInterval(String beginTime) {
		super();
		try {
			this.beginTime = convertStrToDate(beginTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static boolean IntervalDiff(Date beginTime, Date endTime){
		
		// 计算2个日期之差
		long interval = endTime.getTime() - beginTime.getTime(); 
		//System.out.println("相差的毫秒是:" + interval); 
	
		// 3.将long转化成时分秒 
		// 3.1先得到时 
		int hour = new Long(interval / (1000 * 60 * 60)).intValue(); 
		// 3.2再得到分 
		int tempLeft_minute = new Long(interval % (1000 * 60 * 60)).intValue(); 
		// 3.2.1 得到分 
		int minute = new Long(tempLeft_minute / (1000 * 60)).intValue(); 
		// 4.1 得到秒 
		int tempLeft_second = new Long(tempLeft_minute % (1000 * 60)) .intValue(); 
		// 4.2.1 得到分 
		int second = new Long(tempLeft_second / 1000).intValue(); 
		System.out.println("相差时间为：" + hour + "时," + minute + "分," + second + "秒"); 
		return (hour >= CONSTANT.PERIOD) ? true : false;
	}

	public static boolean IntervalDiff(Date endTime){
		Date tempBeginTime = beginTime;
		beginTime = endTime;
		return IntervalDiff(tempBeginTime, endTime);
	}

	
	public String getCurrDay() throws Exception { 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		return sdf.format(new Date());
	}
		
	private Date convertStrToDate(String beginTime2) throws Exception { 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		return sdf.parse(beginTime2); 
	} 
	
	private static Date beginTime;
	
//	public static void main(String[] args) throws Exception{
//		String time1 = "2006-11-02 09:02:02";
//		String time2 = "2006-11-02 9:30:12";
//		TimeInterval iDiff = new TimeInterval(time1);
//		Date t1 = iDiff.convertStrToDate(time1);
//		Date t2 = iDiff.convertStrToDate(time2);
//		boolean b = iDiff.IntervalDiff(t2);
//		System.out.println(b);
//	}
}
