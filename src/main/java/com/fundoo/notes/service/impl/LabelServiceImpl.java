package com.fundoo.notes.service.impl;

import com.fundoo.notes.dto.request.LabelRequestDto;
import com.fundoo.notes.dto.response.LabelResponseDto;
import com.fundoo.notes.entity.Label;
import com.fundoo.notes.entity.Note;
import com.fundoo.notes.entity.User;
import com.fundoo.notes.exception.DuplicateEmailException;
import com.fundoo.notes.exception.NoteNotFoundException;
import com.fundoo.notes.exception.ResourceNotFoundException;
import com.fundoo.notes.repository.LabelRepository;
import com.fundoo.notes.repository.NoteRepository;
import com.fundoo.notes.service.LabelService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Class Name  : LabelServiceImpl
 * Description : Implements label CRUD and note-label assignment.
 * Author      : Ashish Srivastava
 * Date        : 27/11/2026
 */

@Service
public class LabelServiceImpl implements LabelService {

    private static final Logger logger =
            LoggerFactory.getLogger(LabelServiceImpl.class);

    private final LabelRepository labelRepository;
    private final NoteRepository noteRepository;
    private final AuthenticatedUserResolver userResolver;

    public LabelServiceImpl(
            LabelRepository labelRepository,
            NoteRepository noteRepository,
            AuthenticatedUserResolver userResolver) {
        this.labelRepository = labelRepository;
        this.noteRepository = noteRepository;
        this.userResolver = userResolver;
    }

    @Override
    @Transactional
    public LabelResponseDto createLabel(LabelRequestDto requestDto, String token) {
        User user = userResolver.resolve(token);
        String name = requestDto.getName().trim();

        if (labelRepository.existsByUserIdAndNameIgnoreCase(user.getId(), name)) {
            throw new DuplicateEmailException("Label already exists");
        }

        Label saved = labelRepository.save(new Label(name, user));
        logger.info("Label created. Label ID: {}, User ID: {}", saved.getId(), user.getId());
        return map(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabelResponseDto> getAllLabels(String token) {
        User user = userResolver.resolve(token);
        return labelRepository.findByUserIdOrderByNameAsc(user.getId())
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "notes", allEntries = true)
    public LabelResponseDto updateLabel(
            Long labelId,
            LabelRequestDto requestDto,
            String token) {
        User user = userResolver.resolve(token);
        Label label = findLabel(labelId, user);
        String name = requestDto.getName().trim();

        if (!label.getName().equalsIgnoreCase(name)
                && labelRepository.existsByUserIdAndNameIgnoreCase(user.getId(), name)) {
            throw new DuplicateEmailException("Label already exists");
        }

        label.setName(name);
        return map(labelRepository.save(label));
    }

    @Override
    @Transactional
    @CacheEvict(value = "notes", allEntries = true)
    public void deleteLabel(Long labelId, String token) {
        User user = userResolver.resolve(token);
        Label label = findLabel(labelId, user);

        // Detach the label from all of the user's notes first.
        for (Note note : noteRepository.findByUserId(user.getId())) {
            note.getLabels().removeIf(item -> item.getId().equals(labelId));
        }

        labelRepository.delete(label);
        logger.info("Label deleted. Label ID: {}", labelId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notes", allEntries = true)
    public void assignLabelToNote(Long labelId, Long noteId, String token) {
        User user = userResolver.resolve(token);
        Label label = findLabel(labelId, user);
        Note note = findNote(noteId, user);
        note.getLabels().add(label);
        noteRepository.save(note);
    }

    @Override
    @Transactional
    @CacheEvict(value = "notes", allEntries = true)
    public void removeLabelFromNote(Long labelId, Long noteId, String token) {
        User user = userResolver.resolve(token);
        findLabel(labelId, user);
        Note note = findNote(noteId, user);
        note.getLabels().removeIf(item -> item.getId().equals(labelId));
        noteRepository.save(note);
    }

    private Label findLabel(Long labelId, User user) {
        return labelRepository.findByIdAndUserId(labelId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
    }

    private Note findNote(Long noteId, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new NoteNotFoundException("Note not found");
        }
        return note;
    }

    private LabelResponseDto map(Label label) {
        return new LabelResponseDto(label.getId(), label.getName());
    }
}
