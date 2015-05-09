package com.chuck.commonlib.util;

public class FileUtil {
	/**
	 * 文件在本地存在返回true，否则false
	 * 
	 * @author admin
	 * @date 2015-4-29 下午5:18:34
	 * @param fileName 文件名
	 * @param fileNames 所要查找是否存在的路径
	 * @return
	 */
	public static boolean isFileExist(String fileName , String[] fileNames){
		if(fileNames != null && fileNames.length > 0){
			for(String tempFileName : fileNames){
				if(tempFileName.equals(fileName)){
					return true;
				}
			}
		}
		return false;
	}
}
