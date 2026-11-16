
package com.fundoo.notes.controller;

import com.fundoo.notes.dto.request.NoteRequestDto;
import com.fundoo.notes.dto.response.NoteResponseDto;
import com.fundoo.notes.service.NoteService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Class Name  : NoteController
 * Description : Exposes REST endpoints for creating
 *               and retrieving user notes.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    // Constructor injection of NoteService.
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // Creates a note for the authenticated user.
    @PostMapping
    public ResponseEntity<NoteResponseDto> createNote(
            @Valid @RequestBody NoteRequestDto requestDto,
            @RequestHeader("Authorization") String token) {

        NoteResponseDto response = noteService.createNote(requestDto, token);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Retrieves all notes belonging to the authenticated user.
    @GetMapping
    public ResponseEntity<List<NoteResponseDto>> getAllNotes(
            @RequestHeader("Authorization") String token) {

        List<NoteResponseDto> response = noteService.getAllNotes(token);

        return ResponseEntity.ok(response);
    }
}
