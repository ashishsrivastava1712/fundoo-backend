package com.fundoo.notes.service;

import com.fundoo.notes.dto.request.ReminderRequestDto;
import com.fundoo.notes.dto.request.ReminderUpdateRequestDto;
import com.fundoo.notes.dto.response.ReminderResponseDto;

import java.util.List;

/*
 * Interface Name : ReminderService
 * Description    : Defines reminder scheduling operations.
 * Author         : Ashish Srivastava
 * Date           : 27/11/2026
 */

public interface ReminderService {

    ReminderResponseDto createReminder(ReminderRequestDto requestDto, String token);

    List<ReminderResponseDto> getAllReminders(String token);

    ReminderResponseDto updateReminder(
            Long reminderId,
            ReminderUpdateRequestDto requestDto,
            String token);

    void deleteReminder(Long reminderId, String token);
}
