package com.example.scheduleproject.jwt;

import com.example.scheduleproject.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static javax.crypto.Cipher.SECRET_KEY;

@Component
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    public static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // JWT 생성 메서드
    public String createToken(String username, UserRoleEnum role) {
        Date now = new Date();

        return BEARER_PREFIX +
                Jwts.builder() // 암호화
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim("role", role) // 권한 정보 추가
                        .setExpiration(new Date(now.getTime() + TOKEN_TIME)) // 현재시간 + 만료 시간
                        .setIssuedAt(now) // 발급일
                        .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘
                        .compact().trim(); //생성된 토큰의 공백 제거
    }

    // JWT를 Cookie에 추가하는 메서드
    public void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token);
            cookie.setPath("/");

            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error("Error encoding JWT: {}", e.getMessage());
        }
    }

    // JWT에서 Bearer Prefix 제거
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(BEARER_PREFIX.length());
        }
        logger.error("Invalid Token format");
        throw new IllegalArgumentException("Invalid Token format");
    }

    // JWT 검증 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            logger.error("Empty JWT claims");
        }
        return false;
    }

    // JWT에서 사용자 정보 추출
    public Claims getUserInfoFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)  // 비밀키 사용
                    .parseClaimsJws(token)      // 토큰 파싱
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token");
            throw new IllegalArgumentException("Expired JWT token", e);
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature");
            throw new IllegalArgumentException("Invalid JWT signature", e);
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token");
            throw new IllegalArgumentException("Malformed JWT token", e);
        } catch (Exception e) {
            logger.error("Invalid JWT token");
            throw new IllegalArgumentException("Invalid JWT token", e);
        }

    }
    // JWT에서 권한 추출
    public String extractUserRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // JWT 파싱 (클레임 추출)
    private Claims extractAllClaims(String token) {
        // Bearer 부분을 제거 후 파싱
        String jwtToken = token.trim();  // 공백 제거
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken) // "Bearer " 제거 후 파싱
                .getBody();
    }



    // HttpServletRequest에서 Cookie에 저장된 JWT 가져오기
    public String getTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    try {
                        String token = URLDecoder.decode(cookie.getValue(), "UTF-8").trim(); //공백 제거
                        return substringToken(token);  // Bearer 제거 후 반환
                    } catch (UnsupportedEncodingException e) {
                        logger.error("Error decoding JWT: {}", e.getMessage());
                    }
                }
            }
        }
        return null;
    }
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
