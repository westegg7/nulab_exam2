package com.nulab.exam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WikiPageDTO {
    private long id;
    private String title;
    private String content;

    public WikiPageDTO(long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
