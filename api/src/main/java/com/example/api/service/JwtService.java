package com.example.api.service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.api.config.SecretConfig;
import com.example.api.model.database.User;
import com.example.api.repository.TokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * JwtService Class
 * <p>
 * Service class for JWT token generation and validation.
 */
@Service
public class JwtService {
    @Autowired
    SecretConfig secretConfig;

    @Autowired
    TokenRepository tokenRepository;

    /**
     * Generates an access token for the given user.
     *
     * @param user the user for whom the token is generated
     * @return the generated access token
     */
    public String generateAccessToken(User user) {
        return generateToken(user, secretConfig.getJWT_ACCESS_TOKEN_EXPIRED()); // generate token with 1 day expiration
    }

    /**
     * Generates an refresh token for the given user.
     *
     * @param user the user for whom the token is generated
     * @return the refresh access token
     */
    public String generateRefreshToken(User user) {
        return generateToken(user, secretConfig.getJWT_REFRESH_TOKEN_EXPIRED()); // generate token with 1 day expiration
    }

    /**
     * Generates a code token for the given user.
     *
     * @param user the user for whom the token is generated
     * @return the generated code token
     */
    public String generateCodeToken(User user) {
        return generateToken(user, 600000); // generate token with 10 min expiration
    }

    private String generateToken(User user, int expirationInMilli) {
        JwtBuilder builder = Jwts
        .builder()
        .subject(user.getUsername().toString())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expirationInMilli));
       

        try {
            SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG");
            secureRandom.setSeed(532413);
            builder.random(secureRandom);
        } catch (Exception e) {
        }
        return builder.signWith(getSigningKey())
        .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretConfig.getJWT_SECRET_KEY());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validates whether the JWT token is valid for the given user.
     *
     * @param token the JWT token
     * @param user  the UserDetails object representing the user
     * @return true if the token is valid for the user, false otherwise
     */
    public Boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);

        System.out.println("token:" + token);

        Boolean isTokenValid = tokenRepository.findByToken(token).map(t -> !t.isLoggedOut()).orElse(false);

        return (username.equals(user.getUsername().toString())) && !isTokenExpired(token) && isTokenValid;
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            return ex.getClaims();
        }
    }
}
