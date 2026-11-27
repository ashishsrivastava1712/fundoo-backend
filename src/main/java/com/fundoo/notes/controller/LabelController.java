package com.fundoo.notes.controller;

import com.fundoo.notes.dto.request.LabelRequestDto;
import com.fundoo.notes.dto.response.LabelResponseDto;
import com.fundoo.notes.service.LabelService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
 * Class Name  : LabelController
 * Description : Exposes REST endpoints for labels and note-label mapping.
 * Author      : Ashish Srivastava
 * Date        : 27/11/2026
 */

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @PostMapping
    public ResponseEntity<LabelResponseDto> createLabel(
            @Valid @RequestBody LabelRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(labelService.createLabel(requestDto, token));
    }

    @GetMapping
    public ResponseEntity<List<LabelResponseDto>> getAllLabels(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(labelService.getAllLabels(token));
    }

    @PutMapping("/{labelId}")
    public ResponseEntity<LabelResponseDto> updateLabel(
            @PathVariable("labelId") Long labelId,
            @Valid @RequestBody LabelRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(labelService.updateLabel(labelId, requestDto, token));
    }

    @DeleteMapping("/{labelId}")
    public ResponseEntity<Map<String, String>> deleteLabel(
            @PathVariable("labelId") Long labelId,
            @RequestHeader("Authorization") String token) {
        labelService.deleteLabel(labelId, token);
        return ResponseEntity.ok(Map.of("message", "Label deleted successfully"));
    }

    @PostMapping("/{labelId}/notes/{noteId}")
    public ResponseEntity<Map<String, String>> assignLabel(
            @PathVariable("labelId") Long labelId,
            @PathVariable("noteId") Long noteId,
            @RequestHeader("Authorization") String token) {
        labelService.assignLabelToNote(labelId, noteId, token);
        return ResponseEntity.ok(Map.of("message", "Label assigned successfully"));
    }

    @DeleteMapping("/{labelId}/notes/{noteId}")
    public ResponseEntity<Map<String, String>> removeLabel(
            @PathVariable("labelId") Long labelId,
            @PathVariable("noteId") Long noteId,
            @RequestHeader("Authorization") String token) {
        labelService.removeLabelFromNote(labelId, noteId, token);
        return ResponseEntity.ok(Map.of("message", "Label removed successfully"));
    }
}
