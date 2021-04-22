package com.iceicelee.nppaservice.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 请求方式比较简单 就自己写一个得了
 *
 * @author: Yao Shuai
 * @date: 2021/4/7 12:48
 */
@Component
@Primary
public class HttpConnector implements IHttpClient {

    final static Logger logger = LoggerFactory.getLogger(HttpConnector.class);

    @Override
    public String post(String urlStr, Map<String, String> requestProperty, String postData) {
        URL url = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            configConnection(requestProperty, connection);
            connection.setRequestMethod("POST");
            logger.info(new StringBuilder().append("POST访问:").append(url).toString());
            connection.connect();
            connection.getOutputStream().write(postData.getBytes(UTF_8));
            inputStream = connection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.close(bufferedReader);
            this.close(inputStream);
            if (connection != null) {
                connection.disconnect();
            }
        }
        String responseStr = stringBuilder.toString();
        logger.info("GET返回:" + responseStr);
        return responseStr;
    }

    private void configConnection(Map<String, String> requestProperty, HttpURLConnection connection) {
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(30));
        connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));
        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        connection.setRequestProperty("User-Agent", "nppa-service");
        if (requestProperty != null) {
            for (Map.Entry<String, String> entry : requestProperty.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public String get(String urlStr, Map<String, String> reqProps, Map<String, String> getParam) {
        StringBuilder paramReqUrl = new StringBuilder("?");
        if (!getParam.isEmpty()) {
            for (Map.Entry<String, String> entry : getParam.entrySet()) {
                paramReqUrl.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            paramReqUrl.deleteCharAt(paramReqUrl.length() - 1);
        }
        urlStr += paramReqUrl.toString();
        URL url = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            configConnection(reqProps, connection);
            connection.setRequestMethod("GET");
            logger.info("GET访问:" + url);
            connection.connect();
            inputStream = connection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            this.close(bufferedReader);
            this.close(inputStream);
            if (connection != null) {
                connection.disconnect();
            }
        }
        String responseStr = stringBuilder.toString();
        logger.info("GET返回:" + responseStr);
        return responseStr;
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
