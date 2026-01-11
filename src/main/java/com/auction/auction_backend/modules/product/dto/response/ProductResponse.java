package com.auction.auction_backend.modules.product.dto.response;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.modules.product.entity.Product;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ProductResponse {
    private Long id;
    private String title;
    private String description; // description_html

    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private BigDecimal buyNowPrice;
    private BigDecimal stepPrice;

    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;

    private boolean autoExtendEnabled;
    private boolean allowUnratedBidder;

    private String categoryName;
    private String thumbnailUrl;
    private ProductStatus status;

    private String sellerName;
    private Long sellerId;
    private String currentWinnerName;
    private Long currentWinnerId;
    private int bidCount;

    public static ProductResponse fromEntity(Product product) {
        String thumb = null;
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            thumb = product.getImages().get(0).getUrl();
        }

        String winnerName = null;
        Long winnerId = null;
        if (product.getCurrentWinner() != null) {
            // Mask tên
            winnerName = com.auction.auction_backend.common.utils.AppUtils
                    .maskName(product.getCurrentWinner().getFullName());
            winnerId = product.getCurrentWinner().getId();
        }

        String sellerName = (product.getSeller() != null) ? product.getSeller().getFullName() : "Unknown";
        Long sellerId = (product.getSeller() != null) ? product.getSeller().getId() : null;

        int totalBids = (product.getBids() != null) ? product.getBids().size() : 0;

        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .startPrice(product.getStartPrice())
                .currentPrice(product.getCurrentPrice())
                .buyNowPrice(product.getBuyNowPrice())
                .stepPrice(product.getStepPrice())
                .startAt(product.getStartAt())
                .endAt(product.getEndAt())
                .createdAt(product.getCreatedAt())
                .autoExtendEnabled(product.isAutoExtendEnabled())
                .allowUnratedBidder(product.isAllowUnratedBidder())
                .thumbnailUrl(thumb)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .status(product.getStatus())
                .sellerName(sellerName)
                .sellerId(sellerId)
                .currentWinnerName(winnerName)
                .currentWinnerId(winnerId)
                .bidCount(totalBids)
                .build();
    }
}
