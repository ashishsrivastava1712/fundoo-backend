package com.fundoo.notes;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*
 * Class Name  : PasswordHashGeneratorTest
 * Description : Generates a BCrypt hash for resetting
 *               a test user's password.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */
class PasswordHashGeneratorTest {

    @Test
    void generatePasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash = encoder.encode("Fundoo@123");

        System.out.println("GENERATED HASH: " + hash);
    }
}