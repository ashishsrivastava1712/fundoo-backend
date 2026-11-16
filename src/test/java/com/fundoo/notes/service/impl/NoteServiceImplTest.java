
package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.request.NoteRequestDto;
import com.fundoo.notes.dto.response.NoteResponseDto;
import com.fundoo.notes.entity.Note;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.InvalidCredentialsException;
import com.fundoo.notes.repository.NoteRepository;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.TokenService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.fundoo.notes.exception.NoteNotFoundException;

/*
 * Class Name  : NoteServiceImplTest
 * Description : Tests note creation and retrieval business logic,
 *               including JWT validation and user identification.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private NoteServiceImpl noteService;

    /*
     * Test 1:
     * Verify that a valid user can create a note.
     */
    @Test
    void shouldCreateNoteSuccessfully() {

        String token = "sample.jwt.token";

        User user = new User();
        user.setId(1L);
        user.setEmail("ashish@example.com");

        NoteRequestDto request = new NoteRequestDto();
        request.setTitle("My First Note");
        request.setDescription("Testing note creation");

        when(tokenService.isTokenValid(token)).thenReturn(true);
        when(tokenService.extractSubject(token))
                .thenReturn("ashish@example.com");

        when(userRepository.findByEmail("ashish@example.com"))
                .thenReturn(Optional.of(user));

        // Return the saved note with a generated ID.
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> {
            Note note = invocation.getArgument(0);
            note.setId(10L);
            return note;
        });

        NoteResponseDto response = noteService.createNote(request, token);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("My First Note", response.getTitle());
        assertEquals("Testing note creation", response.getDescription());
        assertFalse(response.isPinned());
        assertFalse(response.isArchived());
        assertFalse(response.isTrashed());

        // Verify that the note belongs to the authenticated user.
        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(captor.capture());

        assertEquals(1L, captor.getValue().getUser().getId());
    }

    /*
     * Test 2:
     * Verify that an invalid JWT is rejected.
     */
    @Test
    void shouldRejectNoteCreationWithInvalidToken() {

        NoteRequestDto request = new NoteRequestDto();
        request.setTitle("Test Note");
        request.setDescription("Invalid token test");

        when(tokenService.isTokenValid("invalid.jwt"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> noteService.createNote(request, "invalid.jwt")
        );

        verify(noteRepository, never()).save(any(Note.class));
    }

    /*
     * Test 3:
     * Verify that a missing token is rejected.
     */
    @Test
    void shouldRejectNoteCreationWithMissingToken() {

        NoteRequestDto request = new NoteRequestDto();
        request.setTitle("Test Note");

        assertThrows(
                InvalidCredentialsException.class,
                () -> noteService.createNote(request, null)
        );

        verify(noteRepository, never()).save(any(Note.class));
    }

    /*
     * Test 4:
     * Verify that a valid token is rejected when
     * its associated email does not exist.
     */
    @Test
    void shouldRejectTokenWhenUserDoesNotExist() {

        NoteRequestDto request = new NoteRequestDto();
        request.setTitle("Test Note");

        when(tokenService.isTokenValid("sample.jwt.token"))
                .thenReturn(true);

        when(tokenService.extractSubject("sample.jwt.token"))
                .thenReturn("unknown@example.com");

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> noteService.createNote(request, "sample.jwt.token")
        );

        verify(noteRepository, never()).save(any(Note.class));
    }

    /*
     * Test 5:
     * Verify that notes are retrieved for the authenticated user.
     */
    @Test
    void shouldGetAllNotesForAuthenticatedUser() {

        String token = "sample.jwt.token";

        User user = new User();
        user.setId(1L);
        user.setEmail("ashish@example.com");

        Note firstNote = new Note();
        firstNote.setId(10L);
        firstNote.setTitle("Java Notes");
        firstNote.setDescription("Spring Boot concepts");
        firstNote.setUser(user);

        Note secondNote = new Note();
        secondNote.setId(11L);
        secondNote.setTitle("DBMS Notes");
        secondNote.setDescription("SQL concepts");
        secondNote.setUser(user);

        when(tokenService.isTokenValid(token)).thenReturn(true);

        when(tokenService.extractSubject(token))
                .thenReturn("ashish@example.com");

        when(userRepository.findByEmail("ashish@example.com"))
                .thenReturn(Optional.of(user));

        when(noteRepository.findByUserId(1L))
                .thenReturn(List.of(firstNote, secondNote));

        List<NoteResponseDto> response = noteService.getAllNotes(token);

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals("Java Notes", response.get(0).getTitle());
        assertEquals("DBMS Notes", response.get(1).getTitle());

        verify(noteRepository).findByUserId(1L);
    }

    /*
     * Test 6:
     * Verify that an authenticated user with no notes
     * receives an empty list.
     */
    @Test
    void shouldReturnEmptyListWhenUserHasNoNotes() {

        String token = "sample.jwt.token";

        User user = new User();
        user.setId(1L);
        user.setEmail("ashish@example.com");

        when(tokenService.isTokenValid(token)).thenReturn(true);

        when(tokenService.extractSubject(token))
                .thenReturn("ashish@example.com");

        when(userRepository.findByEmail("ashish@example.com"))
                .thenReturn(Optional.of(user));

        when(noteRepository.findByUserId(1L))
                .thenReturn(List.of());

        List<NoteResponseDto> response = noteService.getAllNotes(token);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
    /*
     * Helper method:
     * Prepares an authenticated user and an existing note
     * for testing note state operations.
     */
    private Note prepareNoteForStateTest(Long noteId) {

        String token = "sample.jwt.token";

        User user = new User();
        user.setId(1L);
        user.setEmail("ashish@example.com");

        Note note = new Note();
        note.setId(noteId);
        note.setTitle("Testing Note");
        note.setDescription("Testing note state changes");
        note.setUser(user);

        when(tokenService.isTokenValid(token)).thenReturn(true);
        when(tokenService.extractSubject(token))
                .thenReturn("ashish@example.com");

        when(userRepository.findByEmail("ashish@example.com"))
                .thenReturn(Optional.of(user));

        when(noteRepository.findById(noteId))
                .thenReturn(Optional.of(note));

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        return note;
    }

    /*
     * Test 7:
     * Verify that pinning a note changes pinned to true.
     */
    @Test
    void shouldPinNoteSuccessfully() {

        Note note = prepareNoteForStateTest(10L);

        NoteResponseDto response =
                noteService.togglePin(10L, "sample.jwt.token");

        assertTrue(response.isPinned());
        verify(noteRepository).save(note);
    }

    /*
     * Test 8:
     * Verify that pinning an already pinned note
     * changes pinned back to false.
     */
    @Test
    void shouldUnpinNoteSuccessfully() {

        Note note = prepareNoteForStateTest(10L);
        note.setPinned(true);

        NoteResponseDto response =
                noteService.togglePin(10L, "sample.jwt.token");

        assertFalse(response.isPinned());
        verify(noteRepository).save(note);
    }

    /*
     * Test 9:
     * Verify that archiving a note changes archived to true.
     */
    @Test
    void shouldArchiveNoteSuccessfully() {

        Note note = prepareNoteForStateTest(10L);

        NoteResponseDto response =
                noteService.toggleArchive(10L, "sample.jwt.token");

        assertTrue(response.isArchived());
        verify(noteRepository).save(note);
    }

    /*
     * Test 10:
     * Verify that archiving an already archived note
     * changes archived back to false.
     */
    @Test
    void shouldUnarchiveNoteSuccessfully() {

        Note note = prepareNoteForStateTest(10L);
        note.setArchived(true);

        NoteResponseDto response =
                noteService.toggleArchive(10L, "sample.jwt.token");

        assertFalse(response.isArchived());
        verify(noteRepository).save(note);
    }

    /*
     * Test 11:
     * Verify that trashing a note changes trashed to true.
     */
    @Test
    void shouldTrashNoteSuccessfully() {

        Note note = prepareNoteForStateTest(10L);

        NoteResponseDto response =
                noteService.toggleTrash(10L, "sample.jwt.token");

        assertTrue(response.isTrashed());
        verify(noteRepository).save(note);
    }

    /*
     * Test 12:
     * Verify that restoring a trashed note
     * changes trashed back to false.
     */
    @Test
    void shouldRestoreNoteSuccessfully() {

        Note note = prepareNoteForStateTest(10L);
        note.setTrashed(true);

        NoteResponseDto response =
                noteService.toggleTrash(10L, "sample.jwt.token");

        assertFalse(response.isTrashed());
        verify(noteRepository).save(note);
    }

    /*
     * Test 13:
     * Verify that an exception is thrown when
     * the requested note does not exist.
     */
    @Test
    void shouldThrowExceptionWhenNoteDoesNotExist() {

        User user = new User();
        user.setId(1L);
        user.setEmail("ashish@example.com");

        when(tokenService.isTokenValid("sample.jwt.token"))
                .thenReturn(true);

        when(tokenService.extractSubject("sample.jwt.token"))
                .thenReturn("ashish@example.com");

        when(userRepository.findByEmail("ashish@example.com"))
                .thenReturn(Optional.of(user));

        when(noteRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoteNotFoundException.class,
                () -> noteService.togglePin(100L, "sample.jwt.token")
        );

        verify(noteRepository, never()).save(any(Note.class));
    }

    /*
     * Test 14:
     * Verify that a user cannot modify another user's note.
     */
    @Test
    void shouldRejectModificationOfAnotherUsersNote() {

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setEmail("ashish@example.com");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");

        Note anotherUsersNote = new Note();
        anotherUsersNote.setId(10L);
        anotherUsersNote.setTitle("Private Note");
        anotherUsersNote.setUser(anotherUser);

        when(tokenService.isTokenValid("sample.jwt.token"))
                .thenReturn(true);

        when(tokenService.extractSubject("sample.jwt.token"))
                .thenReturn("ashish@example.com");

        when(userRepository.findByEmail("ashish@example.com"))
                .thenReturn(Optional.of(authenticatedUser));

        when(noteRepository.findById(10L))
                .thenReturn(Optional.of(anotherUsersNote));

        assertThrows(
                NoteNotFoundException.class,
                () -> noteService.togglePin(10L, "sample.jwt.token")
        );

        verify(noteRepository, never()).save(any(Note.class));
    }

    /*
     * Test 15:
     * Verify that note modification is rejected
     * when the JWT is invalid.
     */
    @Test
    void shouldRejectNoteModificationWithInvalidToken() {

        when(tokenService.isTokenValid("invalid.jwt"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> noteService.togglePin(10L, "invalid.jwt")
        );

        verify(noteRepository, never()).findById(anyLong());
        verify(noteRepository, never()).save(any(Note.class));
    }
}
