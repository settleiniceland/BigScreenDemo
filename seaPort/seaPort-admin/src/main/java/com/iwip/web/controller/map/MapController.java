package com.iwip.web.controller.map;

import com.iwip.common.utils.ApiClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/map")
public class MapController {

    // 缓存accessToken
    private static final Map<String, String> TOKEN_CACHE = new ConcurrentHashMap<>();

    // token过期时间缓存
    private static final Map<String, Long> TOKEN_EXPIRE_CACHE = new ConcurrentHashMap<>();

    // token有效期（秒），默认2小时，可以被动态更新
    private static long TOKEN_VALID_SECONDS = 7200;

    @PostMapping("/getAccessToken")
    public String getAccessToken() throws Exception {
        String cacheKey = "DEFAULT_TOKEN";

        // 检查缓存中是否有有效的token
        if (TOKEN_CACHE.containsKey(cacheKey)) {
            Long expireTime = TOKEN_EXPIRE_CACHE.get(cacheKey);
            // 如果token未过期，直接返回
            if (expireTime != null && System.currentTimeMillis() < expireTime) {
                return TOKEN_CACHE.get(cacheKey);
            }
        }

        // 缓存中没有有效token，重新获取
        String authResponse = ApiClient.sendPostRequest();

        // 解析响应获取token
        String accessToken = parseTokenFromResponse(authResponse);

        if (accessToken != null && !accessToken.isEmpty()) {
            // 缓存token和过期时间
            TOKEN_CACHE.put(cacheKey, accessToken);
            TOKEN_EXPIRE_CACHE.put(cacheKey, System.currentTimeMillis() + TOKEN_VALID_SECONDS * 1000);
            System.out.println("accessToken:" + accessToken);
            return accessToken;
        } else {
            throw new Exception("获取accessToken失败，响应内容: " + authResponse);
        }
    }

    /**
     * 从响应中解析token
     *
     * @param response API响应
     * @return 访问令牌
     */
    private String parseTokenFromResponse(String response) {

        if (response != null && response.contains("accessToken")) {
            String token = getString(response);

            // 同时获取过期时间
            if (response.contains("expiresIn")) {
                int expiresStart = response.indexOf("expiresIn") + 10; // "expiresIn":
                int expiresEnd = response.indexOf(",", expiresStart);
                if (expiresEnd == -1) {
                    expiresEnd = response.indexOf("}", expiresStart);
                }
                String expiresInStr = response.substring(expiresStart, expiresEnd).trim();

                // 清理expiresInStr中可能存在的非数字字符
                expiresInStr = expiresInStr.replaceAll("[^0-9]", "");
                // 更新TOKEN_VALID_SECONDS，将毫秒转换为秒
                long expiresIn = Long.parseLong(expiresInStr) / 1000;
                // 减去5分钟作为安全边界
                TOKEN_VALID_SECONDS = expiresIn > 300 ? expiresIn - 300 : expiresIn;
            }
            return token;
        }
        return null;
    }

    @NotNull
    private static String getString(String response) {
        int tokenStart = response.indexOf("accessToken") + 13; // "accessToken":"
        int tokenEnd = response.indexOf("\"", tokenStart + 1);
        String token = response.substring(tokenStart, tokenEnd).trim();

        // 去除可能的引号和前面的冒号
        if (token.startsWith(":")) {
            token = token.substring(1).trim();
        }
        if (token.startsWith("\"")) {
            token = token.substring(1);
        }
        if (token.endsWith("\"")) {
            token = token.substring(0, token.length() - 1);
        }
        return token;
    }
}
