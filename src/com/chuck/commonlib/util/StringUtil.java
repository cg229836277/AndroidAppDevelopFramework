package com.chuck.commonlib.util;

public class StringUtil {

	/**
	 * 为空的话返回true，不为空的话返回false
	 * 
	 * @author admin
	 * @date 2015-4-22 上午9:57:19
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str != null && !"".equals(str)){
			return false;
		}else{
			return true;
		}
	}
}
