
package com.fundoo.notes.service.impl;

import com.fundoo.notes.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/*
 * Class Name  : JwtTokenService
 * Description : Generates and validates signed JWT tokens
 *               using an HMAC SHA signing key.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */
@Service
public class JwtTokenService implements TokenService {

    private final SecretKey secretKey;
    private final long expirationMillis;

    // Reads the Base64 secret and token expiry from configuration.
    public JwtTokenService(
            @Value("${app.jwt.secret}") String base64Secret,
            @Value("${app.jwt.expiration-ms}") long expirationMillis) {

        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);

        // HMAC SHA-256 requires a key of at least 256 bits.
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;
    }

    @Override
    public String generateToken(String subject) {

        Date now = new Date();
        Date expiration = new Date(
                now.getTime() + expirationMillis
        );

        // Creates a signed JWT with subject, issue time and expiry.
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String extractSubject(String token) {

        // Verifies the signature before returning the token claims.
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    @Override
    public boolean isTokenValid(String token) {

        try {
            extractSubject(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }
}
