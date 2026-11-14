
package com.fundoo.notes.controller;

import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.UserResponseDto;
import com.fundoo.notes.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * Class Name  : UserController
 * Description : Exposes REST endpoints for user management
 *               and delegates business operations to UserService.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor injection provides the user service dependency.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Accepts registration details and returns HTTP 201 on success.
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto requestDto) {

        UserResponseDto response = userService.register(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
