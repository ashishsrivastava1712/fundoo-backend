package com.fundoo.notes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * Class Name  : NoteRequestDto
 * Description : Carries note create and update details.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

public class NoteRequestDto {

    @NotBlank(message = "Note title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;

    @Size(max = 20, message = "Colour must be at most 20 characters")
    private String color;

    public NoteRequestDto() {
    }

    public NoteRequestDto(String title, String description) {
        this.title = title;
        this.description = description;
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
}
