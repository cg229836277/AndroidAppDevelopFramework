package com.chuck.commonlib.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import android.util.Log;

public class TimeUtil {
	
	public interface GetInternetTimeListenner{
		void getInternetTimeResult(TimeDetail detailTime);
		
		void errorInfo(String errorInfo);
	}
	
	private static GetInternetTimeListenner mGetTimeListenner;
	
	public void setOnInternetTimeListenner(GetInternetTimeListenner getTimeListenner){
		mGetTimeListenner = getTimeListenner;
	}
	
	public static String formatTime(Date date , String format){
		if(date == null || StringUtil.isEmpty(format)){
			return null;
		}
		SimpleDateFormat formatTime = new SimpleDateFormat(format);
		String formatedTime = formatTime.format(date);
		return formatedTime;
	}
	
	public static void getInternetTime(Context context , GetInternetTimeListenner getTimeListenner){
		if(getTimeListenner == null){						
			return;			
		}
		
		mGetTimeListenner = getTimeListenner;
		
		if(!NetworkUtil.isNetworkAvailable(context)){
			mGetTimeListenner.errorInfo("网络没有连接");
			return;
		}		
		
    	final Resources res = context.getResources();
        final int id = Resources.getSystem().getIdentifier("config_ntpServer", "string","android");
        final String defaultServer = res.getString(id);
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				SntpClient client = new SntpClient();
				requestTime(client, defaultServer);
			}
		}).start(); 
	}
	
	private static void requestTime(SntpClient client , String defaultServer){
    	
		boolean isSuccess = client.requestTime(defaultServer , 1000);
		if(isSuccess){
			Log.e("NTP tag", "It is Success");
		     long now = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
		     Date current = new Date(now);
		     
		     String format = formatTime(current , "yyyy:MM:dd:hh:mm:ss");
		     if(!StringUtil.isEmpty(format)){
		    	 String[] detail = format.split(":");
		    	 if(detail != null && detail.length == 6){
			    	 TimeDetail timeDetail = new TimeDetail();
			    	 timeDetail.setYear(detail[0]);
			    	 timeDetail.setMonth(detail[1]);
			    	 timeDetail.setDay(detail[2]);
			    	 timeDetail.setHour(detail[3]);
			    	 timeDetail.setMinute(detail[4]);
			    	 timeDetail.setSecond(detail[5]);			    	 
			    	 mGetTimeListenner.getInternetTimeResult(timeDetail);
			    	 return;
		    	 }
		     }	     
		}else{
			Log.e("NTP tag", "request another time");
			requestTime(client, defaultServer);
		}
    }
	
	public static class TimeDetail{		
		private String year;
		private String month;
		private String day;
		private String hour;
		private String minute;
		private String second;
		public String getYear() {
			return year;
		}
		public void setYear(String year) {
			this.year = year;
		}
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public String getDay() {
			return day;
		}
		public void setDay(String day) {
			this.day = day;
		}
		public String getHour() {
			return hour;
		}
		public void setHour(String hour) {
			this.hour = hour;
		}
		public String getMinute() {
			return minute;
		}
		public void setMinute(String minute) {
			this.minute = minute;
		}
		public String getSecond() {
			return second;
		}
		public void setSecond(String second) {
			this.second = second;
		}
	}
}
