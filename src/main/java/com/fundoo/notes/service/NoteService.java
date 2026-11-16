
package com.fundoo.notes.service;

import com.fundoo.notes.dto.request.NoteRequestDto;
import com.fundoo.notes.dto.response.NoteResponseDto;

import java.util.List;

/*
 * Class Name  : NoteService
 * Description : Defines business operations for creating,
 *               retrieving and updating user notes.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

public interface NoteService {

    // Creates a note for the authenticated user.
    NoteResponseDto createNote(NoteRequestDto requestDto, String token);

    // Retrieves all notes belonging to the authenticated user.
    List<NoteResponseDto> getAllNotes(String token);

    // Toggles the pin status of an authenticated user's note.
    NoteResponseDto togglePin(Long noteId, String token);

    // Toggles the archive status of an authenticated user's note.
    NoteResponseDto toggleArchive(Long noteId, String token);

    // Toggles the trash status of an authenticated user's note.
    NoteResponseDto toggleTrash(Long noteId, String token);
}
