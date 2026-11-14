
package com.fundoo.notes.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Class Name  : JwtTokenServiceTest
 * Description : Tests JWT generation, subject extraction,
 *               signature validation and token expiration.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */
class JwtTokenServiceTest {

    private JwtTokenService tokenService;

    // Test-only secret. Never use this key in the real application.
    private final String testSecret =
            Base64.getEncoder().encodeToString(
                    "0123456789ABCDEF0123456789ABCDEF".getBytes()
            );

    @BeforeEach
    void setUp() {
        tokenService = new JwtTokenService(testSecret, 60000);
    }

    @Test
    void shouldGenerateTokenSuccessfully() {

        String token =
                tokenService.generateToken("user@example.com");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractSubjectFromToken() {

        String token =
                tokenService.generateToken("user@example.com");

        String subject = tokenService.extractSubject(token);

        assertEquals("user@example.com", subject);
    }

    @Test
    void shouldValidateCorrectToken() {

        String token =
                tokenService.generateToken("user@example.com");

        assertTrue(tokenService.isTokenValid(token));
    }

    @Test
    void shouldRejectInvalidToken() {

        assertFalse(tokenService.isTokenValid("invalid.token.value"));
    }

    @Test
    void shouldRejectExpiredToken() {

        JwtTokenService expiredTokenService =
                new JwtTokenService(testSecret, -1000);

        String expiredToken =
                expiredTokenService.generateToken("user@example.com");

        assertFalse(expiredTokenService.isTokenValid(expiredToken));
    }
}
