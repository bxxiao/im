package com.bx.im.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final String CLAIM_KEY_USERID = "uid";
    // 加解密密钥
    private static final String SECRET = "eyJ1aWQiOjY2LCJuYW1lIjoi5byg5LiJIiwiZXhwIjoxNjQ2MDUwMDczfQ";
    // 过期时间（一天）单位ms
    private static final long expiration = 60 * 60 * 24 * 1000;

    public static String generateJWT(Long uid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERID, uid.toString());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static boolean verifyJWT(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        // } catch (ExpiredJwtException | SignatureException e) {
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 解析token并获取其中的uid
     * @param token
     * @return
     */
    public static Long parseUid(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
            String uidStr = (String) claims.get(CLAIM_KEY_USERID);
            return Long.parseLong(uidStr);
        } catch (ExpiredJwtException | SignatureException e) {
            return null;
        }
    }

}
