package com.auction.auction_backend.modules.product.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.modules.product.dto.request.CreateProductRequest;
import com.auction.auction_backend.modules.product.service.impl.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<BaseResponse<String>> createProduct(@RequestBody @Valid CreateProductRequest request) {
        productService.createProduct(request);
        return ResponseEntity.ok(BaseResponse.success("Tạo sản phẩm đấu giá thành công"));    }
}
