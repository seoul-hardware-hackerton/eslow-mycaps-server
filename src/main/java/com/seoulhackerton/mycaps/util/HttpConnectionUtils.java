
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpConnectionUtils {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final int DEFAULT_TIME_OUT = 3000;
	public static final String DEFAULT_PARAMS_ENCORDING = "UTF-8";
	public static final String METHOD_GET = "get";
	public static final String METHOD_POST = "post";
	
	public static int getDefaultTimeOut() {
		return DEFAULT_TIME_OUT;
	}

	public static String getDefaultPramsEncording() {
		return DEFAULT_PARAMS_ENCORDING;
	}

	public static String getMethodGet() {
		return METHOD_GET;
	}

	public static String getMethodPost() {
		return METHOD_POST;
	}
	
	public static String sendRequest(String url) {
		return sendRequest(METHOD_GET, url, null, null, DEFAULT_PARAMS_ENCORDING, DEFAULT_TIME_OUT);
	}
	
	public static String sendRequest(String url, Map<String, Object> params){
		return sendRequest(METHOD_POST, url, null, params, DEFAULT_PARAMS_ENCORDING, DEFAULT_TIME_OUT);
	}
	
	public static String sendRequest(String method, String url) {
		return sendRequest(method, url, null, null, DEFAULT_PARAMS_ENCORDING, DEFAULT_TIME_OUT);
	}
	
	public static String sendRequest(String method, String url, Map<String, Object> params)  {
		return sendRequest(method, url, null, params, DEFAULT_PARAMS_ENCORDING, DEFAULT_TIME_OUT);
	}
	
	public static String sendRequest(String method, String url, Map<String, Object> headers, Map<String, Object> params) {
		return sendRequest(method, url, null, params, DEFAULT_PARAMS_ENCORDING, DEFAULT_TIME_OUT);
	}
	
	public static String sendRequest(String method, String url, Map<String, Object> headers, Map<String, Object> params, String paramsEncoding, int timeout)  {
		String result = "";
		
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setValidateAfterInactivity(-1);
		
		Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(timeout);
		builder.setSocketTimeout(timeout);
		RequestConfig config = builder.build();
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setConnectionManager(connManager);
		httpClientBuilder.setDefaultRequestConfig(config);
		
		CloseableHttpClient client = httpClientBuilder.build();
		
		try {
			List<NameValuePair> paramList = convertParam(params);
			String httpMethod = method.toLowerCase();
			if (httpMethod.equals(METHOD_GET)) {
				String param = params != null ? "?"+ URLEncodedUtils.format(paramList, paramsEncoding) : "";
				
				HttpGet get = new HttpGet(url+param);
				get.setHeader("Connection", "close");
				
				if(headers != null) {
					for(Object okey : headers.keySet()) {
						String key = String.valueOf(okey);
						get.setHeader(key, headers.get(key).toString());
					}
				}

				ResponseHandler<String> rh = new BasicResponseHandler();
				
				try {
					result = client.execute(get, rh);
				}
				catch(Exception e1) {
					e1.getMessage().toString();
				}
				finally {
					get.releaseConnection();
				}
			} else if (httpMethod.equals(METHOD_POST)) {
				HttpPost post = new HttpPost(url);
				post.setHeader("Connection", "close");
				
				if(headers != null) {
					for(Object okey : headers.keySet()) {
						String key = String.valueOf(okey);
						post.setHeader(key, headers.get(key).toString());
					}
				}

				post.setEntity(new UrlEncodedFormEntity(paramList, paramsEncoding));
				ResponseHandler<String> rh = new BasicResponseHandler();
				
				try {
					result = client.execute(post, rh);
				}
				catch(Exception e2) {
					e2.getMessage().toString();
				}
				finally {
					post.releaseConnection();
				}
			}
		} catch (Exception e) {
			e.getMessage().toString();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.getMessage().toString();
			}
			connManager.close();
			connManager.shutdown();
		}

		return result;
	}
	
	public static String sendGetRequest(String url, String params) {
		return sendGetRequest(url, params, DEFAULT_TIME_OUT);
	}
	
	public static String sendJsonRequest(String url, Map<String, Object> headers, String jsonParams, String contentType, String paramsEncoding, int timeout) throws IOException {
		
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setValidateAfterInactivity(-1);
		
		Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(timeout);
		builder.setSocketTimeout(timeout);
		RequestConfig config = builder.build();
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setConnectionManager(connManager);
		httpClientBuilder.setDefaultRequestConfig(config);
		
		CloseableHttpClient client = httpClientBuilder.build();
		
		
		HttpResponse response = null;
		String result = null;		
		HttpPost post = null;
		try {
			post = new HttpPost(url);
			post.setHeader("Connection", "close");
			post.setHeader("Content-Type", contentType);

			if (headers != null) {
				for (Object okey : headers.keySet()) {
					String key = String.valueOf(okey);
					post.setHeader(key, headers.get(key).toString());
				}
			}

			StringEntity input = new StringEntity(jsonParams, paramsEncoding);
			post.setEntity(input);
			
			try {
				response = client.execute(post);
				result = EntityUtils.toString(response.getEntity());
				
			} catch (IOException e2) {
				result="요청 timeout";
				throw e2;
				
			} finally {
				if (response != null)
				post.releaseConnection();				
			}
			
		} catch (IOException e) {
			throw e;
		} finally {
			client.close();
			connManager.close();
			connManager.shutdown();
		}

		return result;
	}
	
	public static String sendGetRequest(String url, String params, int timeout) {
		String result = "";
		
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setValidateAfterInactivity(-1);
		
		Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(timeout);
		builder.setSocketTimeout(timeout);
		RequestConfig config = builder.build();
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setConnectionManager(connManager);
		httpClientBuilder.setDefaultRequestConfig(config);
		
		CloseableHttpClient client = httpClientBuilder.build();

		try {
			HttpGet get = new HttpGet(url+params);
			get.setHeader("Connection", "close");

			ResponseHandler<String> rh = new BasicResponseHandler();

			try {
				result = client.execute(get, rh);
			}
			catch(Exception e1) {
				e1.getMessage().toString();
			}
			finally {
				get.releaseConnection();
			}
		} catch (Exception e) {
			e.getMessage().toString();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.getMessage().toString();
			}
			connManager.close();
			connManager.shutdown();
		}

		return result;
	}
	
	private static List<NameValuePair> convertParam(Map<String, Object> params) {
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();

		if(params != null) {
			for(Object okey : params.keySet()) {
				String key = String.valueOf(okey);
				Object value = params.get(key);
				
				if(value != null) {
					paramList.add(new BasicNameValuePair(key, value.toString()));
				}
			}
		}
		return paramList;
	}	
}