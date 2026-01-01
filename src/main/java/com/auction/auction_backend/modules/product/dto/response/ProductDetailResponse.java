package com.auction.auction_backend.modules.product.dto.response;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.modules.product.entity.Product;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ProductDetailResponse extends ProductResponse {
    private List<String> imageUrls;
    private List<ProductResponse> relatedProducts;
    private int sellerRatingPositive;
    private int sellerRatingNegative;

    public static ProductDetailResponse fromEntity(Product product, List<ProductResponse> related) {
        String categoryName = (product.getCategory() != null) ? product.getCategory().getName() : "Uncategorized";
        String sellerName = (product.getSeller() != null) ? product.getSeller().getFullName() : "Unknown";
        List<String> images = (product.getImages() != null)
                ? product.getImages().stream().map(img -> img.getUrl()).collect(Collectors.toList())
                : List.of();
        String thumb = (!images.isEmpty()) ? images.get(0) : null;
        String winnerName = null;
        if (product.getCurrentWinner() != null) {
            winnerName = product.getCurrentWinner().getFullName();
        }
        return ProductDetailResponse.builder()
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
                .categoryName(categoryName)
                .sellerName(sellerName)
                .currentWinnerName(winnerName)
                .status(product.getStatus())
                .thumbnailUrl(thumb)
                .bidCount(product.getBids() != null ? product.getBids().size() : 0)
                .imageUrls(images)
                .relatedProducts(related)
                .sellerRatingPositive(product.getSeller() != null ? product.getSeller().getRatingPositive() : 0)
                .sellerRatingNegative(product.getSeller() != null ? product.getSeller().getRatingNegative() : 0)
                .build();
    }
}
