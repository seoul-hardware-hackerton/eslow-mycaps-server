
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
	static Properties properties = new Properties();
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	static {
		String path = "../resource.properties";

		try {
			InputStream in = PropertiesReader.class.getClassLoader().getResourceAsStream(path);
			properties.load(in);
			in.close();
		} catch (Exception e) {
			e.getMessage().toString();
		}
	}
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
}
