package com.seoulhackerton.mycaps.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Util {

    public static String asciiToString(byte[] voiceLevel) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < voiceLevel.length; i++) {
            builder.append(String.valueOf(Character.toChars(voiceLevel[i])));
        }
        return builder.toString();
    }

    public static int[] asciiToInt(byte[] voiceLevel) {
        int[] intArray = new int[voiceLevel.length];
        for (int i = 0; i < voiceLevel.length; i++) {
            intArray[i] = Integer.parseInt(String.valueOf(Character.toChars(voiceLevel[i])));
        }
        return intArray;
    }

    public static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;

        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            System.out.println(file.getName());
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }

    public static final int DEFAULT_TIME_OUT = 3000;
    public static final String DEFAULT_PARAMS_ENCORDING = "UTF-8";
    public static final String METHOD_GET = "get";
    public static final String METHOD_POST = "post";

    public static String sendRequest(String url) {
        return sendRequest(METHOD_GET, url, null, null, DEFAULT_PARAMS_ENCORDING, DEFAULT_TIME_OUT);
    }

    public static String sendRequest(String method, String url, Map<String, Object> headers, Map<String, Object> params, String paramsEncoding, int timeout) {
        String result = "";

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setValidateAfterInactivity(-1);

        RequestConfig.Builder builder = RequestConfig.custom();
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
                String param = params != null ? "?" + URLEncodedUtils.format(paramList, paramsEncoding) : "";

                HttpGet get = new HttpGet(url + param);
                get.setHeader("Connection", "close");

                if (headers != null) {
                    for (Object okey : headers.keySet()) {
                        String key = String.valueOf(okey);
                        get.setHeader(key, headers.get(key).toString());
                    }
                }

                ResponseHandler<String> rh = new BasicResponseHandler();

                try {
                    result = client.execute(get, rh);
                } catch (Exception e1) {
                    e1.getMessage().toString();
                } finally {
                    get.releaseConnection();
                }
            } else if (httpMethod.equals(METHOD_POST)) {
                HttpPost post = new HttpPost(url);
                post.setHeader("Connection", "close");

                if (headers != null) {
                    for (Object okey : headers.keySet()) {
                        String key = String.valueOf(okey);
                        post.setHeader(key, headers.get(key).toString());
                    }
                }

                post.setEntity(new UrlEncodedFormEntity(paramList, paramsEncoding));
                ResponseHandler<String> rh = new BasicResponseHandler();

                try {
                    result = client.execute(post, rh);
                } catch (Exception e2) {
                    e2.getMessage().toString();
                } finally {
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

    private static List<NameValuePair> convertParam(Map<String, Object> params) {
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();

        if (params != null) {
            for (Object okey : params.keySet()) {
                String key = String.valueOf(okey);
                Object value = params.get(key);

                if (value != null) {
                    paramList.add(new BasicNameValuePair(key, value.toString()));
                }
            }
        }
        return paramList;
    }
}
