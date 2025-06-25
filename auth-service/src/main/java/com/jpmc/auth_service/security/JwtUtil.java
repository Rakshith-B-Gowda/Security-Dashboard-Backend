package com.jpmc.auth_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component  // utility for JWT operations
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;             // signing secret
    @Value("${jwt.expirationMs}")
    private long expirationMs;         // token TTL

    // build HMAC key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // create JWT with subject and role
    public String generateToken(String username, String role) {
        log.debug("Generating token for: {}", username);                       // debug
        return Jwts.builder()
                .setSubject(username).claim("role", role)
                .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();     // build token
    }

    // parse token and return claims
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody();                                   // parse
    }

    // validate subject and expiry
    public boolean validateToken(String token, String username) {
        Claims claims = extractAllClaims(token);                                // get claims
        boolean valid = claims.getSubject().equals(username) && !claims.getExpiration().before(new Date());
        log.debug("Token valid: {}", valid);                                    // debug
        return valid;
    }
}