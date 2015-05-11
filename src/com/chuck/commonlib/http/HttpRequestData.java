package com.chuck.commonlib.http;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import com.chuck.commonlib.util.CollectionUtil;
import com.chuck.commonlib.util.StringUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 上传文件或发送数据，分为Get和Post方式
 * @Title：chuck chan
 * @Description：
 * @date 2015-4-27 下午7:12:47
 * @author admin
 * @version 1.0
 */
public class HttpRequestData {
	private final static HttpUtils http = new HttpUtils();
	private static HttpRequestDataListenner mHttpRequestListenner;	
	
	public interface HttpRequestDataListenner{
		public void requestSuccess(String callBackData);
		public void requestError(String errorLog);
		public void requestPercent(float percent);
	}
	
	public void setHttpRequestDataListenner(HttpRequestDataListenner httpRequestListenner){
		mHttpRequestListenner = httpRequestListenner;
	}
	
	/**
	 * get请求
	 * 
	 * @author admin
	 * @date 2015-4-27 下午7:21:14
	 * @param uploadUrl
	 */
	public static void httpGetRequestData(String uploadUrl){
		if(!StringUtil.isEmpty(uploadUrl)){
			http.configTimeout(5000);
			http.send(HttpMethod.GET,uploadUrl,new RequestCallBack<String>(){
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	if(mHttpRequestListenner != null){
		        		mHttpRequestListenner.requestPercent(100 * (current/total));
		        	}
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	if(mHttpRequestListenner != null){
		        		mHttpRequestListenner.requestSuccess(responseInfo.result);
		        	}
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	if(mHttpRequestListenner != null){
		        		mHttpRequestListenner.requestError(msg);
		        	}
		        }
			});
		}
	}
	
	/**
	 * POST	请求,如果上传文件Value必须是File类型
	 * 
	 * @author admin
	 * @date 2015-4-27 下午7:25:49
	 * @param uploadUrl
	 * @param params 请求参数默认是String Object(String , File...)类型
	 */
	public static void httpPostUploadData(String uploadUrl , Map<String , Object> paramsMap){
		if(!StringUtil.isEmpty(uploadUrl)){		
			RequestParams params = dealWithParams(paramsMap);
			http.configTimeout(5000);
			http.send(HttpMethod.POST,uploadUrl,params,new RequestCallBack<String>() {
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	if(mHttpRequestListenner != null){
		        		mHttpRequestListenner.requestPercent(100 * (current/total));
		        	}
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	if(mHttpRequestListenner != null){
		        		mHttpRequestListenner.requestSuccess(responseInfo.result);
		        	}
		        }

		        @Override	
		        public void onFailure(HttpException error, String msg) {
		        	if(mHttpRequestListenner != null){
		        		mHttpRequestListenner.requestError(msg);
		        	}
		        }
			});
		}
	}

	/**
	 * 处理请求参数
	 * 
	 * @author admin
	 * @date 2015-4-28 上午10:06:59
	 * @param paramsMap
	 * @return
	 */
	private static RequestParams dealWithParams(Map<String, Object> paramsMap) {
		if (CollectionUtil.isMapNull(paramsMap)) {
			Iterator<Map.Entry<String, Object>> it = paramsMap.entrySet().iterator();
			RequestParams params = new RequestParams();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				if(value instanceof String){
					params.addBodyParameter(key, (String)value);
				}else if(value instanceof File){
					params.addBodyParameter("file", (File)value);
				}
			}
			return params;			
		}
		return null;
	}
}
