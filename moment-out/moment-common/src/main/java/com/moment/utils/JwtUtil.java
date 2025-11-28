package com.moment.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return token 字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 参数校验，避免 NPE
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("JWT secretKey 不能为空");
        }
        if (ttlMillis <= 0) {
            throw new IllegalArgumentException("jwt 过期时间必须为正数毫秒");
        }

        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 使用旧版 jjwt API：signWith(SignatureAlgorithm, byte[])
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return Claims
     */
    public static Claims parseJWT(String secretKey, String token) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("JWT secretKey 不能为空");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("JWT token 不能为空");
        }

        // 使用旧版 jjwt API：parser()
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }
}
