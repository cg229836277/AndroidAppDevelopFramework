package com.chuck.commonlib.util;

import java.io.File;

import com.chuck.commonlib.http.HttpDownloadManager;
import com.chuck.commonlib.http.HttpDownloadService;
import com.chuck.commonlib.http.HttpDownloadService.DownloadListenner;
import com.lidroid.xutils.BitmapUtils;
import android.content.Context;
import android.os.Environment;
import android.webkit.URLUtil;
import android.widget.ImageView;

public class BitmapUtil {
	private Context mContext = null;
	private BitmapUtils bitmapUtil = null;
	private final String defaultSaveImagePath = Environment.getExternalStorageDirectory() + File.separator;
	
	public BitmapUtil(Context context){
		mContext = context;
		bitmapUtil = new BitmapUtils(mContext);
	}
	
	/**
	 * 显示图片，包括本地和网络图片
	 * 
	 * @author admin
	 * @date 2015-5-9 下午3:41:47
	 * @param imagePath 图片地址
	 * @param isDownload 是否下载该图片
	 * @param imageView 加载图片的控件
	 */
	public void showImage(String imageUrl, ImageView imageView){
		if(StringUtil.isEmpty(imageUrl) || imageView == null){
			return;		
		}
		bitmapUtil.display(imageView, imageUrl);
	}
	
	public void showAndDownloadImage(String imageUrl , String saveImagePath ,ImageView imageView){
		if(StringUtil.isEmpty(imageUrl) || imageView == null){
			return;		
		}
		
		if(StringUtil.isEmpty(saveImagePath)){
			saveImagePath = defaultSaveImagePath;
		}
		
		bitmapUtil.display(imageView, imageUrl);
		startDownloadImage(imageUrl , saveImagePath);
	}
	
	/**
	 * 下载图片并且监听进度
	 * 
	 * @author admin
	 * @date 2015-5-9 下午3:54:44
	 * @param imagePath 图片地址
	 * @param isDownload 是否下载
	 * @param imageView 加载图片控件
	 * @param downloadListenner 下载监听
	 */
	public void showAndDownloadImage(String imageUrl , String saveImagePath ,ImageView imageView , DownloadListenner downloadListenner){		
		if(downloadListenner != null){
			HttpDownloadService downloadService= new HttpDownloadService();
			downloadService.setDownloadListenner(downloadListenner);
		}
		showAndDownloadImage(imageUrl,saveImagePath,imageView);
	}
	
	private void startDownloadImage(String imageUrl , String saveImagePath){
		if(!StringUtil.isEmpty(saveImagePath) && !StringUtil.isEmpty(imageUrl) && URLUtil.isNetworkUrl(imageUrl)){
			HttpDownloadManager.singleFileDownload(mContext , imageUrl, saveImagePath);
		}
	}
}
