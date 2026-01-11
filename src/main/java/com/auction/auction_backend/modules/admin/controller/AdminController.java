package com.auction.auction_backend.modules.admin.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.modules.admin.dto.response.DashboardResponse;
import com.auction.auction_backend.modules.admin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;
    private final com.auction.auction_backend.modules.user.service.UserService userService;
    private final com.auction.auction_backend.modules.user.service.UpgradeRequestService upgradeRequestService;
    private final com.auction.auction_backend.modules.product.service.impl.ProductService productService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<DashboardResponse>> getDashboardStats() {
        return ResponseEntity.ok(BaseResponse.success(dashboardService.getDashboardStats()));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<DashboardResponse>> getStats() {
        return ResponseEntity.ok(BaseResponse.success(dashboardService.getDashboardStats()));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<com.auction.auction_backend.common.api.PageResponse<com.auction.auction_backend.modules.user.dto.response.UserResponse>>> getUsers(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String keyword,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(BaseResponse.success(userService.getUsers(keyword, page, size)));
    }

    @org.springframework.web.bind.annotation.PutMapping("/users/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> banUser(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam boolean ban) {
        userService.banUser(id, ban);
        String message = ban ? "Khóa tài khoản thành công" : "Mở khóa tài khoản thành công";
        return ResponseEntity.ok(BaseResponse.success(message));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(BaseResponse.success("Xóa người dùng thành công"));
    }

    @PostMapping("/users/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> resetUserPassword(@PathVariable Long id) {
        userService.resetUserPassword(id);
        return ResponseEntity.ok(BaseResponse.success("Reset mật khẩu thành công. Email đã được gửi đến người dùng."));
    }

    @GetMapping("/upgrade-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<java.util.List<com.auction.auction_backend.modules.user.entity.UpgradeRequest>>> getUpgradeRequests() {
        return ResponseEntity.ok(BaseResponse.success(upgradeRequestService.getPendingRequests()));
    }

    @PostMapping("/upgrade-requests/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> approveUpgradeRequest(@PathVariable Long id) {
        upgradeRequestService.approveRequest(id);
        return ResponseEntity.ok(BaseResponse.success("Duyệt yêu cầu thành công"));
    }

    public ResponseEntity<BaseResponse<String>> rejectUpgradeRequest(@PathVariable Long id,
            @RequestParam String reason) {
        upgradeRequestService.rejectRequest(id, reason);
        return ResponseEntity.ok(BaseResponse.success("Từ chối yêu cầu thành công"));
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<org.springframework.data.domain.Page<com.auction.auction_backend.modules.product.dto.response.ProductResponse>>> getProducts(
            @org.springframework.web.bind.annotation.ModelAttribute com.auction.auction_backend.modules.product.dto.request.ProductSearchCriteria criteria) {
        // Admin see all by default if not specified, but let's allow FE to decide
        // If FE sends nothing, includeAllStatuses is null -> ProductSpecification
        // defaults to ACTIVE unless we change logic.
        // Wait, ProductSpec: if !TRUE.equals(includeAllStatuses) -> check status.
        // So if admin wants to see EVERYTHING needed to set includeAllStatuses=true.
        // Or we can set a default for admin here. Let's make it flexible.
        return ResponseEntity.ok(BaseResponse.success(productService.searchProducts(criteria)));
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(BaseResponse.success("Xóa sản phẩm thành công"));
    }
}
