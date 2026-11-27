package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.request.ResetPasswordRequestDto;
import com.fundoo.notes.dto.response.UserResponseDto;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.InvalidCredentialsException;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.AccountService;
import com.fundoo.notes.service.OtpService;
import com.fundoo.notes.service.PasswordService;
import com.fundoo.notes.service.RedisService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/*
 * Class Name  : AccountServiceImpl
 * Description : Implements profile lookup, JWT logout via Redis blacklist
 *               and OTP-based password reset.
 * Author      : Ashish Srivastava
 * Date        : 27/11/2026
 */

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger =
            LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AuthenticatedUserResolver userResolver;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordService passwordService;
    private final RedisService redisService;

    @Value("${app.redis.token-expiration}")
    private long tokenExpirationSeconds;

    public AccountServiceImpl(
            AuthenticatedUserResolver userResolver,
            UserRepository userRepository,
            OtpService otpService,
            PasswordService passwordService,
            RedisService redisService) {
        this.userResolver = userResolver;
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.passwordService = passwordService;
        this.redisService = redisService;
    }

    @Override
    public UserResponseDto getCurrentUser(String token) {
        User user = userResolver.resolve(token);
        return new UserResponseDto(user.getId(), user.getFirstName(), user.getEmail());
    }

    @Override
    public void logout(String token) {
        userResolver.resolve(token);
        String jwt = userResolver.extractJwt(token);
        redisService.save(
                AuthenticatedUserResolver.BLACKLIST_PREFIX + jwt,
                "logged-out",
                Duration.ofSeconds(tokenExpirationSeconds)
        );
        logger.info("Token blacklisted on logout");
    }

    @Override
    public void forgotPassword(String email) {
        // Do not reveal whether the email exists.
        if (userRepository.findByEmail(email).isEmpty()) {
            logger.warn("Password reset requested for unknown email: {}", email);
            return;
        }
        String otp = otpService.generateOtp(email);
        // No SMTP server is configured; the OTP is written to the server log.
        logger.info("Password reset OTP for {} is {} (valid 5 minutes)", email, otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid or expired OTP"));

        if (!otpService.verifyOtp(requestDto.getEmail(), requestDto.getOtp())) {
            throw new InvalidCredentialsException("Invalid or expired OTP");
        }

        user.setPassword(passwordService.encodePassword(requestDto.getNewPassword()));
        userRepository.save(user);
        logger.info("Password reset successful for user ID: {}", user.getId());
    }
}
