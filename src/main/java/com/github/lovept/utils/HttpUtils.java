package com.github.lovept.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * @author lovept :)
 * @date 2024/6/26 17:02
 * @description http工具类
 */
public class HttpUtils {

    public static long getHttpFileContentLength(String url) throws IOException {
        int contentLength;
        HttpURLConnection connection = null;
        try {
            connection = getHttpUrlConnection(url);
            contentLength = connection.getContentLength();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return contentLength;
    }


    public static HttpURLConnection getHttpUrlConnection(String url, long startPos, long endPos) throws IOException {
        // 获取链接对象
        HttpURLConnection connection = getHttpUrlConnection(url);

        if (endPos != 0) {
            // bytes=100-200s
            connection.setRequestProperty("RANGE", "bytes=" + startPos + "-" + endPos);
        } else {
            connection.setRequestProperty("RANGE", "bytes=" + startPos + "-");
        }
        return connection;
    }


    public static HttpURLConnection getHttpUrlConnection(String url) throws IOException {
        URL httpUrl = URI.create(url).toURL();
        HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
        return connection;
    }


    public static String getHttpFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
