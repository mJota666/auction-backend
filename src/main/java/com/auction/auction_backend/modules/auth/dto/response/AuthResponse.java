package com.auction.auction_backend.modules.auth.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String email;
    private String fullname;
    private String role;
    private int ratingPositive;
    private int ratingNegative;
    private String address;
    private LocalDate dob;
}
