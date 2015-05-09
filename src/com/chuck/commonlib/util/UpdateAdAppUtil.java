package com.chuck.commonlib.util;


import android.content.Context;

public class UpdateAdAppUtil {
	private static UpdateAppListenner updateListenner;

	private interface UpdateAppListenner{
		void installResult(boolean isSuccess);
	}
	
	public void setUpdateAppListenner(UpdateAppListenner listenner){
		updateListenner = listenner;
	}
	/**
	 * 静默升级安装app
	 * 
	 * @author admin
	 * @date 2015-5-9 上午11:22:41
	 * @param context
	 * @param updateAppPath
	 * @return 返回是否升级成功
	 */
	public static void updateAdApp(final Context context , final String updateAppPath){		
		new Thread(new Runnable() {			
			@Override
			public void run() {
				if(!StringUtil.isEmpty(updateAppPath)){
					int result = PackageInstallUtils.install(context, updateAppPath);
					if(result == PackageInstallUtils.INSTALL_SUCCEEDED){
						if(updateListenner != null){
							updateListenner.installResult(true);
						}
					}else{
						if(updateListenner != null){
							updateListenner.installResult(false);
						}
					}
				}
			}
		}).start();
	}
}
