
package com.fundoo.notes.service.impl;

import com.fundoo.notes.service.PasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/*
 * Class Name  : PasswordServiceImpl
 * Description : Implements password hashing and verification
 *               using Spring Security's BCrypt encoder.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */
@Service
public class PasswordServiceImpl implements PasswordService {

    private final PasswordEncoder passwordEncoder;

    // Constructor injection of the configured BCrypt encoder.
    public PasswordServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matchesPassword(
            String rawPassword, String encodedPassword) {

        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
