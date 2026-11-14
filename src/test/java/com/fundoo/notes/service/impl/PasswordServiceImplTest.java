
package com.fundoo.notes.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Class Name  : PasswordServiceImplTest
 * Description : Tests BCrypt password encoding and verification.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */
class PasswordServiceImplTest {

    private PasswordServiceImpl passwordService;

    @BeforeEach
    void setUp() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        passwordService = new PasswordServiceImpl(encoder);
    }

    @Test
    void shouldEncodePasswordSuccessfully() {

        String rawPassword = "Password123";

        String encodedPassword =
                passwordService.encodePassword(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
    }

    @Test
    void shouldMatchCorrectPassword() {

        String rawPassword = "Password123";

        String encodedPassword =
                passwordService.encodePassword(rawPassword);

        assertTrue(
                passwordService.matchesPassword(
                        rawPassword, encodedPassword
                )
        );
    }

    @Test
    void shouldRejectIncorrectPassword() {

        String encodedPassword =
                passwordService.encodePassword("Password123");

        assertFalse(
                passwordService.matchesPassword(
                        "WrongPassword", encodedPassword
                )
        );
    }

    @Test
    void shouldGenerateDifferentHashesForSamePassword() {

        String firstHash =
                passwordService.encodePassword("Password123");

        String secondHash =
                passwordService.encodePassword("Password123");

        assertNotEquals(firstHash, secondHash);

        assertTrue(passwordService.matchesPassword(
                "Password123", firstHash
        ));

        assertTrue(passwordService.matchesPassword(
                "Password123", secondHash
        ));
    }
}
