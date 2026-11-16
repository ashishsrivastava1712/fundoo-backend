
package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.request.LoginRequestDto;
import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.LoginResponseDto;
import com.fundoo.notes.dto.response.UserResponseDto;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.DuplicateEmailException;
import com.fundoo.notes.exception.InvalidCredentialsException;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.PasswordService;
import com.fundoo.notes.service.TokenService;
import com.fundoo.notes.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Class Name  : UserServiceImpl
 * Description : Implements user registration and login,
 *               including password verification and JWT creation.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */
@Service
public class UserServiceImpl implements UserService {

    // Logger for tracking user-related business operations.
    private static final Logger logger =
            LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    // Constructor injection of repository and security utilities.
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordService passwordService,
            TokenService tokenService) {

        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    @Override
    public UserResponseDto register(UserRegisterRequestDto requestDto) {

        logger.info("User registration process started");

        // Prevent registration with an already registered email.
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {

            logger.warn("User registration rejected: email already exists");

            throw new DuplicateEmailException(
                    "Email is already registered");
        }

        User user = new User();
        user.setFirstName(requestDto.getFirstName());
        user.setEmail(requestDto.getEmail());

        // Store only the BCrypt hash, never the raw password.
        user.setPassword(
                passwordService.encodePassword(requestDto.getPassword())
        );

        logger.debug("Password encoded successfully for new user");

        User savedUser = userRepository.save(user);

        logger.info(
                "User registration completed successfully. User ID: {}",
                savedUser.getId()
        );

        // Never include the password in the response DTO.
        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getEmail()
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto requestDto) {

        logger.info("User login process started");

        // Find the account using the submitted email.
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login rejected: user account not found");
                    return new InvalidCredentialsException(
                            "Invalid email or password");
                });

        // Compare submitted password against the stored BCrypt hash.
        boolean passwordMatches = passwordService.matchesPassword(
                requestDto.getPassword(),
                user.getPassword()
        );

        // Throw custom exception if password does not match.
        if (!passwordMatches) {

            logger.warn(
                    "Login rejected due to invalid credentials. User ID: {}",
                    user.getId()
            );

            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }

        logger.debug(
                "Password verification successful. User ID: {}",
                user.getId()
        );

        // Generate a token using the user's email as its subject.
        String token = tokenService.generateToken(user.getEmail());

        logger.info(
                "User login completed successfully. User ID: {}",
                user.getId()
        );

        return new LoginResponseDto(token, "Login successful");
    }
}
