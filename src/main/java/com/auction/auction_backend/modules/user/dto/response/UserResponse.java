package com.auction.auction_backend.modules.user.dto.response;

import com.auction.auction_backend.common.enums.UserRole;
import com.auction.auction_backend.modules.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private String address;
    private LocalDate dob;
    private boolean active;
    private int ratingPositive;
    private int ratingNegative;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .address(user.getAddress())
                .dob(user.getDob())
                .active(user.isActive())
                .ratingPositive(user.getRatingPositive())
                .ratingNegative(user.getRatingNegative())
                .build();
    }
}
