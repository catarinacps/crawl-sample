package com.crawl.backend.query;

import com.crawl.backend.crawler.CrawlerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueryServiceTest {
    @Mock
    QueryDAO queryDAO;
    @Mock
    CrawlerService crawlerService;
    @InjectMocks
    QueryService queryService = new QueryService(null);

    QueryServiceTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void launchQueryWithNonNullKeyword() {
        ConcurrentHashMap<URI, Long> hitsMap = new ConcurrentHashMap<>(Map.of(URI.create("http://localhost:8080"), 2L));
        String keyword = "keyword";
        var expectedQuery = new Query(keyword);

        when(queryDAO.get(any())).thenReturn(Optional.of(expectedQuery));
        when(queryDAO.save(any())).thenReturn(expectedQuery);
        when(crawlerService.find(anyString())).thenReturn(hitsMap);

        var createdQuery = queryService.launchQuery(keyword);

        assertTrue(createdQuery.isPresent());

        var savedQuery = queryService.getQuery(createdQuery.get().getId());

        assertTrue(savedQuery.isPresent());

        assertEquals(savedQuery.get(), createdQuery.get());
    }

    @Test
    void launchQueryWithNullKeyword() {
        assertTrue(queryService.launchQuery(null).isEmpty());
    }

    @Test
    void launchQueryWithEmptyKeyword() {
        assertTrue(queryService.launchQuery("").isEmpty());
    }

    @Test
    void getQuery() {
    }

    @Test
    void getQueries() {
    }
}