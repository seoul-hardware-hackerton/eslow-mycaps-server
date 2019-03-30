
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;

public class SpringRequest extends HttpServletRequestWrapper {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public SpringRequest(HttpServletRequest request) {
		super(request);
	}

	public HttpServletRequest getHttpRequest() {
		return (HttpServletRequest)super.getRequest();
	}

	// header
	public String[] getAllHeaders(String name) {
		return org.springframework.util.StringUtils.toStringArray(getHttpRequest().getHeaders(name));
	}

	// parameter
	public String[] getParameters(String name) {
		return getHttpRequest().getParameterValues(name);
	}

	public String getParameter(String name, String defaultValue) {
		String str = getHttpRequest().getParameter(name);
		if (str != null && !str.isEmpty()) return str;
		return defaultValue;
	}
	public String getParameter(String name) {
		return getParameter(name, null);
	}

	public int getParameterAsInt(String name, int defaultValue) {		
		String str = getHttpRequest().getParameter(name);
		if (str != null) 
			return Integer.parseInt(str);
		
		return defaultValue;
	}
	public int getParameterAsInt(String name) {
		return getParameterAsInt(name, 0);
	}

	public MultipartFile getParameterAsFile(String name) {
		if (!(getHttpRequest() instanceof MultipartHttpServletRequest)) return null;
		MultipartHttpServletRequest multipartRequest  = (MultipartHttpServletRequest)getHttpRequest();
		return multipartRequest.getFile(name);
	}

	public List<MultipartFile> getParameterAsFiles(String name) {
		if (!(getHttpRequest() instanceof MultipartHttpServletRequest)) return null;
		MultipartHttpServletRequest multipartRequest  = (MultipartHttpServletRequest)getHttpRequest();
		return multipartRequest.getFiles(name);
	}

	public String getQueryParameters() throws UnsupportedEncodingException {
		HttpServletRequest request = getHttpRequest();
		Enumeration<?> e = request.getParameterNames();

		StringBuilder query = new StringBuilder();
		while (e.hasMoreElements()) {
			String key = e.nextElement().toString();
			String values[] = request.getParameterValues(key);

			key = URLEncoder.encode(key, "UTF-8");
			for (int i = 0; i < values.length; i++) {
				String value = URLEncoder.encode(values[i], "UTF-8");
				query.append("&").append(key).append("=").append(value);
			}
		}

		String str = query.toString();
		if (str.isEmpty()) return "";
		return str.substring(1);
	}

	// body
	public String getBody() throws IOException {
		Reader r = getHttpRequest().getReader();
		if (r == null) return null;

		BufferedReader br = new BufferedReader(r);
		StringBuffer sb = new StringBuffer();
		char[] b = new char[4*1024];
		int len;
		while ((len = br.read(b)) >= 0) {
			sb.append(b, 0, len);
		}
		return sb.toString();
	}
	public byte[] getBodyBytes() throws IOException {
		InputStream in = getHttpRequest().getInputStream();
		if (in == null) return null;

		BufferedInputStream bin = new BufferedInputStream(in);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte b[] = new byte[8*1024];
		int len;
		while ((len = bin.read(b)) >= 0) {
			bout.write(b, 0, len);
		}

		b = bout.toByteArray();
		assert(b == null);
		if (b.length <= 0) return null;
		return b;
	}

}

