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
	
	public final static String FILE_TYPE_VIDEO = "video";
	public final static String FILE_TYPE_FLASH = "flash";
	public final static String FILE_TYPE_PICTURE = "picture";
	public final static String FILE_TYPE_APP = "app";
	
	public final static String PICTURES_SAVE_PATH = Environment.getExternalStorageDirectory() + File.separator + "cloudroom" + File.separator + "pictures/";
	public final static String FLASHES_SAVE_PATH = Environment.getExternalStorageDirectory() + File.separator + "cloudroom" + File.separator + "flashes/";
	public final static String VIDEOS_SAVE_PATH = Environment.getExternalStorageDirectory() + File.separator + "cloudroom" + File.separator + "videos/";
	public final static String VIDEOS_SAVE_APP = Environment.getExternalStorageDirectory() + File.separator + "cloudroom" + File.separator + "app/";
	
	public static final String IS_LIST = "list";
	
	private static Context mContext;
	
	/**
	 * 下载多个文件
	 * 
	 * @author admin
	 * @date 2015-4-30 下午1:40:48
	 * @param fileUrls 文件地址集合
	 * @param FileType 下载文件的类型
	 */
	public static void MultiFilsDownload(ArrayList<String> fileUrls , String fileType , Context context){
		if(!CollectionUtil.isArrayListNull(fileUrls) && !StringUtil.isEmpty(fileType)){
			mContext = context;
			startDownload(fileUrls , fileType);
		}
	}
	
	/**
	 * 下载单个文件
	 * 
	 * @author admin
	 * @date 2015-4-30 下午1:44:58
	 * @param fileUrl  文件地址 
	 * @param fileType 下载文件类型
	 */
	public static void singleFileDownload(String fileUrl , String fileType , Context context){
		if(!StringUtil.isEmpty(fileUrl) && !StringUtil.isEmpty(fileType)){
			mContext = context;
			startDownload(fileUrl , fileType);
		}
	}
	
	/**
	 * 下载任意文件
	 * 
	 * @author admin
	 * @date 2015-5-9 下午4:00:42
	 * @param fileUrl 文件地址
	 * @param fileSavePath 文件本地保存地址
	 * @param context
	 */
	public static void downloadAnyTypeFile(String fileUrl , String fileSavePath , Context context){
		if(URLUtil.isNetworkUrl(fileUrl) && !StringUtil.isEmpty(fileUrl) && !StringUtil.isEmpty(fileSavePath)){
			Intent serviceIntent = new Intent(context , HttpDownloadService.class);
			serviceIntent.putExtra(DOWNLOAD_URL, fileUrl);
			serviceIntent.putExtra(SAVE_PATH, fileSavePath);
			context.startService(serviceIntent);
		}
	}
	
	/**
	 * 
	 * 
	 * @author admin
	 * @date 2015-5-9 下午4:01:12
	 * @param fileUrl
	 * @param fileSavePath
	 * @param context
	 * @param downloadListenner 文件下载进度监听
	 */
	public static void downloadAnyTypeFile(String fileUrl , String fileSavePath , Context context , DownloadListenner downloadListenner){		
		if(downloadListenner != null){
			HttpDownloadService downloadService= new HttpDownloadService();
			downloadService.setDownloadListenner(downloadListenner);
		}
		downloadAnyTypeFile(fileUrl , fileSavePath , context);
	}
	
	@SuppressWarnings("unchecked")
	private static void startDownload(Object downloadUrl , String fileType){
		Intent serviceIntent = new Intent(mContext , HttpDownloadService.class);
		if(downloadUrl instanceof ArrayList<?>){
			serviceIntent.putStringArrayListExtra(DOWNLOAD_URL, (ArrayList<String>)downloadUrl);
			serviceIntent.putExtra(IS_LIST, true);
		}else if(downloadUrl instanceof String){
			serviceIntent.putExtra(DOWNLOAD_URL, (String)downloadUrl);
			serviceIntent.putExtra(IS_LIST, false);
		}
		
		if(fileType.equals(FILE_TYPE_FLASH)){
			serviceIntent.putExtra(SAVE_PATH, FLASHES_SAVE_PATH);
		}else if(fileType.equals(FILE_TYPE_VIDEO)){
			serviceIntent.putExtra(SAVE_PATH, VIDEOS_SAVE_PATH);
		}else{
			serviceIntent.putExtra(SAVE_PATH, PICTURES_SAVE_PATH);
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
