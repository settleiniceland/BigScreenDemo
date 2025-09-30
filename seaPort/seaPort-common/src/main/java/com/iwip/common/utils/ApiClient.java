package com.iwip.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Instant;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ApiClient {

    private static final String APPID = "harbor";
    private static final String API_URL = "https://10.1.11.60:443/api/auth";
    private static final String LOGIN_SECRET = "M6FqctisK2gXPGChIJVFHul4RuGcf8IZ";

    static {
        // 初始化时禁用SSL证书验证
        disableSslVerification();
    }

    // 禁用SSL证书验证的方法
    private static void disableSslVerification() {
        try {
            // 创建信任所有证书的TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // 创建SSL上下文并使用我们的TrustManager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // 设置主机名验证器，接受所有主机名
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String sendPostRequest() throws Exception {
        // 准备参数
        long time = Instant.now().getEpochSecond();
        String signature = generateSignature(time);

        // 构建请求体
        String requestBody = String.format(
                "{\"appid\":\"%s\",\"time\":%d,\"signature\":\"%s\"}",
                APPID, time, signature
        );

        // 创建并配置连接
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // 发送请求
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 处理响应
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    /**
     * 生成签名 signature = md5(md5(登录秘钥) + time)
     */
    private static String generateSignature(long time) throws Exception {
        // 第一次MD5：md5(登录秘钥)
        String firstMd5 = md5(LOGIN_SECRET);

        // 第二次MD5：md5(第一次结果 + time)
        return md5(firstMd5 + time);
    }

    /**
     * MD5加密（32位小写）
     */
    private static String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
