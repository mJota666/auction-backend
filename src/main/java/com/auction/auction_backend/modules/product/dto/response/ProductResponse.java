package com.auction.auction_backend.modules.product.dto.response;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.modules.product.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private BigDecimal buyNowPrice;
    private BigDecimal stepPrice;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private String thumbnailUrl;
    private String categoryName;
    private ProductStatus status;
    private String currentWinnerName;
    private int bidCount;

    public static ProductResponse fromEntity(Product product) {
        String thumb = null;
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            thumb = product.getImages().get(0).getUrl();
        }

        String winnerName = null;
        if (product.getCurrentWinner() != null) {
            winnerName = product.getCurrentWinner().getFullName();
        }

        int totalBids = (product.getBids() != null) ? product.getBids().size() : 0;
        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .startPrice(product.getStartPrice())
                .currentPrice(product.getCurrentPrice())
                .buyNowPrice(product.getBuyNowPrice())
                .stepPrice(product.getStepPrice())
                .endAt(product.getEndAt())
                .createdAt(product.getCreatedAt())
                .thumbnailUrl(thumb)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .status(product.getStatus())
                .currentWinnerName(winnerName)
                .bidCount(totalBids)
                .build();
    }
}
