package com.fundoo.notes.service;

import com.fundoo.notes.dto.request.LabelRequestDto;
import com.fundoo.notes.dto.response.LabelResponseDto;

import java.util.List;

/*
 * Interface Name : LabelService
 * Description    : Defines label management and note-label operations.
 * Author         : Ashish Srivastava
 * Date           : 27/11/2026
 */

public interface LabelService {

    LabelResponseDto createLabel(LabelRequestDto requestDto, String token);

    List<LabelResponseDto> getAllLabels(String token);

    LabelResponseDto updateLabel(Long labelId, LabelRequestDto requestDto, String token);

    void deleteLabel(Long labelId, String token);

    void assignLabelToNote(Long labelId, Long noteId, String token);

    void removeLabelFromNote(Long labelId, Long noteId, String token);
}
