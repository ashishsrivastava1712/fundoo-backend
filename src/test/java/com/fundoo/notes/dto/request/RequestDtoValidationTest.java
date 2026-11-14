
package com.fundoo.notes.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Class Name  : RequestDtoValidationTest
 * Description : Tests registration and login DTO validation,
 *               including blank fields and invalid email formats.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

class RequestDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    // Initializes the validation provider before running tests.
    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Releases the validation provider after all tests finish.
    @AfterAll
    static void tearDown() {
        factory.close();
    }

    // Verifies that valid registration details pass validation.
    @Test
    void shouldAcceptValidRegistrationDetails() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setFirstName("Ashish");
        dto.setEmail("ashish@example.com");
        dto.setPassword("Password123");

        assertTrue(validator.validate(dto).isEmpty());
    }

    // Verifies that a blank first name is rejected.
    @Test
    void shouldRejectBlankFirstName() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setFirstName(" ");
        dto.setEmail("ashish@example.com");
        dto.setPassword("Password123");

        assertFalse(validator.validate(dto).isEmpty());
    }

    // Verifies that an invalid email is rejected.
    @Test
    void shouldRejectInvalidRegistrationEmail() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setFirstName("Ashish");
        dto.setEmail("invalid-email");
        dto.setPassword("Password123");

        assertFalse(validator.validate(dto).isEmpty());
    }

    // Verifies that a blank registration password is rejected.
    @Test
    void shouldRejectBlankRegistrationPassword() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setFirstName("Ashish");
        dto.setEmail("ashish@example.com");
        dto.setPassword("");

        assertFalse(validator.validate(dto).isEmpty());
    }

    // Verifies that valid login details pass validation.
    @Test
    void shouldAcceptValidLoginDetails() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("ashish@example.com");
        dto.setPassword("Password123");

        assertTrue(validator.validate(dto).isEmpty());
    }

    // Verifies that an invalid login email is rejected.
    @Test
    void shouldRejectInvalidLoginEmail() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("wrong-email");
        dto.setPassword("Password123");

        assertFalse(validator.validate(dto).isEmpty());
    }

    // Verifies that a blank login password is rejected.
    @Test
    void shouldRejectBlankLoginPassword() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("ashish@example.com");
        dto.setPassword(" ");

        assertFalse(validator.validate(dto).isEmpty());
    }
}
