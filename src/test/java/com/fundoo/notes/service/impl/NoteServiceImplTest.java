
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
}
