
package com.fundoo.notes.controller;

import com.fundoo.notes.dto.request.ReminderRequestDto;
import com.fundoo.notes.entity.Note;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.event.ReminderEvent;
import com.fundoo.notes.repository.NoteRepository;
import com.fundoo.notes.repository.UserRepository;
import com.fundoo.notes.service.ReminderEventPublisher;
import com.fundoo.notes.service.TokenService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
 * Class Name  : ReminderController
 * Description : Accepts reminder requests and publishes events.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderEventPublisher eventPublisher;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public ReminderController(
            ReminderEventPublisher eventPublisher,
            NoteRepository noteRepository,
            UserRepository userRepository,
            TokenService tokenService) {

        this.eventPublisher = eventPublisher;
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createReminder(
            @Valid @RequestBody ReminderRequestDto request,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        String token = authorizationHeader.substring(7);

        String email = tokenService.extractSubject(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Note note = noteRepository.findById(request.getNoteId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You cannot set reminder for this note");
        }

        ReminderEvent event = new ReminderEvent(
                user.getId(),
                note.getId(),
                user.getEmail(),
                note.getTitle(),
                request.getReminderTime()
        );

        eventPublisher.publish(event);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "message",
                        "Reminder event published successfully"
                ));
    }
}
