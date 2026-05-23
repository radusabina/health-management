package com.example.healthmanagementbackend.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "mysupersecretkeymysupersecretkey123";
    private static final String REFRESH_SECRET_KEY = "myrefreshsupersecretkeymyrefresh123";

    private final Set<String> blacklistedRefreshTokens = Collections.synchronizedSet(new HashSet<>());

    public Key getAccessKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private Key getRefreshKey() {
        return Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes());
    }

    public String generateToken(UUID userId, String email) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 min
                .signWith(getAccessKey())
                .compact();
    }

    public String generateRefreshToken(UUID userId, String email) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)) // 7 days
                .signWith(getRefreshKey())
                .compact();
    }

    public boolean validateAccessToken(String token, String email) {
        return validate(token, email, getAccessKey());
    }

    public void invalidateRefreshToken(String token) {
        blacklistedRefreshTokens.add(token);
    }

    public boolean validateRefreshToken(String token, String email) {
        if (blacklistedRefreshTokens.contains(token)) return false;
        return validate(token, email, getRefreshKey());
    }

    private boolean validate(String token, String email, Key key) {
        try {
            String subject = extractClaim(token, Claims::getSubject, key);
            return subject.equals(email) && !isExpired(token, key);
        } catch (Exception e) {
            return false;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, Key key) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public boolean isExpired(String token, Key key) {
        Date expiration = extractClaim(token, Claims::getExpiration, key);
        return expiration.before(new Date());
    }

    public UUID extractUserIdFromRefresh(String token) {
        String idString = extractClaim(token, claims -> claims.get("userId", String.class), getRefreshKey());
        return UUID.fromString(idString);
    }

    public String extractEmailFromRefresh(String token) {
        return extractClaim(token, Claims::getSubject, getRefreshKey());
    }
}