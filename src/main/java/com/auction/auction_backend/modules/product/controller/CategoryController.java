package com.auction.auction_backend.modules.product.controller;

import com.auction.auction_backend.common.api.BaseResponse;
import com.auction.auction_backend.modules.product.entity.Category;
import com.auction.auction_backend.modules.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<BaseResponse<List<Category>>> getAllCategories() {
        return ResponseEntity.ok(BaseResponse.success(categoryService.getAllCategories()));
    }

    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Category>> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(BaseResponse.success(categoryService.createCategory(category)));
    }

    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Category>> updateCategory(@PathVariable Long id,
            @RequestBody Category category) {
        return ResponseEntity.ok(BaseResponse.success(categoryService.updateCategory(id, category)));
    }

    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(BaseResponse.success("Xóa danh mục thành công"));
    }

    @GetMapping("/categories/{id}/children")
    public ResponseEntity<BaseResponse<List<Category>>> getChildCategories(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(categoryService.getChildCategories(id)));
    }
}
