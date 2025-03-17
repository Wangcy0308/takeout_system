package org.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}") // 从 application.properties 读取配置
    private String secret;  // jwt.secret 替换了硬编码的 secret 字符串

    private Key secretKey;
    private final long expiration = 36000000000L; // 10小时

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes()); // 使用配置的密钥初始化
    }

    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
        System.out.println("Generated token: " + token);

        return token;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Claims parseToken(String token) {
        try {
            System.out.println("[DEBUG] Attempting to Parse Token: " + token); // 打印正在解析的 Token
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 使用固定密钥解析 Token
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("[ERROR] Token Parsing Failed: " + e.getMessage()); // 打印解析失败的错误信息
            return null;
        }
    }

}
