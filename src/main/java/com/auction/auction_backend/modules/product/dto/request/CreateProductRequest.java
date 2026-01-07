package com.auction.auction_backend.modules.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String title;

    private String description;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @NotNull(message = "Giá khởi điểm không đưcọ để trống")
    @Min(value = 1000, message = "Giá khởi điểm tối thiểu 1000đ")
    private BigDecimal startPrice;

    @NotNull(message = "Bước giá không được để trống")
    private BigDecimal stepPrice;

    private BigDecimal buyNowPrice;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime endAt;

    private List<String> imageUrls;

    private Boolean autoExtendEnabled;

    private Boolean allowUnratedBidder;
}
