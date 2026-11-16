
package com.fundoo.notes.dto.request;

import jakarta.validation.constraints.NotBlank;

/*
 * Class Name  : NoteRequestDto
 * Description : Accepts and validates note creation details
 *               received from the client.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

public class NoteRequestDto {

    @NotBlank(message = "Note title is required")
    private String title;

    private String description;

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
}
