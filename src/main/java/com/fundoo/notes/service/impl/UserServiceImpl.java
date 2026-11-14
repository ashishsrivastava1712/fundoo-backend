
package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.request.LoginRequestDto;
import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.LoginResponseDto;
import com.fundoo.notes.dto.response.UserResponseDto;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.PasswordService;
import com.fundoo.notes.service.TokenService;
import com.fundoo.notes.service.UserService;
import org.springframework.stereotype.Service;
import com.fundoo.notes.exception.DuplicateEmailException;
import com.fundoo.notes.exception.InvalidCredentialsException;

/*
 * Class Name  : UserServiceImpl
 * Description : Implements user registration and login,
 *               including password verification and JWT creation.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */
@Service
public class UserServiceImpl implements UserService {

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

        // Prevent registration with an already registered email.
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email is already registered");
        }

        User user = new User();
        user.setFirstName(requestDto.getFirstName());
        user.setEmail(requestDto.getEmail());

        // Store only the BCrypt hash, never the raw password.
        user.setPassword(
                passwordService.encodePassword(requestDto.getPassword())
        );

        User savedUser = userRepository.save(user);

        // Never include the password in the response DTO.
        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getEmail()
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto requestDto) {

        // Find the account using the submitted email.
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        // Compare submitted password against the stored BCrypt hash.
        boolean passwordMatches = passwordService.matchesPassword(
                requestDto.getPassword(),
                user.getPassword()
        );

        // Throw custom exception if password does not match.
        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate a token using the user's email as its subject.
        String token = tokenService.generateToken(user.getEmail());

        return new LoginResponseDto(token, "Login successful");
    }
}

