package com.auction.auction_backend.modules.product.service.impl;

import com.auction.auction_backend.common.api.PageResponse;
import com.auction.auction_backend.common.exception.AppException;
import com.auction.auction_backend.common.exception.ErrorCode;
import com.auction.auction_backend.modules.product.dto.request.AnswerQARequest;
import com.auction.auction_backend.modules.product.dto.request.CreateQARequest;
import com.auction.auction_backend.modules.product.dto.response.ProductQAResponse;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.entity.ProductQA;
import com.auction.auction_backend.modules.product.repository.ProductQARepository;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import com.auction.auction_backend.modules.product.service.ProductQAService;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductQAServiceImpl implements ProductQAService {
        private final ProductQARepository productQARepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;
        private final com.auction.auction_backend.modules.notification.service.EmailService emailService;

        @Override
        @Transactional
        public void createQuestion(CreateQARequest request) {
                UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                User asker = userRepository.findById(currentUser.getId())
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

                ProductQA qa = ProductQA.builder()
                                .product(product)
                                .asker(asker)
                                .question(request.getQuestion())
                                .isAnswered(false)
                                .build();

                productQARepository.save(qa);

                // Send Email to Seller
                String productLink = "http://localhost:5173/products/" + product.getId(); // TODO: Use config from
                                                                                          // properties
                emailService.sendQuestionNotification(
                                product.getSeller().getEmail(),
                                product.getTitle(),
                                request.getQuestion(),
                                productLink);
        }

        @Override
        @Transactional
        public void answerQuestion(Long qaId, AnswerQARequest request) {
                UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();

                ProductQA qa = productQARepository.findById(qaId)
                                .orElseThrow(() -> new RuntimeException("Câu hỏi không tồn tại"));

                if (!qa.getProduct().getSeller().getId().equals(currentUser.getId())) {
                        throw new RuntimeException("Bạn không phải là người bán của sản phẩm này");
                }

                qa.setAnswer(request.getAnswer());
                qa.setAnswered(true);
                productQARepository.save(qa);

                // Send Email to Asker
                String productLink = "http://localhost:5173/products/" + qa.getProduct().getId(); // TODO: Use config
                                                                                                  // from properties
                emailService.sendAnswerNotification(
                                qa.getAsker().getEmail(),
                                qa.getProduct().getTitle(),
                                qa.getQuestion(),
                                request.getAnswer(),
                                productLink);
        }

        @Override
        public PageResponse<ProductQAResponse> getProductQAs(Long productId, int page, int size) {
                Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size);
                Page<ProductQA> pageResult = productQARepository.findByProductIdOrderByCreatedAtDesc(productId,
                                pageable);

                List<ProductQAResponse> response = pageResult.getContent().stream()
                                .map(ProductQAResponse::fromEntity)
                                .toList();

                return PageResponse.<ProductQAResponse>builder()
                                .content(response)
                                .page(pageResult.getNumber() + 1)
                                .size(pageResult.getSize())
                                .totalElements(pageResult.getTotalElements())
                                .totalPages(pageResult.getTotalPages())
                                .build();
        }
}
