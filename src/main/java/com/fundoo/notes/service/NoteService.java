
package com.fundoo.notes.service;

import com.fundoo.notes.dto.request.NoteRequestDto;
import com.fundoo.notes.dto.response.NoteResponseDto;

import java.util.List;

/*
 * Class Name  : NoteService
 * Description : Defines business operations for creating
 *               and retrieving user notes.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

public interface NoteService {

    // Creates a note for the authenticated user.
    NoteResponseDto createNote(NoteRequestDto requestDto, String token);

    // Retrieves all notes belonging to the authenticated user.
    List<NoteResponseDto> getAllNotes(String token);
}
