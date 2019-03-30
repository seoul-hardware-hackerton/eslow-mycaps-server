
/*
 * G 서비스 관리서버 version 1.0
 *
 *  Copyright ⓒ 2016 kt corp. All rights reserved.
 *
 *  This is a proprietary software of kt corp, and you may not use this file except in
 *  compliance with license agreement with kt corp. Any redistribution or use of this
 *  software, with or without modification shall be strictly prohibited without prior written
 *  approval of kt corp, and the copyright notice above does not evidence any actual or
 *  intended publication of such software.
 */

package com.seoulhackerton.mycaps.util;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class RequestUtil {
	
	public static String getClientIp(HttpServletRequest request){
		String ip = request.getHeader("NS-CLIENT-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
		    ip = request.getHeader("X-FORWARDED-FOR"); 
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
		    ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
		    ip = request.getHeader("HTTP_CLIENT_IP"); 
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
		    ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
		    ip = request.getRemoteAddr(); 
		}
		
		return ip;
	}
	
	public static Map<String, Object> getUrlWithParam(HttpServletRequest request){
		
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String> param = new HashMap<String, String>();
		
		String parameterList = ""; 
		String retUrl = request.getRequestURI(); 
		result.put("url", retUrl);

		int k=0; 

		for (java.util.Enumeration<String> e = request.getParameterNames(); e.hasMoreElements() ;k++) { 
			String name = (String) e.nextElement(); 
			String[] value = request.getParameterValues(name);

			if (k == 0) retUrl = retUrl.concat("?"); 
			else if (k>0) retUrl = retUrl.concat("&"); 
			parameterList = parameterList.concat("&");

			for (int q=0; q<value.length;q++){                                 
				if (q>0) {
					retUrl = retUrl.concat("&"); 
					parameterList = parameterList.concat("&"); 
				}
				retUrl = retUrl.concat(name).concat("=").concat(value[q]); 
				parameterList = parameterList.concat(name).concat("=").concat(value[q]); 
				param.put(name, value[q]);				
			}
		}
		
		result.put("retUrl", retUrl);
		result.put("param", param);

		return result;
	}
	
}
