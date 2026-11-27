package com.fundoo.notes.service.impl;

import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.InvalidCredentialsException;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.RedisService;
import com.fundoo.notes.service.TokenService;

import org.springframework.stereotype.Component;

/*
 * Class Name  : AuthenticatedUserResolver
 * Description : Validates the JWT from the Authorization header,
 *               rejects logged-out tokens and resolves the user.
 * Author      : Ashish Srivastava
 * Date        : 27/11/2026
 */

@Component
public class AuthenticatedUserResolver {

    public static final String BLACKLIST_PREFIX = "BLACKLIST:";

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RedisService redisService;

    public AuthenticatedUserResolver(
            TokenService tokenService,
            UserRepository userRepository,
            RedisService redisService) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.redisService = redisService;
    }

    // Extracts the raw JWT from a "Bearer" header.
    public String extractJwt(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new InvalidCredentialsException(
                    "Authorization token is required");
        }
        return authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7).trim()
                : authorizationHeader.trim();
    }

    // Resolves the authenticated user from the Authorization header.
    public User resolve(String authorizationHeader) {
        String jwt = extractJwt(authorizationHeader);

        if (!tokenService.isTokenValid(jwt)
                || redisService.exists(BLACKLIST_PREFIX + jwt)) {
            throw new InvalidCredentialsException("Invalid or expired token");
        }

        String email = tokenService.extractSubject(jwt);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "User associated with token not found"));
    }
}
