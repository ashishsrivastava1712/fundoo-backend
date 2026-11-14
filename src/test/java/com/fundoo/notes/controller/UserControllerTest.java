
package com.fundoo.notes.controller;

import com.fundoo.notes.dto.request.LoginRequestDto;
import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.LoginResponseDto;
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
 * Description : Tests REST controller responses for registration
 *               and login operations.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldReturnCreatedWhenRegistrationSucceeds() {

        UserRegisterRequestDto request = new UserRegisterRequestDto();

        UserResponseDto response =
                new UserResponseDto(
                        1L, "Ashish", "ashish@example.com"
                );

        when(userService.register(request)).thenReturn(response);

        ResponseEntity<UserResponseDto> result =
                userController.register(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
    }

    @Test
    void shouldReturnOkWhenLoginSucceeds() {

        LoginRequestDto request = new LoginRequestDto();

        LoginResponseDto response =
                new LoginResponseDto(
                        "sample.jwt.token", "Login successful"
                );

        when(userService.login(request)).thenReturn(response);

        ResponseEntity<LoginResponseDto> result =
                userController.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("sample.jwt.token", result.getBody().getToken());
    }
}
