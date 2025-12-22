package com.auction.auction_backend.common.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private int code;
    private String message;
    private String path;
}
