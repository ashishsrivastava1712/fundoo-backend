package com.fundoo.notes.controller;

import com.fundoo.notes.dto.request.NoteRequestDto;
import com.fundoo.notes.dto.response.NoteResponseDto;
import com.fundoo.notes.service.NoteService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
 * Class Name  : NoteController
 * Description : Exposes REST endpoints for note management.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // Creates a note for the authenticated user.
    @PostMapping
    public ResponseEntity<NoteResponseDto> createNote(
            @Valid @RequestBody NoteRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.createNote(requestDto, token));
    }

    // Retrieves all notes belonging to the authenticated user.
    @GetMapping
    public ResponseEntity<List<NoteResponseDto>> getAllNotes(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.getAllNotes(token));
    }

    // Retrieves a single note.
    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponseDto> getNote(
            @PathVariable("noteId") Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.getNoteById(noteId, token));
    }

    // Updates title, description and colour of a note.
    @PutMapping("/{noteId}")
    public ResponseEntity<NoteResponseDto> updateNote(
            @PathVariable("noteId") Long noteId,
            @Valid @RequestBody NoteRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.updateNote(noteId, requestDto, token));
    }

    // Toggles the pin status of a note.
    @PatchMapping("/{noteId}/pin")
    public ResponseEntity<NoteResponseDto> togglePin(
            @PathVariable("noteId") Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.togglePin(noteId, token));
    }

    // Toggles the archive status of a note.
    @PatchMapping("/{noteId}/archive")
    public ResponseEntity<NoteResponseDto> toggleArchive(
            @PathVariable("noteId") Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.toggleArchive(noteId, token));
    }

    // Toggles the trash status of a note.
    @PatchMapping("/{noteId}/trash")
    public ResponseEntity<NoteResponseDto> toggleTrash(
            @PathVariable("noteId") Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.toggleTrash(noteId, token));
    }

    // Permanently deletes a trashed note.
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Map<String, String>> deleteNote(
            @PathVariable("noteId") Long noteId,
            @RequestHeader("Authorization") String token) {
        noteService.deleteNotePermanently(noteId, token);
        return ResponseEntity.ok(Map.of("message", "Note deleted permanently"));
    }
}
