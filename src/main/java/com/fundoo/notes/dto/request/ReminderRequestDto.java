
package com.fundoo.notes.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;

/*
 * Class Name  : ReminderRequestDto
 * Description : Validates reminder scheduling requests.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

public class ReminderRequestDto {

    @NotNull(message = "Note ID is required")
    private Long noteId;

    @NotNull(message = "Reminder time is required")
    @Future(message = "Reminder time must be in the future")
    private LocalDateTime reminderTime;

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }
}
