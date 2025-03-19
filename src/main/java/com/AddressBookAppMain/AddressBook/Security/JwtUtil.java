package com.AddressBookAppMain.AddressBook.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Generate Token and Store in Redis
    public String generateToken(String email) {
        String token = JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(secretKey));

        redisTemplate.opsForValue().set("JWT_TOKEN:" + email, token, expirationTime, TimeUnit.MILLISECONDS);
        return token;
    }

    // Get Email from Token
    public String getEmailFromToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secretKey))
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // Retrieve Token from Redis
    public String getStoredToken(String email) {
        return redisTemplate.opsForValue().get("JWT_TOKEN:" + email);
    }

    // ✅ Validate Token
    public boolean isTokenValid(String email, String token) {
        String storedToken = getStoredToken(email);
        return token.equals(storedToken);
    }
}