
package com.fundoo.notes.controller;

import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.UserResponseDto;
import com.fundoo.notes.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * Class Name  : UserControllerTest
 * Description : Unit tests for the user registration REST controller,
 *               including response status and service delegation.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    // Verifies that registration returns HTTP 201 CREATED.
    @Test
    void shouldReturnCreatedWhenRegistrationSucceeds() {

        UserRegisterRequestDto request = new UserRegisterRequestDto();
        request.setFirstName("Ashish");
        request.setEmail("ashish@example.com");
        request.setPassword("Password123");

        UserResponseDto response = new UserResponseDto(
                1L,
                "Ashish",
                "ashish@example.com"
        );

        when(userService.register(request)).thenReturn(response);

        ResponseEntity<UserResponseDto> result =
                userController.register(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());

        verify(userService).register(request);
    }

    // Verifies that the response contains the expected user details.
    @Test
    void shouldReturnRegisteredUserDetails() {

        UserRegisterRequestDto request = new UserRegisterRequestDto();

        UserResponseDto response = new UserResponseDto(
                5L,
                "Test User",
                "test@example.com"
        );

        when(userService.register(request)).thenReturn(response);

        ResponseEntity<UserResponseDto> result =
                userController.register(request);

        assertEquals("Test User", result.getBody().getFirstName());
        assertEquals("test@example.com", result.getBody().getEmail());
    }
}
