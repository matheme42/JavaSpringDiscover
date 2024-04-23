package com.example.api.service;

import java.util.Date;
import java.util.UUID;
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
     * Generates an authentication token for the given user.
     *
     * @param user the user for whom the token is generated
     * @return the generated authentication token
     */
    public String generateAuthToken(User user) {
        return generateToken(user, 86400000); // generate token with 1 day expiration
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


    /**
     * Generates a mqtt token for the given user.
     *
     * @param user the user for whom the token is generated
     * @return the generated code token
     */
    public String generateMqttToken(User user) {
        return Jwts
        .builder()
        .subject(UUID.randomUUID().toString())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + 86400000))
        .signWith(getSigningKey())
        .compact();        
    }


    private String generateToken(User user, int expirationInMilli) {
        return Jwts
        .builder()
        .subject(user.getUsername().toString())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expirationInMilli))
        .signWith(getSigningKey())
        .compact();
    }

    private SecretKey getSigningKey() {
        byte [] keyBytes = Decoders.BASE64URL.decode(secretConfig.getJWT_SECRET_KEY());
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
     * @param user the UserDetails object representing the user
     * @return true if the token is valid for the user, false otherwise
     */
    public Boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);

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
