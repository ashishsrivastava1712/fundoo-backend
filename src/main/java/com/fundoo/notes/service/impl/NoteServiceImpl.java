
package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.request.NoteRequestDto;
import com.fundoo.notes.dto.response.NoteResponseDto;
import com.fundoo.notes.entity.Note;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.InvalidCredentialsException;
import com.fundoo.notes.exception.NoteNotFoundException;
import com.fundoo.notes.repository.NoteRepository;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.NoteService;
import com.fundoo.notes.service.TokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Class Name  : NoteServiceImpl
 * Description : Implements note creation, retrieval and
 *               state update operations using JWT.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */
@Service
public class NoteServiceImpl implements NoteService {

    // Logger for tracking note-related business operations.
    private static final Logger logger =
            LoggerFactory.getLogger(NoteServiceImpl.class);

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    // Constructor injection for required dependencies.
    public NoteServiceImpl(
            NoteRepository noteRepository,
            UserRepository userRepository,
            TokenService tokenService) {

        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public NoteResponseDto createNote(
            NoteRequestDto requestDto,
            String token) {

        logger.info("Note creation process started");

        // Resolve the owner from the verified JWT.
        User user = findUserFromToken(token);

        // Create a new note and associate it with its owner.
        Note note = new Note();
        note.setTitle(requestDto.getTitle());
        note.setDescription(requestDto.getDescription());
        note.setUser(user);

        // Persist the note in the database.
        Note savedNote = noteRepository.save(note);

        logger.info(
                "Note created successfully. Note ID: {}, User ID: {}",
                savedNote.getId(),
                user.getId()
        );

        return mapToResponse(savedNote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDto> getAllNotes(String token) {

        logger.info("Fetching notes for authenticated user");

        // Identify the user making the request.
        User user = findUserFromToken(token);

        // Retrieve only notes owned by this user.
        List<Note> notes = noteRepository.findByUserId(user.getId());

        logger.debug(
                "Retrieved {} notes for User ID: {}",
                notes.size(),
                user.getId()
        );

        // Convert entities into response DTOs.
        return notes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public NoteResponseDto togglePin(Long noteId, String token) {

        logger.info("Pin update requested for Note ID: {}", noteId);

        // Find the authenticated user's note.
        Note note = findNoteForUser(noteId, token);

        // Reverse the current pin state.
        note.setPinned(!note.isPinned());

        Note updatedNote = noteRepository.save(note);

        logger.info(
                "Pin state updated for Note ID: {}. Pinned: {}",
                noteId,
                updatedNote.isPinned()
        );

        return mapToResponse(updatedNote);
    }

    @Override
    @Transactional
    public NoteResponseDto toggleArchive(Long noteId, String token) {

        logger.info("Archive update requested for Note ID: {}", noteId);

        // Find the authenticated user's note.
        Note note = findNoteForUser(noteId, token);

        // Reverse the current archive state.
        note.setArchived(!note.isArchived());

        Note updatedNote = noteRepository.save(note);

        logger.info(
                "Archive state updated for Note ID: {}. Archived: {}",
                noteId,
                updatedNote.isArchived()
        );

        return mapToResponse(updatedNote);
    }

    @Override
    @Transactional
    public NoteResponseDto toggleTrash(Long noteId, String token) {

        logger.info("Trash update requested for Note ID: {}", noteId);

        // Find the authenticated user's note.
        Note note = findNoteForUser(noteId, token);

        // Reverse the current trash state.
        note.setTrashed(!note.isTrashed());

        Note updatedNote = noteRepository.save(note);

        logger.info(
                "Trash state updated for Note ID: {}. Trashed: {}",
                noteId,
                updatedNote.isTrashed()
        );

        return mapToResponse(updatedNote);
    }

    /*
     * Finds a note only when it belongs to the authenticated user.
     * A missing note and another user's note both return not found.
     */
    private Note findNoteForUser(Long noteId, String token) {

        User user = findUserFromToken(token);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> {
                    logger.warn("Note not found. Note ID: {}", noteId);
                    return new NoteNotFoundException("Note not found");
                });

        // Prevent users from modifying notes owned by others.
        if (!note.getUser().getId().equals(user.getId())) {

            logger.warn(
                    "Unauthorized note modification attempt. Note ID: {}, User ID: {}",
                    noteId,
                    user.getId()
            );

            throw new NoteNotFoundException("Note not found");
        }

        return note;
    }

    // Validates the JWT and retrieves its corresponding user.
    private User findUserFromToken(String token) {

        if (token == null || token.isBlank()) {

            logger.warn("Request rejected: authorization token is missing");

            throw new InvalidCredentialsException(
                    "Authorization token is required");
        }

        // Remove Bearer prefix when supplied by the client.
        String jwt = token.startsWith("Bearer ")
                ? token.substring(7).trim()
                : token.trim();

        if (!tokenService.isTokenValid(jwt)) {

            logger.warn("Request rejected: invalid or expired JWT");

            throw new InvalidCredentialsException(
                    "Invalid or expired token");
        }

        // JWT subject contains the user's email.
        String email = tokenService.extractSubject(jwt);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Request rejected: JWT user account not found");
                    return new InvalidCredentialsException(
                            "User associated with token not found");
                });

        logger.debug(
                "JWT authentication successful for User ID: {}",
                user.getId()
        );

        return user;
    }

    // Maps the Note entity to an API response DTO.
    private NoteResponseDto mapToResponse(Note note) {

        return new NoteResponseDto(
                note.getId(),
                note.getTitle(),
                note.getDescription(),
                note.isPinned(),
                note.isArchived(),
                note.isTrashed()
        );
    }
}
