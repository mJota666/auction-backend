package com.auction.auction_backend.modules.admin.service.impl;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.modules.admin.dto.response.DashboardResponse;
import com.auction.auction_backend.modules.admin.service.DashboardService;
import com.auction.auction_backend.modules.order.repository.OrderRepository;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public DashboardResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long activeAuctions = productRepository.countByStatus(ProductStatus.ACTIVE);
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .activeAuctions(activeAuctions)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .build();
    }
}
