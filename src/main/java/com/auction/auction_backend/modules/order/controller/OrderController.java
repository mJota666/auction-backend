package com.auction.auction_backend.modules.order.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.common.enums.OrderStatus;
import com.auction.auction_backend.modules.order.dto.request.CreateOrderRequest;
import com.auction.auction_backend.modules.order.dto.response.OrderResponse;
import com.auction.auction_backend.modules.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<BaseResponse<String>> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        orderService.createOrder(request);
        return ResponseEntity.ok(BaseResponse.success("Tạo đơn hàng thành công"));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<BaseResponse<List<OrderResponse>>> getMyOrders() {
        return ResponseEntity.ok(BaseResponse.success(orderService.getMyOrders()));
    }

    @GetMapping("/my-sales")
    public ResponseEntity<BaseResponse<List<OrderResponse>>> getMySales() {
        return ResponseEntity.ok(BaseResponse.success(orderService.getMySales()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderResponse>> getOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(orderService.getOrderDetail(id)));
    }

    // TODO: Separate endpoint for Admin/Seller status update validation
    @PutMapping("/{id}/status")
    public ResponseEntity<BaseResponse<String>> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(BaseResponse.success("Cập nhật trạng thái đơn hàng thành công"));
    }
}
