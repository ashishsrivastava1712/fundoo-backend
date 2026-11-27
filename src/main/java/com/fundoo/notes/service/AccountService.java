package com.fundoo.notes.service;

import com.fundoo.notes.dto.request.ResetPasswordRequestDto;
import com.fundoo.notes.dto.response.UserResponseDto;

/*
 * Interface Name : AccountService
 * Description    : Defines profile, logout and password recovery operations.
 * Author         : Ashish Srivastava
 * Date           : 27/11/2026
 */

public interface AccountService {

    // Returns the profile of the authenticated user.
    UserResponseDto getCurrentUser(String token);

    // Invalidates the current JWT.
    void logout(String token);

    // Generates a password-reset OTP for the email.
    void forgotPassword(String email);

    // Resets the password after OTP verification.
    void resetPassword(ResetPasswordRequestDto requestDto);
}
