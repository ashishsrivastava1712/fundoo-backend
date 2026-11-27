
package com.fundoo.notes.controller;

import com.fundoo.notes.dto.request.ReminderRequestDto;
import com.fundoo.notes.dto.request.ReminderUpdateRequestDto;
import com.fundoo.notes.dto.response.ReminderResponseDto;
import com.fundoo.notes.service.ReminderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
 * Class Name  : ReminderController
 * Description : Exposes reminder CRUD endpoints; scheduling publishes
 *               a RabbitMQ reminder event.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<ReminderResponseDto> createReminder(
            @Valid @RequestBody ReminderRequestDto request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reminderService.createReminder(request, token));
    }

    @GetMapping
    public ResponseEntity<List<ReminderResponseDto>> getAllReminders(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(reminderService.getAllReminders(token));
    }

    @PutMapping("/{reminderId}")
    public ResponseEntity<ReminderResponseDto> updateReminder(
            @PathVariable("reminderId") Long reminderId,
            @Valid @RequestBody ReminderUpdateRequestDto request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(
                reminderService.updateReminder(reminderId, request, token));
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Map<String, String>> deleteReminder(
            @PathVariable("reminderId") Long reminderId,
            @RequestHeader("Authorization") String token) {
        reminderService.deleteReminder(reminderId, token);
        return ResponseEntity.ok(Map.of("message", "Reminder deleted successfully"));
    }
}
