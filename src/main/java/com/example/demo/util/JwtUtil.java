package com.example.demo.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Lấy cụm từ bí mật từ application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Lấy thời gian sống của thẻ (1 ngày)
    @Value("${jwt.expiration}")
    private long expiration;

    // Chuyển chuỗi bí mật thành chìa khóa mã hóa
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 1. Hàm TẠO THẺ (Khi đăng nhập)
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Hàm ĐỌC THẺ (Lấy tên user từ thẻ)
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 3. Hàm KIỂM TRA THẺ (Xem thẻ là thật hay giả)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Báo sai nếu thẻ giả, hết hạn hoặc bị sửa đổi
        }
    }
}