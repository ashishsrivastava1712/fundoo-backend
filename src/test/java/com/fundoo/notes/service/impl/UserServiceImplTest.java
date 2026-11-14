
package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.UserResponseDto;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.repository.UserRepository;

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
 * Description : Unit tests for user registration business logic,
 *               including successful registration and duplicate email.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    // Verifies that a new user can be registered successfully.
    @Test
    void shouldRegisterUserSuccessfully() {

        UserRegisterRequestDto request = new UserRegisterRequestDto();
        request.setFirstName("Ashish");
        request.setEmail("ashish@example.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail("ashish@example.com"))
                .thenReturn(Optional.empty());

        User savedUser = new User(
                "Ashish",
                "ashish@example.com",
                "Password123"
        );
        savedUser.setId(1L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto response = userService.register(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Ashish", response.getFirstName());
        assertEquals("ashish@example.com", response.getEmail());

        verify(userRepository).save(any(User.class));
    }

    // Verifies that duplicate email registration is rejected.
    @Test
    void shouldRejectDuplicateEmail() {

        UserRegisterRequestDto request = new UserRegisterRequestDto();
        request.setFirstName("Ashish");
        request.setEmail("existing@example.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    // Verifies that the repository is checked using the submitted email.
    @Test
    void shouldCheckEmailBeforeSavingUser() {

        UserRegisterRequestDto request = new UserRegisterRequestDto();
        request.setFirstName("Test");
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        User savedUser = new User(
                "Test",
                "test@example.com",
                "Password123"
        );
        savedUser.setId(2L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        userService.register(request);

        verify(userRepository).findByEmail("test@example.com");
    }
}
