package com.project.Notering.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtTokenUtils {


    public static String getUserName(String token, String key) {
        return extractClaims(token, key).get("userName", String.class);
    }

    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();
        return expiredDate.before(new Date());

    }

    private static Claims extractClaims(String token, String key) {
        return Jwts.parser()
                .verifyWith(getKey(key))    // setSigningKey 대신 verifyWith 사용
                .build()
                .parseSignedClaims(token)   // parseClaimsJws 대신 parseSignedClaims 사용
                .getPayload();              // getBody 대신 getPayload 사용
    }




    public static String generateToken(String userName, String key, long expireTimeMs) {

        return Jwts.builder()
                .claim("userName", userName)  // 직접 claim 설정
                .issuedAt(new Date(System.currentTimeMillis()))  // setIssuedAt 대신
                .expiration(new Date(System.currentTimeMillis() + expireTimeMs))  // setExpiration 대신
                .signWith(getKey(key))  // SignatureAlgorithm 파라미터 제거
                .compact();

    }

    private static SecretKey getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);  // 입력받은 키를 사용하여 SecretKey 생성
    }

}
