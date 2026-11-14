
package com.fundoo.notes.service;

import com.fundoo.notes.dto.request.LoginRequestDto;
import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.LoginResponseDto;
import com.fundoo.notes.dto.response.UserResponseDto;

/*
 * Class Name  : UserService
 * Description : Defines business operations for user registration
 *               and login.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */
public interface UserService {

    // Registers a user and returns safe user details.
    UserResponseDto register(UserRegisterRequestDto requestDto);

    // Authenticates a user and returns a JWT.
    LoginResponseDto login(LoginRequestDto requestDto);
}
