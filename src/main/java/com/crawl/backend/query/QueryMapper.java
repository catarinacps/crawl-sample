package com.crawl.backend.query;

import com.crawl.backend.dto.QueryIdDTO;
import com.crawl.backend.dto.QueryStatusDTO;
import com.crawl.backend.dto.QueryStatusDTOBuilder;

public class QueryMapper {
    public static QueryStatusDTO toStatusDTO(Query query) {
        return new QueryStatusDTOBuilder()
                .setUrls(query.getHits())
                .setStatus(query.getStatus())
                .setId(query.getId())
                .createQueryStatusDTO();
    }

    public static QueryIdDTO toIdDTO(Query query) {
        return new QueryIdDTO(query.getId());
    }
}
