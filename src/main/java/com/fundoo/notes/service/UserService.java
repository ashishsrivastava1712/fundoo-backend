
package com.fundoo.notes.service;

import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.UserResponseDto;

/*
 * Class Name  : UserService
 * Description : Defines the business operations supported
 *               by the user management module.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

public interface UserService {

    // Registers a new user and returns the saved user details.
    UserResponseDto register(UserRegisterRequestDto requestDto);
}
