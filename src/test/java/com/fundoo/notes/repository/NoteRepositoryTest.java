
/*
 * Class Name  : NoteRepositoryTest
 * Description : Tests NoteRepository database operations,
 *               user-specific retrieval and note searching.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

package com.fundoo.notes.repository;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.fundoo.notes.entity.Note;
import com.fundoo.notes.entity.User;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    // Creates and persists a user for repository tests.
    private User createTestUser() {

        User user = new User();

        user.setFirstName("Ashish");
        user.setEmail(
                "ashish-" + UUID.randomUUID() + "@example.com"
        );
        user.setPassword("testPassword");

        return userRepository.save(user);
    }

    // Test 1: Verify that a note can be saved.
    @Test
    void shouldSaveNoteSuccessfully() {

        User user = createTestUser();

        Note note = new Note(
                "Spring Boot",
                "Repository concepts",
                user
        );

        Note savedNote = noteRepository.save(note);

        assertNotNull(savedNote.getId());
        assertEquals("Spring Boot", savedNote.getTitle());
        assertEquals("Repository concepts", savedNote.getDescription());
    }

    // Test 2: Retrieve all notes belonging to a user.
    @Test
    void shouldFindNotesByUserId() {

        User user = createTestUser();

        Note note1 = new Note("Java", "Java concepts", user);
        Note note2 = new Note("Spring", "Spring concepts", user);

        noteRepository.save(note1);
        noteRepository.save(note2);

        List<Note> notes =
                noteRepository.findByUserId(user.getId());

        assertEquals(2, notes.size());
    }

    // Test 3: Retrieve only active notes.
    @Test
    void shouldFindActiveNotesByUserId() {

        User user = createTestUser();

        Note activeNote = new Note(
                "Active Note",
                "Visible note",
                user
        );

        Note archivedNote = new Note(
                "Archived Note",
                "Archived content",
                user
        );
        archivedNote.setArchived(true);

        Note trashedNote = new Note(
                "Trashed Note",
                "Trashed content",
                user
        );
        trashedNote.setTrashed(true);

        noteRepository.save(activeNote);
        noteRepository.save(archivedNote);
        noteRepository.save(trashedNote);

        List<Note> notes =
                noteRepository.findByUserIdAndArchivedFalseAndTrashedFalse(
                        user.getId()
                );

        assertEquals(1, notes.size());
        assertEquals("Active Note", notes.get(0).getTitle());
    }

    // Test 4: Search notes by title without case sensitivity.
    @Test
    void shouldSearchNotesByTitleIgnoringCase() {

        User user = createTestUser();

        Note note = new Note(
                "Spring Boot Development",
                "Backend development notes",
                user
        );

        noteRepository.save(note);

        List<Note> notes =
                noteRepository.findByUserIdAndTitleContainingIgnoreCase(
                        user.getId(),
                        "spring"
                );

        assertEquals(1, notes.size());
        assertEquals(
                "Spring Boot Development",
                notes.get(0).getTitle()
        );
    }
}
