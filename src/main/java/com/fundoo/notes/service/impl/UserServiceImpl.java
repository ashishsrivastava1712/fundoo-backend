
package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.request.UserRegisterRequestDto;
import com.fundoo.notes.dto.response.UserResponseDto;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.UserService;

import org.springframework.stereotype.Service;

/*
 * Class Name  : UserServiceImpl
 * Description : Implements user registration business logic,
 *               including duplicate email verification and
 *               saving user details through the repository.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // Constructor injection provides the repository dependency.
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Checks email availability, saves the user and returns response DTO.
    @Override
    public UserResponseDto register(UserRegisterRequestDto requestDto) {

        // Prevent registration with an email already in the database.
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Convert incoming registration data into a User entity.
        User user = new User();
        user.setFirstName(requestDto.getFirstName());
        user.setEmail(requestDto.getEmail());

        /*
         * TEMPORARY: Password encoding is intentionally deferred to UC-06.
         * Do not deploy or expose this registration flow before UC-06
         * replaces this assignment with PasswordEncoder.encode(...).
         */
        user.setPassword(requestDto.getPassword());

        // Persist the new user in the database.
        User savedUser = userRepository.save(user);

        // Return only safe user details; never return the password.
        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getEmail()
        );
    }
}
