package com.fundoo.notes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * Class Name  : LabelRequestDto
 * Description : Carries label create and rename details.
 * Author      : Ashish Srivastava
 * Date        : 27/11/2026
 */

public class LabelRequestDto {

    @NotBlank(message = "Label name is required")
    @Size(max = 50, message = "Label name must be at most 50 characters")
    private String name;

    public LabelRequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
