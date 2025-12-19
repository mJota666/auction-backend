package com.auction.auction_backend.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String message;
    private T data;

    public static<T> BaseResponse<T> success(T data) {
        return BaseResponse.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }
}
