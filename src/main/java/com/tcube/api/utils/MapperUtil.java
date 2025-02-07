package com.tcube.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MapperUtil {
	
	static ObjectMapper MAPPER = new ObjectMapper();

	public static <T> T readAsObjectOf(Class<T> clazz, String value) throws Exception {
		try {
			return MAPPER.readValue(value, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}

}
