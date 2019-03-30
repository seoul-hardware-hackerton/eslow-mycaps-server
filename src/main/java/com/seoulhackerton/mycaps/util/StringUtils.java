
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

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static boolean isEmpty(Object arg) {
		return null == arg || "".equals(arg) || "null".equalsIgnoreCase(String.valueOf(arg));
	}

	public static boolean isEmpty(String str) {
		return (str == null || str.trim().length() < 1 || "null".equalsIgnoreCase(str));
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	public static boolean isNotEmpty(Object arg) {
		return !isEmpty(arg);
	}
	
	public static String defaultString(String str, String def) {
		if (isEmpty(str)) {
			return def;
		} else {
			return str;
		}
	}
	
	public static String defaultString(Object arg, String def) {
		if (arg instanceof String) {
			return defaultString(String.valueOf(arg), def);
		} else {
			return isEmpty(arg) ? def : String.valueOf(arg);
		}
	}
	
	public static String defaultString(String str) {
		return defaultString(str, "");
	}
	
	public static String defaultString(Object arg) {
		return defaultString(arg, "");
	}
	
	public static String padding(String target, String paddingType, int length, String paddingChar, String charset) {
		String tmpTarget = defaultString(target);
		byte[] targetArr = null;
		try {
			targetArr = tmpTarget.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			e.getMessage().toString();
		}
		if (targetArr.length > length) {
			return tmpTarget;
		}
		
		String result = "";
		
		int padLength = length - targetArr.length;
		for ( int i = 0 ; i < padLength ; i++ ) {
			result = result.concat(paddingChar);
		}
		
		if ("L".equals(paddingType)) {
			result = result.concat(tmpTarget);
		} else {
			result = tmpTarget.concat(result);
		}
		
		return result;
	}
	
	public static String paddingLeft(String target, String paddingChar, int length) {
		return padding(target, "L", length, paddingChar, "UTF-8");
	}
	
	public static String paddingRight(String target, String paddingChar, int length) {
		return padding(target, "R", length, paddingChar, "UTF-8");
	}
	
	public static String paddingNumber(String target, int length) {
		return padding(target, "L", length, "0", "UTF-8");
	}
	
	public static String paddingString(String target, int length) {
		return padding(target, "R", length, " ", "UTF-8");
	}
	
	public static String trim(String target, String trimType, char trimChar) {
		//target = convNull(target);
				
		String result = "";
				
		if ( "L".equals(trimType) ) {
			int targetIndex = 0;		
			for ( int i = targetIndex ; i < target.length() ; i++ ) {
				if (trimChar == target.charAt(i)) {
					targetIndex++;
				} else {
					result = target.substring(targetIndex, target.length());
					break;
				}
			}
		} else {
			int targetIndex = target.length()-1;		
			for ( int i = targetIndex ; i >= 0 ; i-- ) {
				if ( trimChar == target.charAt(i) ) {
					targetIndex--;
				} else {
					result = target.substring(0, targetIndex+1);
					break;
				}
			}
		}
		
		return result;
	}
	
	public static String trimLeft(String target, char trimChar) {
		return trim(target, "L", trimChar);
	}
	
	public static String trimRight(String target, char trimChar)
	{
		return trim(target, "R", trimChar);
	}
	
	public static String trimNumber(String target) {
		return trim(target, "L", '0');
	}
	
	public static String trimString(String target) {
		return trim(target, "R", ' ');
	}
	
	public static Map<String, Object> trimMap(Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (map.get(key) instanceof String && !isEmpty(map.get(key))) {
				String value = (String) map.get(key);
				resultMap.put(key, value.trim());
			} else {
				resultMap.put(key, map.get(key));				
			}
		}
		
		return resultMap;
	}
	
	public static String escapeString(String src) {
		if (isEmpty(src)) {
			return "";
		}
		
		return src.replaceAll("#", "&#35;")
				.replaceAll("&", "&amp;")
				.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;")
				.replaceAll("\"", "&quot;")
				.replaceAll("\'", "&apos;");
	}
	
	public static String escapeString2(String src) {
		if (isEmpty(src)) {
			return "";
		}
		
		return src.replaceAll(";", "&#59;")
				.replaceAll("#", "&#35;")
				.replaceAll("&", "&amp;")
				.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;")
				.replaceAll("\\(", "&#40;")
				.replaceAll("\\)", "&#41;")
				.replaceAll("\\{", "&#123;")
				.replaceAll("\\}", "&#125;")
				.replaceAll("\"", "&quot;")
				.replaceAll("\'", "&apos;");
	}
	
	
	public static String nl2br(String src) {
		if (isEmpty(src)) {
			return "";
		}
		
		return src.replaceAll("\r", "<br />")
				.replaceAll("\n", "<br />")
				.replaceAll("\r\n", "<br />");
	}
	
	public static String printStackTraceToString(Exception e) {
		StringBuffer sb = new StringBuffer();

		try {
			sb.append(e.toString());
			sb.append("\n");
			StackTraceElement element[] = e.getStackTrace();
			for (int idx = 0; idx < element.length; idx++) {
				sb.append("\tat ");
				sb.append(element[idx].toString());
				sb.append("\n");
			}
		} catch (Exception ex) {
			return e.toString();
		}
		return sb.toString();
	}
	
	public static List<String> stringToList(String str, int splitIndex) {
		List<String> result = new ArrayList<String>();
		int strLength = str.length();
		
		if(strLength < splitIndex){
			result.add(str);
		}else{
			int beginIndex = 0;
			int endIndex = 0;
			while (endIndex < strLength) {
				beginIndex = endIndex;
				endIndex = endIndex + splitIndex;
				if(endIndex > strLength){
					endIndex = strLength;
				}
				result.add(str.substring(beginIndex, endIndex));
			}
		}
		
		return result;
	}
	
	/**
	 *
	 * 비밀번호 정책 검사
	 *
	 * 길이, 필수 특수문자 포함여부, 한글 및 미지원 특수문자 포함 여부, 아이디 4글자 중복 등 검사.
	 *
	 * @return true = 정책 통과
	 */
	public static boolean checkPwdRule(String id, String newPass){

	    // 길이 검사
	    boolean retVal = (newPass.length() >= 8) && (newPass.length() <= 20);

	    if ( retVal ){
	        //한글 및 사용금지 특수문자 포함 검사.
	        retVal &= (newPass.matches("[0-9|a-z|A-Z|" + "!@#$%^*" + "]*"));

	        // 4자 이상 반복 문자열 검사. ex)aaaa, 11111 등
	        Pattern p = Pattern.compile("(\\w)\\1\\1\\1");
	        retVal &= (!p.matcher(newPass).find());

	        // 필수 특수문자가 포함되었는지 검사.(! @ # $ % ^ * + =)
	        p = Pattern.compile("[" + "!@#$%^*" + "]{1}");
	        retVal &= p.matcher(newPass).find();

	        // 숫자 포함 여부 검사
	        p = Pattern.compile("[0-9]{1}");
	        retVal &= p.matcher(newPass).find();

	        // 영문 포함 여부 검사
	        p = Pattern.compile("[a-z|A-Z]{1}");
	        retVal &= p.matcher(newPass).find();

	        // 연속 숫자 여부 검사
	        
			// ID 와 4자 이상 중복 검사.
			if (retVal && !TextUtils.isEmpty(id) && id.length() > 4) {
				int len = id.length() - 4;

				for (int i = 0; i <= len; i++) {
					String subStr = id.substring(i, i + 4);

					p = Pattern.compile(subStr);
					retVal &= (!p.matcher(newPass).find());

					if (!retVal)
						break;
				}
			}
			
			int samePass = 0;
			char chrPass0;
			char chrPass1;
			for(int i=0; i < newPass.length()-1; i++) {
				chrPass0 = newPass.charAt(i);
				chrPass1 = newPass.charAt(i+1);

				if((int)chrPass0 - (int)chrPass1 == -1) {
					samePass = samePass + 1;
				}else{
					if(samePass < 3){
						samePass = 0;
					}
				}
			}

			if(samePass > 2){
				retVal = false;
			}
	    }
	    return retVal;
	}
	
	public static String stringByteCut(String szText, String szKey, int nLength, int nPrev, boolean isNotag, boolean isAdddot){  // 문자열 자르기
	    
	    String rVal = szText;
	    int oF = 0, oL = 0, rF = 0, rL = 0;
	    int nLengthPrev = 0;
	    Pattern p = Pattern.compile("<(/?)([^<>]*)?>", Pattern.CASE_INSENSITIVE);  // 태그제거 패턴
	    
	    if(isNotag) {rVal = p.matcher(rVal).replaceAll("");}  // 태그 제거
	    rVal = rVal.replaceAll("&", "&");
	    rVal = rVal.replaceAll("(!/|\r|\n| )", "");  // 공백제거
	  
	    try {
	      byte[] bytes = rVal.getBytes("UTF-8");     // 바이트로 보관
	 
	      if(szKey != null && !szKey.equals("")) {
	        nLengthPrev = (rVal.indexOf(szKey) == -1)? 0: rVal.indexOf(szKey);  // 일단 위치찾고
	        nLengthPrev = rVal.substring(0, nLengthPrev).getBytes("MS949").length;  // 위치까지길이를 byte로 다시 구한다
	        nLengthPrev = (nLengthPrev-nPrev >= 0)? nLengthPrev-nPrev:0;    // 좀 앞부분부터 가져오도록한다.
	      }
	    
	      // x부터 y길이만큼 잘라낸다. 한글안깨지게.
	      int j = 0;
	 
	      if(nLengthPrev > 0) while(j < bytes.length) {
	        if((bytes[j] & 0x80) != 0) {
	          oF+=2; rF+=3; if(oF+2 > nLengthPrev) {break;} j+=3;
	        } else {if(oF+1 > nLengthPrev) {break;} ++oF; ++rF; ++j;}
	      }
	      
	      j = rF;
	 
	      while(j < bytes.length) {
	        if((bytes[j] & 0x80) != 0) {
	          if(oL+2 > nLength) {break;} oL+=2; rL+=3; j+=3;
	        } else {if(oL+1 > nLength) {break;} ++oL; ++rL; ++j;}
	      }
	 
	      rVal = new String(bytes, rF, rL, "UTF-8");  // charset 옵션
	 
	      if (isAdddot && rF + rL + 3 <= bytes.length) {
	    	  rVal = rVal.concat("...");
	      }  // ...을 붙일지말지 옵션
	    } catch(UnsupportedEncodingException e){ e.getMessage().toString(); }  
	    
	    return rVal;
	  }
	
}