package com.auction.auction_backend.modules.order.service;

import com.auction.auction_backend.common.enums.OrderStatus;
import com.auction.auction_backend.modules.order.dto.request.CreateOrderRequest;
import com.auction.auction_backend.modules.order.dto.response.OrderResponse;
import com.auction.auction_backend.modules.order.entity.Order;

import java.util.List;

public interface OrderService {
    void createOrder(CreateOrderRequest request);

    List<OrderResponse> getMyOrders(); // For buyer

    List<OrderResponse> getMySales(); // For seller

    void updateOrderStatus(Long orderId, OrderStatus status);

    OrderResponse getOrderDetail(Long orderId);

    void createOrderFromAuction(com.auction.auction_backend.modules.product.entity.Product product);
}
