package com.auction.auction_backend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse
                .builder()
                .status(errorCode.getStatusCode().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        ErrorResponse response = ErrorResponse.builder()
                .status(400)
                .code(1001)
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            org.springframework.security.authentication.BadCredentialsException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .status(401)
                .code(401)
                .message("Email hoặc mật khẩu không chính xác")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(401).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnwantedException(Exception e, HttpServletRequest request) {
        e.printStackTrace();
        ErrorResponse response = ErrorResponse.builder()
                .status(500)
                .code(9999)
                .message("Lỗi hệ thống nội bộ: " + e.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleConcurrencyError(ObjectOptimisticLockingFailureException e,
            HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(409)
                .code(4009)
                .message("Giá sản phẩm vừa được cập nhật bởi người khác. Vui lòng thử lại")
                .path(request.getRequestURI())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(409).body(response);
    }
}
