package com.nulab.exam.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResultDTO {
    private List<String> keywordList;
    private long id;
    private String title;
    private List<String> snippets;

    public SearchResultDTO(List<String> keywordList, long id, String title, List<String> snippet) {
        this.keywordList = keywordList;
        this.id = id;
        this.title = title;
        this.snippets = snippet;
    }

}
