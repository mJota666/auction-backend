package com.auction.auction_backend.modules.product.service.impl;

import com.auction.auction_backend.modules.product.entity.Category;
import com.auction.auction_backend.modules.product.repository.CategoryRepository;
import com.auction.auction_backend.modules.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final com.auction.auction_backend.modules.product.repository.ProductRepository productRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Danh mục không tồn tại");
        }

        if (productRepository.countByCategoryId(id) > 0) {
            throw new RuntimeException("Không thể xóa danh mục đã có sản phẩm");
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại")); // Should use ErrorCode but keeping
                                                                                    // simple

        category.setName(categoryRequest.getName());
        category.setParentId(categoryRequest.getParentId());

        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getChildCategories(Long parentId) {
        return categoryRepository.findAllByParentId(parentId);
    }
}
