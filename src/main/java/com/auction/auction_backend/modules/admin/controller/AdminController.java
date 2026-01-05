package com.auction.auction_backend.modules.admin.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.modules.admin.dto.response.DashboardResponse;
import com.auction.auction_backend.modules.admin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/upgrade-requests/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> rejectUpgradeRequest(@PathVariable Long id,
            @RequestParam String reason) {
        upgradeRequestService.rejectRequest(id, reason);
        return ResponseEntity.ok(BaseResponse.success("Từ chối yêu cầu thành công"));
    }
}
