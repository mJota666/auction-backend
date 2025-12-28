package com.auction.auction_backend.modules.product.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.modules.product.dto.request.CreateProductRequest;
import com.auction.auction_backend.modules.product.dto.request.ProductSearchCriteria;
import com.auction.auction_backend.modules.product.dto.response.ProductResponse;
import com.auction.auction_backend.modules.product.service.impl.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> searchProducts(
            @ModelAttribute ProductSearchCriteria criteria
    ) {
        return ResponseEntity.ok(BaseResponse.success(productService.searchProducts(criteria)));
    }
    @PostMapping
    public ResponseEntity<BaseResponse<String>> createProduct(@RequestBody @Valid CreateProductRequest request) {
        productService.createProduct(request);
        return ResponseEntity.ok(BaseResponse.success("Tạo sản phẩm đấu giá thành công"));    }
}
