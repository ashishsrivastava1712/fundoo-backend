package com.fundoo.notes.service;

import com.fundoo.notes.dto.request.NoteRequestDto;
import com.fundoo.notes.dto.response.NoteResponseDto;

import java.util.List;

/*
 * Interface Name : NoteService
 * Description    : Defines note management operations.
 * Author         : Ashish Srivastava
 * Date           : 14/11/2026
 */

public interface NoteService {

    // Creates a note for the authenticated user.
    NoteResponseDto createNote(NoteRequestDto requestDto, String token);

    // Retrieves all notes belonging to the authenticated user.
    List<NoteResponseDto> getAllNotes(String token);

    // Retrieves a single note of the authenticated user.
    NoteResponseDto getNoteById(Long noteId, String token);

    // Updates title, description and colour of a note.
    NoteResponseDto updateNote(Long noteId, NoteRequestDto requestDto, String token);

    // Toggles the pin status of an authenticated user's note.
    NoteResponseDto togglePin(Long noteId, String token);

    // Toggles the archive status of an authenticated user's note.
    NoteResponseDto toggleArchive(Long noteId, String token);

    // Toggles the trash status of an authenticated user's note.
    NoteResponseDto toggleTrash(Long noteId, String token);

    // Permanently deletes a trashed note.
    void deleteNotePermanently(Long noteId, String token);
}
