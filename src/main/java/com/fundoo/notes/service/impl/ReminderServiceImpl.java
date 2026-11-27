package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.request.ReminderRequestDto;
import com.fundoo.notes.dto.request.ReminderUpdateRequestDto;
import com.fundoo.notes.dto.response.ReminderResponseDto;
import com.fundoo.notes.entity.Note;
import com.fundoo.notes.entity.Reminder;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.event.ReminderEvent;
import com.fundoo.notes.exception.NoteNotFoundException;
import com.fundoo.notes.exception.ResourceNotFoundException;
import com.fundoo.notes.repository.NoteRepository;
import com.fundoo.notes.repository.ReminderRepository;
import com.fundoo.notes.service.ReminderEventPublisher;
import com.fundoo.notes.service.ReminderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Class Name  : ReminderServiceImpl
 * Description : Persists reminders and publishes reminder events to RabbitMQ.
 * Author      : Ashish Srivastava
 * Date        : 27/11/2026
 */

@Service
public class ReminderServiceImpl implements ReminderService {

    private static final Logger logger =
            LoggerFactory.getLogger(ReminderServiceImpl.class);

    private final ReminderRepository reminderRepository;
    private final NoteRepository noteRepository;
    private final ReminderEventPublisher eventPublisher;
    private final AuthenticatedUserResolver userResolver;

    public ReminderServiceImpl(
            ReminderRepository reminderRepository,
            NoteRepository noteRepository,
            ReminderEventPublisher eventPublisher,
            AuthenticatedUserResolver userResolver) {
        this.reminderRepository = reminderRepository;
        this.noteRepository = noteRepository;
        this.eventPublisher = eventPublisher;
        this.userResolver = userResolver;
    }

    @Override
    @Transactional
    @CacheEvict(value = "notes", allEntries = true)
    public ReminderResponseDto createReminder(
            ReminderRequestDto requestDto,
            String token) {
        User user = userResolver.resolve(token);
        Note note = noteRepository.findById(requestDto.getNoteId())
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new NoteNotFoundException("Note not found");
        }

        // One reminder per note: reschedule if one already exists.
        Reminder reminder = reminderRepository.findByNoteId(note.getId())
                .orElseGet(() -> new Reminder(null, note, user));
        reminder.setReminderTime(requestDto.getReminderTime());
        Reminder saved = reminderRepository.save(reminder);

        publish(saved, user, note);
        logger.info("Reminder saved. Reminder ID: {}, Note ID: {}",
                saved.getId(), note.getId());
        return map(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponseDto> getAllReminders(String token) {
        User user = userResolver.resolve(token);
        return reminderRepository.findByUserIdOrderByReminderTimeAsc(user.getId())
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "notes", allEntries = true)
    public ReminderResponseDto updateReminder(
            Long reminderId,
            ReminderUpdateRequestDto requestDto,
            String token) {
        User user = userResolver.resolve(token);
        Reminder reminder = findReminder(reminderId, user);
        reminder.setReminderTime(requestDto.getReminderTime());
        Reminder saved = reminderRepository.save(reminder);
        publish(saved, user, saved.getNote());
        return map(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notes", allEntries = true)
    public void deleteReminder(Long reminderId, String token) {
        User user = userResolver.resolve(token);
        reminderRepository.delete(findReminder(reminderId, user));
        logger.info("Reminder deleted. Reminder ID: {}", reminderId);
    }

    private void publish(Reminder reminder, User user, Note note) {
        eventPublisher.publish(new ReminderEvent(
                user.getId(),
                note.getId(),
                user.getEmail(),
                note.getTitle(),
                reminder.getReminderTime()
        ));
    }

    private Reminder findReminder(Long reminderId, User user) {
        return reminderRepository.findByIdAndUserId(reminderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found"));
    }

    private ReminderResponseDto map(Reminder reminder) {
        return new ReminderResponseDto(
                reminder.getId(),
                reminder.getNote().getId(),
                reminder.getNote().getTitle(),
                reminder.getReminderTime()
        );
    }
}
