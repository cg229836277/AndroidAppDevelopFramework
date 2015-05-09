package com.chuck.commonlib.util;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

public class JsonUtil extends JSON{
	private static final SerializerFeature[] features = { 
			//SerializerFeature.WriteMapNullValue, // 输出空置字段
			SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
			SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
			SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
			//SerializerFeature.WriteNullStringAsEmpty, // 字符类型字段如果为null，输出为""，而不是null
			SerializerFeature.DisableCircularReferenceDetect,
			SerializerFeature.WriteDateUseDateFormat
	};
	private JsonUtil() {
	}

	public static String toJson(Object target) {
		return toJson(target,null);
	}

	public static String toJson(Object target, String fmt) {
		if(StringUtil.isEmpty(fmt)){
			return JSON.toJSONString(target,features);
		}else{
			SerializeConfig sc = new SerializeConfig();
			sc.put(Date.class, new SimpleDateFormatSerializer(fmt));
			return JSON.toJSONString(target,sc,features);
		}
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}
	public static <T> T fromJson(String json, TypeReference<T> type) {
		return JSON.parseObject(json, type);
	}
	public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
		return JSON.parseArray(json, clazz);
	}
	public static <T> List<Object> fromJsonList(String json,Type[] types) {
		return JSON.parseArray(json, types);
	}
}
