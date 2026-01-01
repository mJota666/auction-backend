package com.auction.auction_backend.modules.order.dto.response;

import com.auction.auction_backend.common.enums.OrderStatus;
import com.auction.auction_backend.modules.order.entity.Order;
import com.auction.auction_backend.modules.product.dto.response.ProductResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private ProductResponse product;
    private Long winnerId;
    private String winnerName;
    private Long sellerId;
    private String sellerName;
    private BigDecimal finalPrice;
    private OrderStatus status;
    private String shippingAddress;
    private LocalDateTime createdAt;

    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .product(ProductResponse.fromEntity(order.getProduct()))
                .winnerId(order.getWinner().getId())
                .winnerName(order.getWinner().getFullName())
                .sellerId(order.getSeller().getId())
                .sellerName(order.getSeller().getFullName())
                .finalPrice(order.getFinalPrice())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt() : LocalDateTime.now())
                .build();
    }
}
