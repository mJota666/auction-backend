package com.auction.auction_backend.modules.user.dto.response;

import com.auction.auction_backend.modules.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PublicUserResponse {
    private Long id;
    private String fullName;
    private String avatarUrl; // Assuming logic to get avatar OR null if not impl yet
    private LocalDateTime createdAt;
    private int ratingPositive;
    private int ratingNegative;

    public static PublicUserResponse fromEntity(User user) {
        return PublicUserResponse.builder()
                .id(user.getId())
                // Only return full name if needed, or mask it? Requirement says "fullName" for
                // profile
                // usually public profile shows display name. Let's return full name as
                // requested.
                .fullName(user.getFullName())
                .ratingPositive(user.getRatingPositive())
                .ratingNegative(user.getRatingNegative())
                .createdAt(user.getCreatedAt())
                // .avatarUrl(...) // Placeholder for now
                .build();
    }
}
