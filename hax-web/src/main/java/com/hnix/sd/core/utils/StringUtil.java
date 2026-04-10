package com.hnix.sd.core.utils;

import java.util.Map;
import com.google.gson.Gson;

public class StringUtil {



	public static Map<String, String> StringToMap(String params) {
		Gson gson = new Gson();
		return gson.fromJson(params, Map.class);
	}

}
