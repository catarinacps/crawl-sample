package com.crawl.backend.dto;

public class QueryIdDTOBuilder {
    private String id;

    public QueryIdDTOBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public QueryIdDTO createQueryIdDTO() {
        return new QueryIdDTO(id);
    }
}