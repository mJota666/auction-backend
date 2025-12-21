package com.auction.auction_backend.modules.auth.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.modules.auth.dto.request.LoginRequest;
import com.auction.auction_backend.modules.auth.dto.request.RegisterRequest;
import com.auction.auction_backend.modules.auth.dto.response.AuthResponse;
import com.auction.auction_backend.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<String>> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(BaseResponse.success("Đăng ký thành công"));
    }
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
