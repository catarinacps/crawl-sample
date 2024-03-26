package com.crawl.backend.dto;

public class QueryCreationDTO {
    private String keyword;

    public QueryCreationDTO() {
    }

    public QueryCreationDTO(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
