package com.fundoo.notes.repository;

import com.fundoo.notes.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Interface Name : LabelRepository
 * Description    : Provides database operations for labels.
 * Author         : Ashish Srivastava
 * Date           : 27/11/2026
 */

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    // Retrieves all labels of a user ordered by name.
    List<Label> findByUserIdOrderByNameAsc(Long userId);

    // Finds a label owned by a user.
    Optional<Label> findByIdAndUserId(Long id, Long userId);

    // Checks whether a user already has a label with this name.
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
}
