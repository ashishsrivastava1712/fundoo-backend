
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Class Name  : NoteServiceImpl
 * Description : Implements note creation, retrieval and
 *               state update operations using JWT and caching.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger logger =
            LoggerFactory.getLogger(NoteServiceImpl.class);

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

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
    @CacheEvict(value = "notes", allEntries = true)
    public NoteResponseDto createNote(
            NoteRequestDto requestDto,
            String token) {

        logger.info("Note creation process started");

        User user = findUserFromToken(token);

        Note note = new Note();
        note.setTitle(requestDto.getTitle());
        note.setDescription(requestDto.getDescription());
        note.setUser(user);

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
    @Cacheable(value = "notes", key = "#root.target.getUserIdFromToken(#p0)")
    public List<NoteResponseDto> getAllNotes(String token) {

        logger.info("Fetching notes for authenticated user");

        User user = findUserFromToken(token);

        List<Note> notes = noteRepository.findByUserId(user.getId());

        logger.debug(
                "Retrieved {} notes for User ID: {}",
                notes.size(),
                user.getId()
        );

        return notes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "notes", allEntries = true)
    public NoteResponseDto togglePin(Long noteId, String token) {

        logger.info("Pin update requested for Note ID: {}", noteId);

        Note note = findNoteForUser(noteId, token);
        note.setPinned(!note.isPinned());

        Note updatedNote = noteRepository.save(note);

        logger.info("Pin state updated for Note ID: {}", noteId);

        return mapToResponse(updatedNote);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notes", allEntries = true)
    public NoteResponseDto toggleArchive(Long noteId, String token) {

        logger.info("Archive update requested for Note ID: {}", noteId);

        Note note = findNoteForUser(noteId, token);
        note.setArchived(!note.isArchived());

        Note updatedNote = noteRepository.save(note);

        logger.info("Archive state updated for Note ID: {}", noteId);

        return mapToResponse(updatedNote);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notes", allEntries = true)
    public NoteResponseDto toggleTrash(Long noteId, String token) {

        logger.info("Trash update requested for Note ID: {}", noteId);

        Note note = findNoteForUser(noteId, token);
        note.setTrashed(!note.isTrashed());

        Note updatedNote = noteRepository.save(note);

        logger.info("Trash state updated for Note ID: {}", noteId);

        return mapToResponse(updatedNote);
    }

    /*
     * Returns authenticated user's ID for the cache key.
     */
    public Long getUserIdFromToken(String token) {
        return findUserFromToken(token).getId();
    }

    private Note findNoteForUser(Long noteId, String token) {

        User user = findUserFromToken(token);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> {
                    logger.warn("Note not found. Note ID: {}", noteId);
                    return new NoteNotFoundException("Note not found");
                });

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

    private User findUserFromToken(String token) {

        if (token == null || token.isBlank()) {

            logger.warn("Authorization token is missing");

            throw new InvalidCredentialsException(
                    "Authorization token is required");
        }

        String jwt = token.startsWith("Bearer ")
                ? token.substring(7).trim()
                : token.trim();

        if (!tokenService.isTokenValid(jwt)) {

            logger.warn("Invalid or expired JWT");

            throw new InvalidCredentialsException(
                    "Invalid or expired token");
        }

        String email = tokenService.extractSubject(jwt);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "User associated with token not found"));

        logger.debug(
                "JWT authentication successful for User ID: {}",
                user.getId()
        );

        return user;
    }

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
