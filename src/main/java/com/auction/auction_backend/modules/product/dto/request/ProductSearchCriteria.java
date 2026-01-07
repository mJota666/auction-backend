package com.auction.auction_backend.modules.product.dto.request;

import lombok.Data;

@Data
public class ProductSearchCriteria {
    private String keyword;
    private Long categoryId;
    private java.util.List<Long> categoryIds;
    private String sortBy;
    private int page = 0;
    private int size = 10;
    private com.auction.auction_backend.common.enums.ProductStatus status;
    private Boolean includeAllStatuses;
}
