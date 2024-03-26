package com.crawl.backend.dto;

public class QueryCreationDTOBuilder {
    private String keyword;

    public QueryCreationDTOBuilder setKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public QueryCreationDTO createQueryCreationDTO() {
        return new QueryCreationDTO(keyword);
    }
}