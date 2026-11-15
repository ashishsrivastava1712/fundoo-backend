
/*
 * Class Name  : NoteTest
 * Description : Tests Note entity constructors, getters, setters
 *               and default lifecycle state values.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

package com.fundoo.notes.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    // Test 1: Verify the parameterized constructor.
    @Test
    void shouldCreateNoteSuccessfully() {

        User user = new User();
        user.setId(1L);

        Note note = new Note(
                "Training Notes",
                "Spring Boot concepts",
                user
        );

        assertEquals("Training Notes", note.getTitle());
        assertEquals("Spring Boot concepts", note.getDescription());
        assertEquals(user, note.getUser());

        assertFalse(note.isPinned());
        assertFalse(note.isArchived());
        assertFalse(note.isTrashed());
    }

    // Test 2: Verify getters and setters.
    @Test
    void shouldSetAndGetNoteFields() {

        Note note = new Note();

        User user = new User();
        user.setId(1L);

        note.setId(10L);
        note.setTitle("Java Notes");
        note.setDescription("Java programming");
        note.setPinned(true);
        note.setArchived(true);
        note.setTrashed(false);
        note.setUser(user);

        assertEquals(10L, note.getId());
        assertEquals("Java Notes", note.getTitle());
        assertEquals("Java programming", note.getDescription());
        assertTrue(note.isPinned());
        assertTrue(note.isArchived());
        assertFalse(note.isTrashed());
        assertEquals(user, note.getUser());
    }

    // Test 3: Verify the default constructor.
    @Test
    void shouldCreateEmptyNote() {

        Note note = new Note();

        assertNull(note.getId());
        assertNull(note.getTitle());
        assertNull(note.getDescription());
        assertNull(note.getUser());

        assertFalse(note.isPinned());
        assertFalse(note.isArchived());
        assertFalse(note.isTrashed());
    }
}
