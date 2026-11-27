package com.fundoo.notes.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/*
 * Class Name  : ReminderUpdateRequestDto
 * Description : Carries the new time for an existing reminder.
 * Author      : Ashish Srivastava
 * Date        : 27/11/2026
 */

public class ReminderUpdateRequestDto {

    @NotNull(message = "Reminder time is required")
    @Future(message = "Reminder time must be in the future")
    private LocalDateTime reminderTime;

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }
}
