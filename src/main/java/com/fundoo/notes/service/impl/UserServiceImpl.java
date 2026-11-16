
package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.request.LoginRequestDto;
import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.LoginResponseDto;
import com.fundoo.notes.dto.response.UserResponseDto;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.event.UserRegistrationEvent;
import com.fundoo.notes.exception.DuplicateEmailException;
import com.fundoo.notes.exception.InvalidCredentialsException;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.PasswordService;
import com.fundoo.notes.service.TokenService;
import com.fundoo.notes.service.UserRegistrationEventPublisher;
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

    private static final Logger logger =
            LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final UserRegistrationEventPublisher eventPublisher;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordService passwordService,
            TokenService tokenService,
            UserRegistrationEventPublisher eventPublisher) {

        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public UserResponseDto register(UserRegisterRequestDto requestDto) {

        logger.info("User registration started for email: {}",
                requestDto.getEmail());

        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            logger.warn("Registration failed: email already exists: {}",
                    requestDto.getEmail());
            throw new DuplicateEmailException("Email is already registered");
        }

        User user = new User();
        user.setFirstName(requestDto.getFirstName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(
                passwordService.encodePassword(requestDto.getPassword())
        );

        User savedUser = userRepository.save(user);

        logger.info("User registered successfully. User ID: {}",
                savedUser.getId());

        UserRegistrationEvent event = new UserRegistrationEvent(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getEmail()
        );

        eventPublisher.publish(event);

        logger.info("User registration event published for user ID: {}",
                savedUser.getId());

        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getEmail()
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto requestDto) {

        logger.info("Login attempt received for email: {}",
                requestDto.getEmail());

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed: account not found for email: {}",
                            requestDto.getEmail());
                    return new InvalidCredentialsException(
                            "Invalid email or password");
                });

        boolean passwordMatches = passwordService.matchesPassword(
                requestDto.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            logger.warn("Login failed: invalid password for email: {}",
                    requestDto.getEmail());
            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }

        String token = tokenService.generateToken(user.getEmail());

        logger.info("Login successful for user ID: {}", user.getId());

        return new LoginResponseDto(token, "Login successful");
    }
}
