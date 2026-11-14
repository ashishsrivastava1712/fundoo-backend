
package com.fundoo.notes.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * Class Name  : LoginRequestDto
 * Description : Stores and validates the email and password
 *               submitted by a user during login.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

public class LoginRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    // Default constructor for DTO object creation.
    public LoginRequestDto() {
    }

    // Returns the email address.
    public String getEmail() {
        return email;
    }

    // Sets the email address.
    public void setEmail(String email) {
        this.email = email;
    }

    // Returns the password.
    public String getPassword() {
        return password;
    }

    // Sets the password.
    public void setPassword(String password) {
        this.password = password;
    }
}
