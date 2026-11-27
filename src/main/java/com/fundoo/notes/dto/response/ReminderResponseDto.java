package com.fundoo.notes.dto.response;

import java.time.LocalDateTime;

/*
 * Class Name  : ReminderResponseDto
 * Description : Returns reminder details to the client.
 * Author      : Ashish Srivastava
 * Date        : 27/11/2026
 */

public class ReminderResponseDto {

    private Long id;
    private Long noteId;
    private String noteTitle;
    private LocalDateTime reminderTime;

    public ReminderResponseDto() {
    }

    public ReminderResponseDto(
            Long id,
            Long noteId,
            String noteTitle,
            LocalDateTime reminderTime) {
        this.id = id;
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.reminderTime = reminderTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }
}
