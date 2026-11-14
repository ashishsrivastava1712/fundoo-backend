
/*
 * Class Name  : UserServiceImplTest
 * Description : Tests registration and login business logic,
 *               including password encoding, duplicate email
 *               validation and JWT token generation.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Mockito-based unit tests for UserServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserServiceImpl userService;

    /*
     * Test 1:
     * Verify that registration saves the user
     * with an encoded password.
     */
    @Test
    void shouldRegisterUserWithEncodedPassword() {

        UserRegisterRequestDto request = new UserRegisterRequestDto();
        request.setFirstName("Ashish");
        request.setEmail("ashish@example.com");
        request.setPassword("Password123");

        // Mock email lookup to indicate that the email is new.
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        // Mock BCrypt password encoding.
        when(passwordService.encodePassword("Password123"))
                .thenReturn("$2a$10$encodedPasswordHash");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("Ashish");
        savedUser.setEmail("ashish@example.com");
        savedUser.setPassword("$2a$10$encodedPasswordHash");

        // Mock database save operation.
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserResponseDto response = userService.register(request);

        // Verify registration response.
        assertEquals(1L, response.getId());
        assertEquals("Ashish", response.getFirstName());
        assertEquals("ashish@example.com", response.getEmail());

        // Verify password encoding and repository save.
        verify(passwordService).encodePassword("Password123");
        verify(userRepository).save(any(User.class));
    }

    /*
     * Test 2:
     * Verify that registration rejects an existing email.
     */
    @Test
    void shouldRejectDuplicateEmail() {

        UserRegisterRequestDto request = new UserRegisterRequestDto();
        request.setFirstName("Ashish");
        request.setEmail("ashish@example.com");
        request.setPassword("Password123");

        // Mock an existing user with the same email.
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new User()));

        // Verify that the custom exception is thrown.
        assertThrows(
                DuplicateEmailException.class,
                () -> userService.register(request)
        );

        // Password encoding and saving must not happen.
        verify(passwordService, never()).encodePassword(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    /*
     * Test 3:
     * Verify successful login with correct credentials.
     */
    @Test
    void shouldLoginSuccessfullyWithCorrectPassword() {

        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("ashish@example.com");
        request.setPassword("Password123");

        User user = new User();
        user.setId(1L);
        user.setFirstName("Ashish");
        user.setEmail("ashish@example.com");
        user.setPassword("$2a$10$encodedPasswordHash");

        // Mock user lookup.
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        // Mock successful password verification.
        when(passwordService.matchesPassword(
                "Password123",
                "$2a$10$encodedPasswordHash"
        )).thenReturn(true);

        // Mock JWT generation.
        when(tokenService.generateToken("ashish@example.com"))
                .thenReturn("sample.jwt.token");

        LoginResponseDto response = userService.login(request);

        // Verify login response.
        assertEquals("sample.jwt.token", response.getToken());
        assertEquals("Login successful", response.getMessage());

        // Verify password verification and token generation.
        verify(passwordService).matchesPassword(
                "Password123",
                "$2a$10$encodedPasswordHash"
        );

        verify(tokenService).generateToken("ashish@example.com");
    }

    /*
     * Test 4:
     * Verify that login rejects an unknown email address.
     */
    @Test
    void shouldRejectLoginForUnknownEmail() {

        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("unknown@example.com");
        request.setPassword("Password123");

        // Mock an email lookup that returns no user.
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        // Verify the custom invalid credentials exception.
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        // Password verification and token generation must not happen.
        verify(passwordService, never())
                .matchesPassword(anyString(), anyString());

        verify(tokenService, never()).generateToken(anyString());
    }

    /*
     * Test 5:
     * Verify that login rejects an incorrect password.
     */
    @Test
    void shouldRejectLoginForIncorrectPassword() {

        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("ashish@example.com");
        request.setPassword("WrongPassword");

        User user = new User();
        user.setEmail("ashish@example.com");
        user.setPassword("$2a$10$encodedPasswordHash");

        // Mock successful user lookup.
        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        // Mock failed password verification.
        when(passwordService.matchesPassword(
                "WrongPassword",
                "$2a$10$encodedPasswordHash"
        )).thenReturn(false);

        // Verify the custom invalid credentials exception.
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        // Token must never be generated for invalid credentials.
        verify(tokenService, never()).generateToken(anyString());
    }
}
