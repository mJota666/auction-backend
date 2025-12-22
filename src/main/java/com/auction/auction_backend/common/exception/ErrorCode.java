package com.auction.auction_backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 1. Lỗi hệ thống chung
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống chưa được định nghĩa", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Sai key xác thực", HttpStatus.BAD_REQUEST),

    // 2. Lỗi Auth/User
    USER_NOT_FOUND(1002, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(1003, "Email này đã được đăng ký", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1004, "Vui lòng đăng nhập", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),

    // 3. Lỗi Product/Category
    PRODUCT_NOT_FOUND(2001, "Sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(2002, "Danh mục không tồn tại", HttpStatus.NOT_FOUND),

    // 4. Lỗi Đấu giá (Bidding)
    BID_AMOUNT_INVALID(3001, "Giá đặt không hợp lệ", HttpStatus.BAD_REQUEST),
    AUCTION_ENDED(3002, "Phiên đấu giá đã kết thúc", HttpStatus.BAD_REQUEST),
    SELF_BIDDING(3003, "Không thể tự đấu giá sản phẩm của mình", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
