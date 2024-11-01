package com.project.FreeCycle.Util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

    }

    /**
     * 유저 아이디를 가져오는 메서드
     * Jwts.parser().verifyWith(secretKey) :secretKey를 사용하여 JWT의 서명을 검증
     * parseSignedClaims : 클래임 확인
     * getPayload().get("userId", String.class) userId key를 가져옴
     * **/
    public String getUserId(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", String.class);
    }

    /**
     * 유저 역할 확인하는 메서드
     * **/
    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }
    
    /**
     * 토큰 만료 했는지 검증하는 메서드
     * **/
    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    /**
     * 토큰 생성 메서드
     * userId : 유저아이디
     * role : 역할
     * expiredMs : 만료시간
     * **/
    public String createJwt(String userId, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 생성시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료시간
                .signWith(secretKey)
                .compact();
    }

}
