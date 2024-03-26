package com.crawl.backend.dto;

import com.crawl.backend.query.QueryStatusEnum;

import java.net.URI;
import java.util.Set;

public class QueryStatusDTOBuilder {
    private String id;
    private QueryStatusEnum status;
    private Set<URI> urls;

    public QueryStatusDTOBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public QueryStatusDTOBuilder setStatus(QueryStatusEnum status) {
        this.status = status;
        return this;
    }

    public QueryStatusDTOBuilder setUrls(Set<URI> urls) {
        this.urls = urls;
        return this;
    }

    public QueryStatusDTO createQueryStatusDTO() {
        return new QueryStatusDTO(id, status, urls);
    }
}