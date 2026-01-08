package com.auction.auction_backend.modules.order.service.impl;

import com.auction.auction_backend.common.enums.OrderStatus;
import com.auction.auction_backend.common.exception.AppException;
import com.auction.auction_backend.common.exception.ErrorCode;
import com.auction.auction_backend.modules.order.dto.request.CreateOrderRequest;
import com.auction.auction_backend.modules.order.dto.response.OrderResponse;
import com.auction.auction_backend.modules.order.entity.Order;
import com.auction.auction_backend.modules.order.repository.OrderRepository;
import com.auction.auction_backend.modules.order.service.OrderService;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final com.auction.auction_backend.modules.user.service.RatingService ratingService;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;
        private final com.auction.auction_backend.modules.notification.service.EmailService emailService;

        @Override
        @Transactional
        public void createOrder(CreateOrderRequest request) {
                UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                User winner = userRepository.findById(currentUser.getId())
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

                // Validation
                if (product.getStatus() == com.auction.auction_backend.common.enums.ProductStatus.ACTIVE) {
                        throw new RuntimeException("Sản phẩm chưa kết thúc phiên đấu giá");
                }

                if (product.getCurrentWinner() == null || !product.getCurrentWinner().getId().equals(winner.getId())) {
                        throw new RuntimeException("Bạn không phải là người thắng cuộc sản phẩm này");
                }

                if (orderRepository.findByProductId(product.getId()).isPresent()) {
                        throw new RuntimeException("Đơn hàng cho sản phẩm này đã được tạo");
                }

                Order order = Order.builder()
                                .product(product)
                                .winner(winner)
                                .seller(product.getSeller())
                                .finalPrice(product.getCurrentPrice())
                                .status(OrderStatus.PENDING_PAYMENT)
                                .shippingAddress(request.getShippingAddress())
                                .build();

                orderRepository.save(order);
        }

        @Override
        public List<OrderResponse> getMyOrders() {
                UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                return orderRepository.findByWinnerId(currentUser.getId()).stream()
                                .map(OrderResponse::fromEntity)
                                .toList();
        }

        @Override
        public List<OrderResponse> getMySales() {
                UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                return orderRepository.findBySellerId(currentUser.getId()).stream()
                                .map(OrderResponse::fromEntity)
                                .toList();
        }

        @Override
        @Transactional
        public void updateOrderStatus(Long orderId, OrderStatus status) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

                // TODO: Check permission (Only Seller or Admin can update status properly)

                order.setStatus(status);
                orderRepository.save(order);
        }

        @Override
        public OrderResponse getOrderDetail(Long orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
                return OrderResponse.fromEntity(order);
        }

        @Override
        @Transactional
        public void createOrderFromAuction(Product product) {
                String productLink = "http://localhost:5173/products/" + product.getId(); // TODO: Config

                // Case: No Winner (Unsold)
                if (product.getStatus() != com.auction.auction_backend.common.enums.ProductStatus.SOLD) {
                        if (product.getCurrentWinner() == null) {
                                // Notify Seller: Unsold
                                try {
                                        emailService.sendAuctionEndedNotification(
                                                        product.getSeller().getEmail(),
                                                        product.getTitle(),
                                                        product.getCurrentPrice(),
                                                        false,
                                                        productLink);
                                } catch (Exception e) {
                                        // log
                                }
                                // Update status to UNSOLD to prevent picking up again
                                product.setStatus(com.auction.auction_backend.common.enums.ProductStatus.UNSOLD);
                                productRepository.save(product);
                                return;
                        }
                }

                if (orderRepository.findByProductId(product.getId()).isPresent()) {
                        // Update status just in case it was missed (e.g. legacy data), though Order
                        // exists implies it's handled.
                        if (product.getStatus() == com.auction.auction_backend.common.enums.ProductStatus.ACTIVE) {
                                product.setStatus(com.auction.auction_backend.common.enums.ProductStatus.SOLD);
                                productRepository.save(product);
                        }
                        return;
                }

                User winner = product.getCurrentWinner();
                com.auction.auction_backend.modules.user.entity.User seller = product.getSeller();

                // Notify Winner: Won
                try {
                        emailService.sendAuctionEndedNotification(
                                        winner.getEmail(),
                                        product.getTitle(),
                                        product.getCurrentPrice(),
                                        true,
                                        productLink);
                } catch (Exception e) {
                        // log
                }

                // Notify Seller: Sold
                try {
                        emailService.sendAuctionEndedNotification(
                                        seller.getEmail(),
                                        product.getTitle(),
                                        product.getCurrentPrice(),
                                        false, // isWinner=false -> "Auction Ended" message.
                                        productLink);
                } catch (Exception e) {
                        // log
                }

                Order order = Order.builder()
                                .product(product)
                                .winner(product.getCurrentWinner())
                                .seller(product.getSeller())
                                .finalPrice(product.getCurrentPrice())
                                .status(OrderStatus.PENDING_PAYMENT)
                                .shippingAddress(product.getCurrentWinner().getAddress())
                                .build();

                orderRepository.save(order);

                // Update product status to SOLD
                product.setStatus(com.auction.auction_backend.common.enums.ProductStatus.SOLD);
                productRepository.save(product);
        }

        @Override
        @Transactional
        public void cancelOrder(Long orderId) {
                UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

                if (!order.getSeller().getId().equals(currentUser.getId())) {
                        throw new RuntimeException("Bạn không phải là người bán của đơn hàng này");
                }

                if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
                        throw new RuntimeException("Chỉ có thể hủy đơn hàng khi chưa thanh toán");
                }

                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);

                // Auto rate -1
                com.auction.auction_backend.modules.user.dto.request.CreateRatingRequest ratingRequest = new com.auction.auction_backend.modules.user.dto.request.CreateRatingRequest();
                ratingRequest.setOrderId(order.getId());
                ratingRequest.setScore(-1);
                ratingRequest.setComment("Người thắng không thanh toán (Hủy tự động bởi người bán)");
                ratingService.createRating(ratingRequest);
        }
}
