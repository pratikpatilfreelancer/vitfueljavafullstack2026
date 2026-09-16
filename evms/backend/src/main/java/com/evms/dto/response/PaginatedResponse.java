package com.evms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated response wrapper for list endpoints.
 * JSON shape: {@code { "data": [...], "meta": { total, page, limit, totalPages } }}
 *
 * @param <T> the type of items in the data list
 * @author EVMS Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {

    private List<T> data;
    private Meta meta;

    /**
     * Pagination metadata.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {

        private long total;
        private int page;
        private int limit;
        private int totalPages;
    }
}
