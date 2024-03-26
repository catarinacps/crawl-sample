package com.crawl.backend.query;

import com.crawl.backend.dto.QueryCreationDTO;
import com.crawl.backend.dto.QueryIdDTO;
import com.crawl.backend.dto.QueryStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

public class QueryController {
    private final QueryService queryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public QueryController(URI baseUrl) throws IOException {
        this.queryService = new QueryService(baseUrl);

        logger.info("New QueryController instantiated with base URL {}", baseUrl);
    }

    public Optional<QueryStatusDTO> get(String id) {
        logger.debug("Received a query lookup request with ID {}", id);

        return queryService.getQuery(id).map(QueryMapper::toStatusDTO);
    }

    public Optional<QueryIdDTO> create(QueryCreationDTO dto) {
        logger.debug("Creating new query from input payload {}", dto);

        return Optional.ofNullable(dto)
                .map(QueryCreationDTO::getKeyword)
                .flatMap(queryService::launchQuery)
                .map(QueryMapper::toIdDTO);
    }
}
