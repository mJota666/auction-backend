package com.auction.auction_backend.modules.order.service.impl;

import com.auction.auction_backend.common.enums.OrderStatus;
import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.modules.order.entity.Order;
import com.auction.auction_backend.modules.order.repository.OrderRepository;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    @Override
    public void createOrderFromAuction(Product product) {
        if (product.getStatus() != ProductStatus.ACTIVE) return;
        if (product.getCurrentWinner() == null) {
            product.setStatus(ProductStatus.UNSOLD);
            productRepository.save(product);
            log.info("Auction ended without winner: Product ID {}", product.getId());
            return;
        }
        product.setStatus(ProductStatus.SOLD);
        productRepository.save(product);
        Order order = Order.builder()
                .product(product)
                .seller(product.getSeller())
                .winner(product.getCurrentWinner())
                .finalPrice(product.getCurrentPrice())
                .status(OrderStatus.PENDING_PAYMENT)
                .shippingAddress(product.getCurrentWinner().getAddress())
                .build();
        orderRepository.save(order);
        log.info("Auction success! Created Order ID {} for Product ID {}", order.getId(), product.getId());    }
}
