package com.auction.auction_backend.modules.notification.service;

import com.auction.auction_backend.modules.notification.entity.Notification;
import com.auction.auction_backend.modules.notification.repository.NotificationRepository;
import com.auction.auction_backend.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        return emitter;
    }

    public void sendNotification(User receiver, String title, String message, String url, String type) {
        Notification notification = Notification.builder()
                .user(receiver)
                .title(title)
                .message(message)
                .relatedUrl(url)
                .type(type)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        Long rid = receiver.getId();
        SseEmitter emitter = emitters.get(rid);

        log.info("Send notif -> receiverId={}, hasEmitter={}, emittersSize={}",
                rid, emitter != null, emitters.size());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (IOException e) {
                log.warn("SSE send failed, remove emitter for userId={}", rid, e);
                emitters.remove(rid);
            }
        }
    }

    public List<Notification> getMyNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(Long notificationId, long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Thông báo không tồn tại"));
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền này");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unreadList = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().filter(n -> !n.isRead()).toList();
        unreadList.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unreadList);
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}
