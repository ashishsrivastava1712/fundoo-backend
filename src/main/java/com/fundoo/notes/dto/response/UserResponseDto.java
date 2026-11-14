
package com.fundoo.notes.dto.response;

/*
 * Class Name  : UserResponseDto
 * Description : Represents the safe user information returned
 *               by the application after registration.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

public class UserResponseDto {

    private Long id;
    private String firstName;
    private String email;

    // Default constructor.
    public UserResponseDto() {
    }

    // Initializes the registration response.
    public UserResponseDto(Long id, String firstName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.email = email;
    }

    // Returns the user ID.
    public Long getId() {
        return id;
    }

    // Sets the user ID.
    public void setId(Long id) {
        this.id = id;
    }

    // Returns the first name.
    public String getFirstName() {
        return firstName;
    }

    // Sets the first name.
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Returns the email.
    public String getEmail() {
        return email;
    }

    // Sets the email.
    public void setEmail(String email) {
        this.email = email;
    }
}
