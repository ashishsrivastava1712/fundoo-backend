
package com.fundoo.notes.service;

/*
 * Class Name  : PasswordService
 * Description : Defines password encoding and verification
 *               operations for the application.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */
public interface PasswordService {

    // Converts a raw password into a BCrypt hash.
    String encodePassword(String rawPassword);

    // Checks whether a raw password matches its stored hash.
    boolean matchesPassword(String rawPassword, String encodedPassword);
}
