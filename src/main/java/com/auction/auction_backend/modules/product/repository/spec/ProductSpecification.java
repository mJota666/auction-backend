package com.auction.auction_backend.modules.product.repository.spec;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.modules.product.dto.request.ProductSearchCriteria;
import com.auction.auction_backend.modules.product.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> getSpecification(ProductSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), ProductStatus.ACTIVE));

            if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
                String keywordLike = "%" + criteria.getKeyword().toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), keywordLike);
                Predicate normalizedTitlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("titleNormalized")), keywordLike);

                predicates.add(criteriaBuilder.or(titlePredicate, normalizedTitlePredicate));
            }

            if (criteria.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), criteria.getCategoryId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
