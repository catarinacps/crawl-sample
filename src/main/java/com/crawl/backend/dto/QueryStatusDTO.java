package com.crawl.backend.dto;

import com.crawl.backend.query.QueryStatusEnum;

import java.net.URI;
import java.util.Set;

public class QueryStatusDTO {
    private String id;
    private QueryStatusEnum status;
    private Set<URI> urls;

    public QueryStatusDTO() {
    }

    public QueryStatusDTO(String id, QueryStatusEnum status, Set<URI> urls) {
        this.id = id;
        this.status = status;
        this.urls = urls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public QueryStatusEnum getStatus() {
        return status;
    }

    public void setStatus(QueryStatusEnum status) {
        this.status = status;
    }

    public Set<URI> getUrls() {
        return urls;
    }

    public void setUrls(Set<URI> urls) {
        this.urls = urls;
    }
}
