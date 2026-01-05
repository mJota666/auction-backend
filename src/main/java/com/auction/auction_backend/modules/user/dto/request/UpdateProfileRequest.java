package com.auction.auction_backend.modules.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @jakarta.validation.constraints.Email(message = "Email không hợp lệ")
    private String email;

    private String address;

    @Past(message = "Ngày sinh phải trong quá khứ")
    private LocalDate dob;
}
