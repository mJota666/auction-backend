package com.auction.auction_backend.modules.user.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.common.api.PageResponse;
import com.auction.auction_backend.common.enums.UpgradeRequestStatus;
import com.auction.auction_backend.modules.user.dto.request.ProcessUpgradeRequest;
import com.auction.auction_backend.modules.user.dto.request.RequestUpgradeRequest;
import com.auction.auction_backend.modules.user.dto.response.UpgradeRequestResponse;
import com.auction.auction_backend.modules.user.service.UpgradeRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UpgradeRequestController {

    private final UpgradeRequestService upgradeRequestService;

    @PostMapping("/users/upgrade-request")
    public ResponseEntity<BaseResponse<String>> requestUpgrade(@RequestBody @Valid RequestUpgradeRequest request) {
        upgradeRequestService.requestUpgrade(request);
        return ResponseEntity.ok(BaseResponse.success("Gửi yêu cầu nâng cấp thành công"));
    }

    @GetMapping("/admin/upgrade-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<PageResponse<UpgradeRequestResponse>>> getRequests(
            @RequestParam(required = false) UpgradeRequestStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(BaseResponse.success(upgradeRequestService.getRequestsByStatus(status, page, size)));
    }

    @PutMapping("/admin/upgrade-requests/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> processRequest(
            @PathVariable Long id,
            @RequestBody @Valid ProcessUpgradeRequest request) {
        upgradeRequestService.processRequest(id, request);
        return ResponseEntity.ok(BaseResponse.success("Xử lý yêu cầu thành công"));
    }
}
