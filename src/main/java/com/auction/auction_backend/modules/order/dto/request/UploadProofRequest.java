package com.auction.auction_backend.modules.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UploadProofRequest {
    @NotBlank(message = "Đường dẫn minh chứng không được để trống")
    private String proofUrl;
}
