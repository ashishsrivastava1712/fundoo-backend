
/*
 * Class Name  : NoteRepository
 * Description : Provides database operations for Note entities,
 *               including user-specific retrieval and filtering.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

package com.fundoo.notes.repository;

import com.fundoo.notes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Retrieves all notes belonging to a particular user.
    List<Note> findByUserId(Long userId);

    // Retrieves notes that are neither archived nor trashed.
    List<Note> findByUserIdAndArchivedFalseAndTrashedFalse(Long userId);

    // Searches a user's notes by title, ignoring letter case.
    List<Note> findByUserIdAndTitleContainingIgnoreCase(
            Long userId,
            String keyword
    );
}
