package com.fundoo.notes.repository;

import com.fundoo.notes.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Interface Name : ReminderRepository
 * Description    : Provides database operations for reminders.
 * Author         : Ashish Srivastava
 * Date           : 27/11/2026
 */

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    // Retrieves all reminders of a user ordered by time.
    List<Reminder> findByUserIdOrderByReminderTimeAsc(Long userId);

    // Finds a reminder owned by a user.
    Optional<Reminder> findByIdAndUserId(Long id, Long userId);

    // Finds the reminder attached to a note.
    Optional<Reminder> findByNoteId(Long noteId);

    // Removes the reminder attached to a note.
    void deleteByNoteId(Long noteId);
}
