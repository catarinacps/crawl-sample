package com.crawl.backend.query;

import com.crawl.backend.utils.DAO;
import com.crawl.backend.utils.DAOInMemory;
import com.crawl.backend.utils.RandomStringGenerator;

public class QueryDAO extends DAOInMemory<Query, String> implements DAO<Query, String> {
    public QueryDAO() {
        super(() -> RandomStringGenerator.generateAlphanumeric(8));
    }
}
