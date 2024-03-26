package com.crawl.backend.query;

import com.crawl.backend.utils.Identifiable;

import java.net.URI;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Query implements Identifiable<String> {
    private final String keyword;
    private String id;
    private QueryStatusEnum status = QueryStatusEnum.UNINITIALIZED;
    private Future<Set<URI>> runner;
    private Set<URI> hits = Set.of();

    public Query(Future<Set<URI>> runner, String keyword) {
        this.runner = runner;
        this.keyword = keyword;
        this.status = QueryStatusEnum.ACTIVE;
    }

    public Query(String keyword) {
        this.keyword = keyword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public QueryStatusEnum getStatus() {
        return status;
    }

    public void setRunner(Future<Set<URI>> runner) {
        this.runner = runner;
        this.status = QueryStatusEnum.ACTIVE;
    }

    public boolean refreshStatus() {
        if (this.isValid() && status != QueryStatusEnum.DONE && runner.isDone()) {
            try {
                this.hits = runner.get();
            } catch (ExecutionException | InterruptedException e) {
                this.hits = Set.of();
            } finally {
                this.status = QueryStatusEnum.DONE;
            }

            return true;
        }

        return false;
    }

    public Set<URI> getHits() {
        return hits;
    }

    private boolean isValid() {
        return Objects.nonNull(id) && Objects.nonNull(runner);
    }
}
