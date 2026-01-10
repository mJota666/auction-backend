package com.auction.auction_backend.modules.product.controller;

import com.auction.auction_backend.modules.product.service.ProductStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/streams")
@RequiredArgsConstructor
public class ProductStreamController {

    private final ProductStreamService productStreamService;

    @GetMapping(value = "/products/{productId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamProductUpdates(@PathVariable Long productId) {
        return productStreamService.subscribe(productId);
    }
}
