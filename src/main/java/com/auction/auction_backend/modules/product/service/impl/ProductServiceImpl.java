package com.auction.auction_backend.modules.product.service.impl;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.common.exception.AppException;
import com.auction.auction_backend.common.exception.ErrorCode;
import com.auction.auction_backend.common.utils.AppUtils;
import com.auction.auction_backend.modules.product.dto.request.CreateProductRequest;
import com.auction.auction_backend.modules.product.dto.request.ProductSearchCriteria;
import com.auction.auction_backend.modules.product.dto.response.ProductResponse;
import com.auction.auction_backend.modules.product.entity.Category;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.entity.ProductImage;
import com.auction.auction_backend.modules.product.repository.CategoryRepository;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import com.auction.auction_backend.modules.product.repository.spec.ProductSpecification;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public void createProduct(CreateProductRequest request) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User seller = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .title(request.getTitle())
                .titleNormalized(AppUtils.removeAccent(request.getTitle()))
                .description(request.getDescription())
                .startPrice(request.getStartPrice())
                .currentPrice(request.getStartPrice())
                .stepPrice(request.getStepPrice())
                .buyNowPrice(request.getBuyNowPrice())
                .startAt(LocalDateTime.now())
                .endAt(request.getEndAt())
                .status(ProductStatus.ACTIVE)
                .seller(seller)
                .category(category)
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ProductImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                images.add(ProductImage.builder()
                        .product(product)
                        .url(request.getImageUrls().get(i))
                        .cover(i == 0)
                        .build());
            }
            product.setImages(images);
        }
        productRepository.save(product);
    }

    @Override
    public Page<ProductResponse> searchProducts(ProductSearchCriteria criteria) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // Mặc định là mới nhất
        if (criteria.getSortBy() != null) {
            switch (criteria.getSortBy()) {
                case "price_asc" -> sort = Sort.by(Sort.Direction.ASC, "currentPrice");
                case "price_desc" -> sort = Sort.by(Sort.Direction.DESC, "currentPrice");
                case "end_at_asc" -> sort = Sort.by(Sort.Direction.ASC, "endAt"); // Sắp hết hạn lên đầu
            }
        }
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
        Specification<Product> spec = ProductSpecification.getSpecification(criteria);
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(ProductResponse::fromEntity);
    }

    @Override
    public List<ProductResponse> getTop5EndingSoon() {
        return productRepository.findTop5ByStatusOrderByEndAtAsc(ProductStatus.ACTIVE)
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Override
    public List<ProductResponse> getTop5HighestPrice() {
        return productRepository.findTop5ByStatusOrderByCurrentPriceDesc(ProductStatus.ACTIVE)
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Override
    public List<ProductResponse> getTop5MostBids() {
        return productRepository.findTopMostBidded(PageRequest.of(0,5))
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
}
