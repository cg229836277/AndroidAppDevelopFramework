package com.chuck.commonlib.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	
	private static List<String> filePathList = null;
	
	/**
	 * 文件在某个文件夹中是否存在
	 * 
	 * @author admin
	 * @date 2015-4-29 下午5:18:34
	 * @param fileName 文件名
	 * @param fileNames 所要查找是否存在的路径
	 * @return 存在返回true，否则false
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
	
	/**
	 * 判断文件或文件夹是否为空
	 * 
	 * @author admin
	 * @date 2015-5-11 上午10:19:48
	 * @param file 文件夹或者文件
	 * @param isCreate 如果文件或文件夹不存在的话是否创建
	 * @return 为空返回 false，否则返回true
	 */
	public static boolean isFileExist(File file , boolean isCreate){
		if(file != null){
			if(!file.exists() && file.isDirectory()){
				if(isCreate){
					file.mkdirs();
				}
				return false;
			}else if(!file.exists() && file.isFile()){
				if(isCreate){
					try {					
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return false;
			}else if(file.exists()){
				return true;
			}
		}else{
			return false;
		}
		return false;
	}
	
	/**
	 * 在指定目录下查找指定类型的文件，条件是本文件夹下没有二级文件夹
	 * 
	 * @author admin
	 * @date 2015-5-11 下午4:30:04
	 * @param filePath 查找文件的路径
	 * @param fileType 查找文件的类型
	 * @return
	 */
	public File[] findPointedFiles(String filePath, final String fileType) {
		File[] files = null;
		if (!StringUtil.isEmpty(filePath) && !StringUtil.isEmpty(fileType)) {
			File dir = new File(filePath);
			if (dir.exists() && dir.isDirectory()) {
				files = dir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(fileType);
					}
				});
			}
		}
		return files;
	}	
	
	/**
	 * 查找指定文件夹下的某个类型的文件，递归查询
	 * 
	 * @author admin
	 * @date 2015-5-11 下午5:03:35
	 * @param filePath
	 * @param fileType
	 */
	public static List<String> findPointedAllFiles(final String filePath, final String fileType) {
		if(!CollectionUtil.isListNull(filePathList)){
			filePathList.clear();
			filePathList = null;
		}
		// 遍历接收一个文件路径，然后把文件子目录中的所有文件遍历并输出来 
		filePathList = new ArrayList<String>();	
		startFind(filePath, fileType);
		return filePathList;	
    }
	
	private static void startFind(String filePath , String fileType){
		File root = new File(filePath);
        File files[] = root.listFiles();  
        if(files != null && files.length > 0){  
            for (File f : files){  
                if(f.isDirectory()){  
                	startFind(f.getAbsolutePath() , fileType);  
                }else{  
                	if(f.getAbsolutePath().endsWith(fileType)){
	                	filePathList.add(f.getAbsolutePath());
                	}
                }  
            }  
        }else{
        	return;
        }
        return;
	}
}
