
package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.request.LoginRequestDto;
import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.LoginResponseDto;
import com.fundoo.notes.dto.response.UserResponseDto;
import com.fundoo.notes.entity.User;
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

/*
 * Class Name  : UserServiceImplTest
 * Description : Tests registration and login business logic,
 *               including password encoding and token generation.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
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

    @Test
    void shouldRegisterUserWithEncodedPassword() {

        UserRegisterRequestDto request = new UserRegisterRequestDto();
        request.setFirstName("Ashish");
        request.setEmail("ashish@example.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(passwordService.encodePassword("Password123"))
                .thenReturn("$2a$10$encodedPasswordHash");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("Ashish");
        savedUser.setEmail("ashish@example.com");
        savedUser.setPassword("$2a$10$encodedPasswordHash");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserResponseDto response = userService.register(request);

        assertEquals(1L, response.getId());
        assertEquals("Ashish", response.getFirstName());
        assertEquals("ashish@example.com", response.getEmail());

        verify(passwordService).encodePassword("Password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmail() {

        UserRegisterRequestDto request = new UserRegisterRequestDto();
        request.setFirstName("Ashish");
        request.setEmail("ashish@example.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new User()));

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(request)
        );

        verify(passwordService, never()).encodePassword(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

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

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordService.matchesPassword(
                "Password123", "$2a$10$encodedPasswordHash"))
                .thenReturn(true);

        when(tokenService.generateToken("ashish@example.com"))
                .thenReturn("sample.jwt.token");

        LoginResponseDto response = userService.login(request);

        assertEquals("sample.jwt.token", response.getToken());
        assertEquals("Login successful", response.getMessage());

        verify(passwordService).matchesPassword(
                "Password123", "$2a$10$encodedPasswordHash"
        );

        verify(tokenService).generateToken("ashish@example.com");
    }

    @Test
    void shouldRejectLoginForUnknownEmail() {

        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("unknown@example.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.login(request)
        );

        verify(passwordService, never())
                .matchesPassword(anyString(), anyString());

        verify(tokenService, never()).generateToken(anyString());
    }

    @Test
    void shouldRejectLoginForIncorrectPassword() {

        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("ashish@example.com");
        request.setPassword("WrongPassword");

        User user = new User();
        user.setEmail("ashish@example.com");
        user.setPassword("$2a$10$encodedPasswordHash");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordService.matchesPassword(
                "WrongPassword", "$2a$10$encodedPasswordHash"))
                .thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.login(request)
        );

        verify(tokenService, never()).generateToken(anyString());
    }
}
