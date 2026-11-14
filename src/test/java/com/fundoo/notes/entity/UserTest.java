
/*
 * Class Name : UserTest
 *
 * Description:
 * This class contains unit tests for the User entity.
 * It verifies object creation, default constructor behaviour,
 * getters and setters of the User class.
 *
 * These tests do not require a database connection.
 *
 * Author : Ashish Srivastava
 * Date   : 14/11/2026
 */

package com.fundoo.notes.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    /*
     * Test Case 1:
     * Checks whether a User object is created
     * with the correct field values.
     */
    @Test
    void shouldCreateUserSuccessfully() {

        User user = new User(
                "Ashish",
                "ashish@gmail.com",
                "password123"
        );

        assertEquals("Ashish", user.getFirstName());
        assertEquals("ashish@gmail.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    /*
     * Test Case 2:
     * Checks whether getters return the values
     * assigned through setters.
     */
    @Test
    void shouldSetAndGetUserFields() {

        User user = new User();

        user.setId(1L);
        user.setFirstName("Ashish");
        user.setEmail("ashish@gmail.com");
        user.setPassword("password123");

        assertEquals(1L, user.getId());
        assertEquals("Ashish", user.getFirstName());
        assertEquals("ashish@gmail.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    /*
     * Test Case 3:
     * Checks whether the default constructor
     * initializes an empty User object.
     */
    @Test
    void shouldCreateEmptyUser() {

        User user = new User();

        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getFirstName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }
}
