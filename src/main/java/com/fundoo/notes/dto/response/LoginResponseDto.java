
package com.fundoo.notes.dto.response;

/*
 * Class Name  : LoginResponseDto
 * Description : Represents the response returned after
 *               successful user authentication.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

public class LoginResponseDto {

    private String token;
    private String message;

    // Default constructor.
    public LoginResponseDto() {
    }

    // Initializes the login response.
    public LoginResponseDto(String token, String message) {
        this.token = token;
        this.message = message;
    }

    // Returns the authentication token.
    public String getToken() {
        return token;
    }

    // Sets the authentication token.
    public void setToken(String token) {
        this.token = token;
    }

    // Returns the response message.
    public String getMessage() {
        return message;
    }

    // Sets the response message.
    public void setMessage(String message) {
        this.message = message;
    }
}
