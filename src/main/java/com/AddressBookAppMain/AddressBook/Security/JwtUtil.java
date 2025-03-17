package com.AddressBookAppMain.AddressBook.Security;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

//
//import static org.springframework.security.config.Elements.JWT;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "your_secret_key";  // Use a secure secret key
    private static final long EXPIRATION_TIME = 3600000;  // 1 hour

    public String generateToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }
}
