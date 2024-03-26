package com.crawl.backend.query;

import com.crawl.backend.crawler.CrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class QueryService {
    private final CrawlerService crawlerService;
    private final QueryDAO queryDAO = new QueryDAO();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public QueryService(URI baseUrl) throws IOException {
        this.crawlerService = new CrawlerService(baseUrl);

        logger.info("New QueryService instantiated on the given base URL {}", baseUrl);
    }

    public Optional<Query> launchQuery(String keyword) {
        logger.debug("Trying to create new query on keyword {}", keyword);

        return Optional.ofNullable(keyword)
                .filter(Predicate.not(String::isBlank))
                .map(term -> {
                    var runner = executor.submit(() -> crawlerService.find(term).keySet());

                    var query = queryDAO.save(new Query(runner, term));

                    logger.info("New query created with ID \"{}\"", query.getId());

                    return query;
                });
    }

    public Optional<Query> getQuery(String id) {
        logger.debug("Fetching query with given ID \"{}\"", id);

        return queryDAO.get(id).map(this::updateQueryIfStatusChanged);
    }

    public Set<Query> getAllQueries() {
        logger.debug("Fetching all queries");
        return queryDAO.getAll().stream()
                .parallel()
                .map(this::updateQueryIfStatusChanged)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void deleteAllQueries() {
        queryDAO.purge();
    }

    private Query updateQueryIfStatusChanged(Query query) {
        if (query.refreshStatus()) {
            logger.debug("Query with ID \"{}\" has a new status and, therefore, will be updated", query.getId());

            queryDAO.update(query);
        }

        return query;
    }
}
