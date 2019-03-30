
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpression {
	private static final String REGEX_TAG = "<(/)?([a-zA-Z0-9]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
	private static final String REGEX_NAME = "^[0-9a-zA-Zㄱ-ㅎ가-힝\\s]+$";
	private static final String REGEX_PHONE = "^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})-?[0-9]{3,4}-?[0-9]{4}$";
	private static final String REGEX_EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
	private static final String REGEX_URL = "([\\p{Alnum}]+)://([a-z0-9.\\-&/%\\|\\^=?:@#$(),.+;~\\_]+)";
	private static final String REGEX_MAKE_LINK = "([\\p{Alnum}]+)://([a-z0-9.\\-&/%=?:@#$(),.+;~\\_]+)";
	private static final String REGEX_REMOVE_LINK = "<a href='([\\p{Alnum}]+)://([a-z0-9.\\-&/%=?:@#$(),.+;~\\_]+)' target='_blank'>([\\p{Alnum}]+)://([a-z0-9.\\-&/%=?:@#$(),.+;~\\_]+)</a>";
	private static final String REGEX_IP = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
	
	public static String getRegExTag() {
		return REGEX_TAG;
	}
	
	public static String getRegExName() {
		return REGEX_NAME;
	}
	
	public static String getRegExPhone() {
		return REGEX_PHONE;
	}
	
	public static String getRegExEmail() {
		return REGEX_EMAIL;
	}
	
	public static String getRegExUrl() {
		return REGEX_URL;
	}
	
	public static String getRegExMakeLink() {
		return REGEX_MAKE_LINK;
	}

	public static String getRegExRemoveLink() {
		return REGEX_REMOVE_LINK;
	}
	
	public static String getRegExIp() {
		return REGEX_IP;
	}
	
	public static String getTagRemove(String str) {
		return str.replaceAll(RegularExpression.getRegExTag(), "");
	}
	
	public static boolean isValidName(String name) {
		return name.matches(getRegExName());
	}
	
	public static boolean isValidPhone(String phone) {
		return phone.matches(getRegExPhone());
	}
	
	public static boolean isValidEmail(String email) {
		return email.matches(getRegExEmail());
	}
	
	public static boolean isValidUrl(String url) {
		return url.matches(getRegExUrl());
	}
	
	public static boolean isValidIp(String ip) {
		return ip.matches(getRegExIp());
	}
	
	public static String getMakeUrlLink(String str) {
		String result = str;

		if(StringUtils.isNotEmpty(str)) {
			Pattern p = Pattern.compile(getRegExMakeLink(), Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(str);
			result = m.replaceAll("<a href='http://$2\' target='_blank'>http://$2</a>");
		}

		return result;
	}

	public static String removeMakeUrlLink(String str) {
		String result = str;

		if(StringUtils.isNotEmpty(str)) {
			Pattern p = Pattern.compile(getRegExRemoveLink(), Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(str);
			result = m.replaceAll("http://$2");
		}

		return result;
	}
}