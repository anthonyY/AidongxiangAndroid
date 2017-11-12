package com.aiitec.openapi.utils;

import java.util.List;

import com.aiitec.openapi.db.JsonInterface;
import com.aiitec.openapi.json.JSON;

/**
 * 用自己写的json解析包
 * @author Anthony
 *
 */
public class AiiJson implements JsonInterface{

	@Override
	public String toJsonString(Object t) {
		return JSON.toJsonString(t);
	}

	@Override
	public <T> List<T> parseArray(String json, Class<T> entityClazz) {
		return JSON.parseArray(json, entityClazz);
	}

	@Override
	public <T> T parseObject(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}

}
