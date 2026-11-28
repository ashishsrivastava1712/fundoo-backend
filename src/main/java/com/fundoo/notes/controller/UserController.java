package com.fundoo.notes.controller;

import com.fundoo.notes.dto.request.ForgotPasswordRequestDto;
import com.fundoo.notes.dto.request.LoginRequestDto;
import com.fundoo.notes.dto.request.ResetPasswordRequestDto;
import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.LoginResponseDto;
import com.fundoo.notes.dto.response.UserResponseDto;
import com.fundoo.notes.service.AccountService;
import com.fundoo.notes.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
 * Class Name  : UserController
 * Description : Exposes registration, login, profile, logout and
 *               password recovery endpoints.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AccountService accountService;

    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    // Registers a new user and returns HTTP 201.
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.register(requestDto));
    }

    // Authenticates the user and returns a JWT.
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(userService.login(requestDto));
    }

    // Verifies email address using verification token.
    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @RequestParam("token") String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully! You can now log in."));
    }

    // Returns the authenticated user's profile.
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(accountService.getCurrentUser(token));
    }

    // Invalidates the current token.
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String token) {
        accountService.logout(token);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // Starts password recovery by generating an OTP.
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto requestDto) {
        accountService.forgotPassword(requestDto.getEmail());
        return ResponseEntity.ok(Map.of("message",
                "If the email is registered, a reset OTP has been generated"));
    }

    // Resets the password using a valid OTP.
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto requestDto) {
        accountService.resetPassword(requestDto);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }
}
