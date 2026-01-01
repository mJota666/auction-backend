package com.auction.auction_backend.modules.product.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.common.api.PageResponse;
import com.auction.auction_backend.modules.product.dto.request.AnswerQARequest;
import com.auction.auction_backend.modules.product.dto.request.CreateQARequest;
import com.auction.auction_backend.modules.product.dto.response.ProductQAResponse;
import com.auction.auction_backend.modules.product.service.ProductQAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductQAController {
    private final ProductQAService productQAService;

    @PostMapping("/qa")
    public ResponseEntity<BaseResponse<String>> createQuestion(@RequestBody @Valid CreateQARequest request) {
        productQAService.createQuestion(request);
        return ResponseEntity.ok(BaseResponse.success("Gửi câu hỏi thành công"));
    }

    @PutMapping("/qa/{id}/answer")
    public ResponseEntity<BaseResponse<String>> answerQuestion(
            @PathVariable Long id,
            @RequestBody @Valid AnswerQARequest request) {
        productQAService.answerQuestion(id, request);
        return ResponseEntity.ok(BaseResponse.success("Trả lời câu hỏi thành công"));
    }

    @GetMapping("/{productId}/qa")
    public ResponseEntity<BaseResponse<PageResponse<ProductQAResponse>>> getProductQAs(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(BaseResponse.success(productQAService.getProductQAs(productId, page, size)));
    }
}
