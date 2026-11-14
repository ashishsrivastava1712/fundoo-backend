
/*
 * Class Name : UserRepositoryTest
 *
 * Description:
 * This test class verifies the functionality of UserRepository.
 * It checks whether users can be saved, searched using email,
 * and retrieved using their unique ID.
 *
 * The tests use Spring Boot and JPA integration to interact
 * with the configured database.
 *
 * Author : Ashish Srivastava
 * Date   : 14/11/2026
 */

package com.fundoo.notes.repository;

import com.fundoo.notes.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    // Injecting UserRepository to perform database operations
    @Autowired
    private UserRepository userRepository;

    /*
     * Test Case 1:
     * Verifies whether a new user can be saved successfully.
     */
    @Test
    void shouldSaveUserSuccessfully() {

        String email = UUID.randomUUID() + "@gmail.com";

        User user = new User(
                "Ashish",
                email,
                "password123"
        );

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("Ashish", savedUser.getFirstName());
        assertEquals(email, savedUser.getEmail());
    }

    /*
     * Test Case 2:
     * Verifies whether a user can be retrieved using email.
     */
    @Test
    void shouldFindUserByEmail() {

        String email = UUID.randomUUID() + "@gmail.com";

        User user = new User(
                "Ashish",
                email,
                "password123"
        );

        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    /*
     * Test Case 3:
     * Verifies that an empty Optional is returned
     * when the requested email does not exist.
     */
    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {

        String email = UUID.randomUUID() + "@gmail.com";

        Optional<User> result = userRepository.findByEmail(email);

        assertTrue(result.isEmpty());
    }

    /*
     * Test Case 4:
     * Verifies whether a saved user can be retrieved
     * using the automatically generated primary key.
     */
    @Test
    void shouldFindUserById() {

        String email = UUID.randomUUID() + "@gmail.com";

        User user = new User(
                "Ashish",
                email,
                "password123"
        );

        User savedUser = userRepository.save(user);

        Optional<User> result =
                userRepository.findById(savedUser.getId());

        assertTrue(result.isPresent());
        assertEquals(savedUser.getId(), result.get().getId());
    }
}
