
/*
 * Class Name  : GlobalExceptionHandlerTest
 * Description : Tests centralized exception handling for duplicate
 *               email, invalid credentials, validation failures
 *               and unexpected exceptions.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

package com.fundoo.notes.exception;

import com.fundoo.notes.dto.response.ApiErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        // Initialize handler and mock HTTP request.
        exceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/users/register");
    }

    @Test
    void shouldHandleDuplicateEmailException() {

        DuplicateEmailException exception =
                new DuplicateEmailException(
                        "Email is already registered"
                );

        ResponseEntity<ApiErrorResponseDto> response =
                exceptionHandler.handleDuplicateEmail(
                        exception, request
                );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals(
                "Email is already registered",
                response.getBody().getMessage()
        );
        assertEquals(
                "/api/users/register",
                response.getBody().getPath()
        );
    }

    @Test
    void shouldHandleInvalidCredentialsException() {

        InvalidCredentialsException exception =
                new InvalidCredentialsException(
                        "Invalid email or password"
                );

        ResponseEntity<ApiErrorResponseDto> response =
                exceptionHandler.handleInvalidCredentials(
                        exception, request
                );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals(
                "Invalid email or password",
                response.getBody().getMessage()
        );
    }

    @Test
    void shouldHandleValidationException() {

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError(
                "userRegisterRequestDto",
                "email",
                "Enter a valid email address"
        );

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError));

        ResponseEntity<ApiErrorResponseDto> response =
                exceptionHandler.handleValidationErrors(
                        exception, request
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals(
                "Validation failed",
                response.getBody().getMessage()
        );
        assertEquals(
                "Enter a valid email address",
                response.getBody()
                        .getValidationErrors()
                        .get("email")
        );
    }

    @Test
    void shouldHandleUnexpectedException() {

        Exception exception =
                new RuntimeException("Internal failure");

        ResponseEntity<ApiErrorResponseDto> response =
                exceptionHandler.handleGenericException(
                        exception, request
                );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals(
                "An unexpected error occurred",
                response.getBody().getMessage()
        );
    }
}
