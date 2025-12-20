package com.auction.auction_backend.modules.user.entity;

import com.auction.auction_backend.common.enums.UserRole;
import com.auction.auction_backend.common.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Column(name = "full_name", nullable=false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String address;

    private LocalDate dob;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @Column(name = "rating_pos")
    @Builder.Default
    private int ratingPositive = 0;

    @Column(name = "rating_neg")
    @Builder.Default
    private int ratingNegative = 0;

}
