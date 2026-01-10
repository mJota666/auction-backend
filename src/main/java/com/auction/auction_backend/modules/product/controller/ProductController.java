package com.auction.auction_backend.modules.product.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.common.api.PageResponse;
import com.auction.auction_backend.modules.bidding.dto.response.BidHistoryResponse;
import com.auction.auction_backend.modules.product.dto.request.CreateProductRequest;
import com.auction.auction_backend.modules.product.dto.request.ProductSearchCriteria;
import com.auction.auction_backend.modules.product.dto.response.ProductDetailResponse;
import com.auction.auction_backend.modules.product.dto.response.ProductResponse;
import com.auction.auction_backend.modules.product.service.impl.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping({ "", "/search" })
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> searchProducts(
            @ModelAttribute ProductSearchCriteria criteria) {
        criteria.setIncludeAllStatuses(false);
        criteria.setStatus(com.auction.auction_backend.common.enums.ProductStatus.ACTIVE);
        return ResponseEntity.ok(BaseResponse.success(productService.searchProducts(criteria)));
    }

    @GetMapping("/seller/me")
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> getMyProducts(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.auction.auction_backend.security.userdetail.UserPrincipal currentUser,
            @ModelAttribute ProductSearchCriteria criteria) {
        criteria.setSellerId(currentUser.getId());
        criteria.setIncludeAllStatuses(true); // Sellers can see all their products (active, draft, ended, etc.)
        return ResponseEntity.ok(BaseResponse.success(productService.searchProducts(criteria)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<String>> createProduct(@RequestBody @Valid CreateProductRequest request) {
        productService.createProduct(request);
        return ResponseEntity.ok(BaseResponse.success("Tạo sản phẩm đấu giá thành công"));
    }

    @GetMapping("/top/ending-soon")
    public ResponseEntity<BaseResponse<List<ProductResponse>>> getTopEndingSoon() {
        return ResponseEntity.ok(BaseResponse.success(productService.getTop5EndingSoon()));
    }

    @GetMapping("/top/highest-price")
    public ResponseEntity<BaseResponse<List<ProductResponse>>> getTopHighestPrice() {
        return ResponseEntity.ok(BaseResponse.success(productService.getTop5HighestPrice()));
    }

    @GetMapping("/top/most-bids")
    public ResponseEntity<BaseResponse<List<ProductResponse>>> getTopMostBids() {
        return ResponseEntity.ok(BaseResponse.success(productService.getTop5MostBids()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductDetailResponse>> getProductDetail(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(productService.getProductDetail(id)));
    }

    @GetMapping("/{id}/bids")
    public ResponseEntity<BaseResponse<List<BidHistoryResponse>>> getProductBids(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(productService.getProductBidHistory(id)));
    }

    @PutMapping("/{id}/description")
    public ResponseEntity<BaseResponse<String>> appendDescription(
            @PathVariable Long id,
            @RequestBody @Valid com.auction.auction_backend.modules.product.dto.request.AppendDescriptionRequest request) {
        productService.appendDescription(id, request.getContent());
        return ResponseEntity.ok(BaseResponse.success("Bổ sung mô tả thành công"));
    }
}
