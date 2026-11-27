package com.fundoo.notes.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * Class Name  : NoteResponseDto
 * Description : Returns note details, labels and reminder to the client.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

public class NoteResponseDto {

    private Long id;
    private String title;
    private String description;
    private String color;
    private boolean pinned;
    private boolean archived;
    private boolean trashed;
    private List<LabelResponseDto> labels = new ArrayList<>();
    private LocalDateTime reminderTime;

    public NoteResponseDto() {
    }

    public NoteResponseDto(
            Long id,
            String title,
            String description,
            boolean pinned,
            boolean archived,
            boolean trashed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pinned = pinned;
        this.archived = archived;
        this.trashed = trashed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isTrashed() {
        return trashed;
    }

    public void setTrashed(boolean trashed) {
        this.trashed = trashed;
    }

    public List<LabelResponseDto> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelResponseDto> labels) {
        this.labels = labels;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }
}
