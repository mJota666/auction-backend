package com.auction.auction_backend.modules.notification.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.modules.notification.entity.Notification;
import com.auction.auction_backend.modules.notification.service.NotificationService;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return notificationService.subscribe(userPrincipal.getId());
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Notification>>> getHistory(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(BaseResponse.success(notificationService.getMyNotifications(userPrincipal.getId())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<BaseResponse<String>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        notificationService.markAsRead(id, userPrincipal.getId());
        return ResponseEntity.ok(BaseResponse.success("Đã đánh dấu đã đọc"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<BaseResponse<String>> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        notificationService.markAllAsRead(userPrincipal.getId());
        return ResponseEntity.ok(BaseResponse.success("Đã đánh dấu tất cả là đang đọc"));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<BaseResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(BaseResponse.success(notificationService.countUnread(userPrincipal.getId())));
    }

}
