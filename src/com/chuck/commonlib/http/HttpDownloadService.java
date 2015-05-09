package com.chuck.commonlib.http;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.chuck.commonlib.util.CollectionUtil;
import com.chuck.commonlib.util.FileUtil;
import com.chuck.commonlib.util.StringUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

/**
 * 下载Service，普通下载必须在intent中传两个参数
 * 分别是本地保存路径"path"，下载地址"url"（ArrayList<String>）；
 * @Title：云屋科技
 * @Description：
 * @date 2015-4-29 下午4:06:39
 * @author admin
 * @version 1.0
 */
public class HttpDownloadService extends IntentService {

	private HttpUtils downloadUtils;
	private static DownloadListenner mDownloadListenner;
	private DecimalFormat decimalFormat = new DecimalFormat(".00");
	
    public HttpDownloadService() {
		super("HttpDownloadService");
	}
    
    public interface DownloadListenner{
    	/**
    	 * 下载成功
    	 * 
    	 * @author admin
    	 * @date 2015-5-9 下午3:48:43
    	 * @param filePath
    	 */
    	void downloadSuccess(String filePath);
    	/**
    	 * 下载失败
    	 * 
    	 * @author admin
    	 * @date 2015-5-9 下午3:48:55
    	 * @param errorLog
    	 */
    	void downloadFail(String errorLog);
    	/**
    	 * 下载进度百分比
    	 * 
    	 * @author admin
    	 * @date 2015-5-9 下午3:49:07
    	 * @param progress
    	 */
    	void downloadProgress(String progress);
    }
    
    public void setDownloadListenner(DownloadListenner downloadListenner){
    	mDownloadListenner = downloadListenner;
    }

	@Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();       
        downloadUtils = new HttpUtils();          
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	return super.onStartCommand(intent, flags, startId);
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

   

	@Override
	protected void onHandleIntent(Intent intent) {		
		String saveFilePath = intent.getStringExtra(HttpDownloadManager.SAVE_PATH);
		boolean isList = intent.getBooleanExtra(HttpDownloadManager.IS_LIST , false);
		if(isList && !StringUtil.isEmpty(saveFilePath)){
			ArrayList<String> filePathArray = intent.getStringArrayListExtra(HttpDownloadManager.DOWNLOAD_URL);
			if(!CollectionUtil.isArrayListNull(filePathArray) ){
				downloadUtils.configRequestThreadPoolSize(filePathArray.size() / 2);
				File file = new File(saveFilePath);
				if(!file.exists()){
					file.mkdirs();
				}
				startDownloadMultiFiles(filePathArray , saveFilePath);
			}
		}else{
			String fileUrl = intent.getStringExtra(HttpDownloadManager.DOWNLOAD_URL);
			if(!StringUtil.isEmpty(fileUrl) && !StringUtil.isEmpty(saveFilePath)){
				String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.length());
				startDownloadFile(fileUrl, saveFilePath, fileName);
			}
		}
	}
	
	/**
	 * 下载多个文件
	 * 
	 * @author admin
	 * @date 2015-5-6 下午5:23:41
	 * @param fileUrlArray
	 * @param filePath
	 */
	private void startDownloadMultiFiles(ArrayList<String> fileUrlArray , String filePath){
		String[] fileNames = new File(filePath).list();
		for(String fileUrl : fileUrlArray){
			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.length());
			if(FileUtil.isFileExist(fileName , fileNames)){
				continue;
			}
			startDownloadFile(fileUrl, filePath, fileName);
		}
	}
	/**
	 * 下载单个文件
	 * 
	 * @author admin
	 * @date 2015-5-6 下午5:23:55
	 * @param fileUrl
	 * @param filePath
	 * @param fileName
	 */
	private void startDownloadFile(String fileUrl , String filePath , String fileName){
		if(!StringUtil.isEmpty(fileUrl) && !StringUtil.isEmpty(filePath) && !StringUtil.isEmpty(fileName)){		
			String target = filePath + fileName;
			downloadUtils.download(fileUrl, target, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					if(mDownloadListenner != null){
						mDownloadListenner.downloadSuccess(arg0.result.getAbsolutePath());
					}
				}
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					if(mDownloadListenner != null){
						mDownloadListenner.downloadFail(arg1);
					}
				}
				
				@Override
				public void onLoading(long total, long current,boolean isUploading) {
					if(mDownloadListenner != null){
						String progress = decimalFormat.format(100 * ((float)current/(float)total));
						mDownloadListenner.downloadProgress("" + progress + "%");
					}
					super.onLoading(total, current, isUploading);
				}
			});
		}
	}
}
