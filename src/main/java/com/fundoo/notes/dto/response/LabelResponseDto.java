package com.fundoo.notes.dto.response;

/*
 * Class Name  : LabelResponseDto
 * Description : Returns label details to the client.
 * Author      : Ashish Srivastava
 * Date        : 27/11/2026
 */

public class LabelResponseDto {

    private Long id;
    private String name;

    public LabelResponseDto() {
    }

    public LabelResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
