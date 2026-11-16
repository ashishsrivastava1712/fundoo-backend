
package com.fundoo.notes.controller;

import com.fundoo.notes.dto.request.NoteRequestDto;
import com.fundoo.notes.dto.response.NoteResponseDto;
import com.fundoo.notes.service.NoteService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * Class Name  : NoteControllerTest
 * Description : Tests REST controller responses for note
 *               creation and retrieval operations.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@ExtendWith(MockitoExtension.class)
class NoteControllerTest {

    @Mock
    private NoteService noteService;

    @InjectMocks
    private NoteController noteController;

    /*
     * Test 1:
     * Verify that note creation returns HTTP 201.
     */
    @Test
    void shouldReturnCreatedWhenNoteIsCreated() {

        NoteRequestDto request = new NoteRequestDto();
        request.setTitle("Java Notes");
        request.setDescription("Spring Boot concepts");

        NoteResponseDto response = mock(NoteResponseDto.class);

        when(noteService.createNote(request, "Bearer sample.jwt.token"))
                .thenReturn(response);

        ResponseEntity<NoteResponseDto> result =
                noteController.createNote(request, "Bearer sample.jwt.token");

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertSame(response, result.getBody());

        verify(noteService).createNote(request, "Bearer sample.jwt.token");
    }

    /*
     * Test 2:
     * Verify that fetching notes returns HTTP 200.
     */
    @Test
    void shouldReturnOkWhenNotesAreFetched() {

        NoteResponseDto response = mock(NoteResponseDto.class);

        List<NoteResponseDto> notes = List.of(response);

        when(noteService.getAllNotes("Bearer sample.jwt.token"))
                .thenReturn(notes);

        ResponseEntity<List<NoteResponseDto>> result =
                noteController.getAllNotes("Bearer sample.jwt.token");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());

        verify(noteService).getAllNotes("Bearer sample.jwt.token");
    }

    /*
     * Test 3:
     * Verify that an empty note collection returns HTTP 200.
     */
    @Test
    void shouldReturnOkWhenNoNotesAreAvailable() {

        when(noteService.getAllNotes("Bearer sample.jwt.token"))
                .thenReturn(List.of());

        ResponseEntity<List<NoteResponseDto>> result =
                noteController.getAllNotes("Bearer sample.jwt.token");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
    }
}
