
package com.fundoo.notes.service;

/*
 * Class Name  : TokenService
 * Description : Defines operations for generating, validating
 *               and extracting information from JWT tokens.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */
public interface TokenService {

    // Generates a signed token for the supplied user identity.
    String generateToken(String subject);

    // Extracts the user identity from a verified token.
    String extractSubject(String token);

    // Checks token signature and expiration.
    boolean isTokenValid(String token);
}
