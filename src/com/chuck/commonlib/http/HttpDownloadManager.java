package com.chuck.commonlib.http;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.chuck.commonlib.http.HttpDownloadService.DownloadListenner;
import com.chuck.commonlib.util.CollectionUtil;
import com.chuck.commonlib.util.StringUtil;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.webkit.URLUtil;
import android.widget.ImageView;

public class HttpDownloadManager {
	
	public final static String SAVE_PATH = "path";
	public final static String DOWNLOAD_URL = "url";
	
	public static final String IS_LIST = "list";
	
	private static Context mContext;
	
	/**
	 * 下载多个文件
	 * 
	 * @author admin
	 * @date 2015-4-30 下午1:40:48
	 * @param fileUrls 文件地址集合
	 * @param fileSavePath 下载文件的存放本地的地址
	 */
	public static void MultiFilsDownload(Context context , ArrayList<String> fileUrls , String fileSavePath){
		if(!CollectionUtil.isArrayListNull(fileUrls) && !StringUtil.isEmpty(fileSavePath)){
			mContext = context;
			startDownload(fileUrls , fileSavePath);
		}
	}
	
	public static void MultiFilsDownload(Context context , ArrayList<String> fileUrls , String fileSavePath , DownloadListenner downloadListenner){
		if(downloadListenner != null){
			HttpDownloadService downloadService= new HttpDownloadService();
			downloadService.setDownloadListenner(downloadListenner);
		}
		MultiFilsDownload(context, fileUrls, fileSavePath);
	}
	
	/**
	 * 下载单个文件
	 * 
	 * @author admin
	 * @date 2015-4-30 下午1:44:58
	 * @param fileUrl  文件地址 
	 * @param fileType 下载文件的存放本地的地址
	 */
	public static void singleFileDownload(Context context , String fileUrl , String fileSavePath){
		if(!StringUtil.isEmpty(fileUrl) && !StringUtil.isEmpty(fileSavePath)){
			mContext = context;
			startDownload(fileUrl , fileSavePath);
		}
	}
	
	public static void singleFileDownload(Context context , String fileUrl , String fileSavePath , DownloadListenner downloadListenner){
		if(downloadListenner != null){
			HttpDownloadService downloadService= new HttpDownloadService();
			downloadService.setDownloadListenner(downloadListenner);
		}
		
		singleFileDownload(context, fileUrl, fileSavePath);
	}
	
	@SuppressWarnings("unchecked")
	private static void startDownload(Object downloadUrl , String savePath){
		Intent serviceIntent = new Intent(mContext , HttpDownloadService.class);
		if(downloadUrl instanceof ArrayList<?>){
			serviceIntent.putStringArrayListExtra(DOWNLOAD_URL, (ArrayList<String>)downloadUrl);
			serviceIntent.putExtra(IS_LIST, true);
		}else if(downloadUrl instanceof String){
			serviceIntent.putExtra(DOWNLOAD_URL, (String)downloadUrl);
			serviceIntent.putExtra(IS_LIST, false);
		}
		
		if(!StringUtil.isEmpty(savePath)){
			serviceIntent.putExtra(SAVE_PATH, savePath);	
		}else{
			String defaultSavePath = Environment.getExternalStorageDirectory() + File.separator;
			serviceIntent.putExtra(SAVE_PATH, defaultSavePath);	
		}
		mContext.startService(serviceIntent);
	}
	
	public static void stopDownload(Context context){
		Intent serviceIntent = new Intent(mContext , HttpDownloadService.class);
		if(isServiceRunning(context)){
			context.stopService(serviceIntent);
		}
	}
	
	/**
	 * 下载服务是服务是否正在运行
	 * 
	 * @author admin
	 * @date 2015-5-9 下午2:39:54
	 * @param context
	 * @return
	 */
	private static boolean isServiceRunning(Context context) {
        boolean isRunning = false;
        
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (serviceList == null || serviceList.size() == 0) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(HttpDownloadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
