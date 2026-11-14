
package com.fundoo.notes.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Class Name  : ResponseDtoTest
 * Description : Tests registration and login response DTO
 *               constructors, getters, and setters.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

class ResponseDtoTest {

    // Verifies that registration response fields are initialized.
    @Test
    void shouldCreateUserResponseSuccessfully() {
        UserResponseDto dto = new UserResponseDto(
                1L,
                "Ashish",
                "ashish@example.com"
        );

        assertEquals(1L, dto.getId());
        assertEquals("Ashish", dto.getFirstName());
        assertEquals("ashish@example.com", dto.getEmail());
    }

    // Verifies that registration response setters update fields.
    @Test
    void shouldUpdateUserResponseFields() {
        UserResponseDto dto = new UserResponseDto();

        dto.setId(2L);
        dto.setFirstName("Test User");
        dto.setEmail("test@example.com");

        assertEquals(2L, dto.getId());
        assertEquals("Test User", dto.getFirstName());
        assertEquals("test@example.com", dto.getEmail());
    }

    // Verifies that login response fields are initialized.
    @Test
    void shouldCreateLoginResponseSuccessfully() {
        LoginResponseDto dto = new LoginResponseDto(
                "sample-token",
                "Login successful"
        );

        assertEquals("sample-token", dto.getToken());
        assertEquals("Login successful", dto.getMessage());
    }

    // Verifies that login response setters update fields.
    @Test
    void shouldUpdateLoginResponseFields() {
        LoginResponseDto dto = new LoginResponseDto();

        dto.setToken("new-token");
        dto.setMessage("Authentication successful");

        assertEquals("new-token", dto.getToken());
        assertEquals("Authentication successful", dto.getMessage());
    }
}
