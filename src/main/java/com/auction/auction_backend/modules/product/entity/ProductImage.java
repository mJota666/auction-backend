package com.auction.auction_backend.modules.product.entity;

import com.auction.auction_backend.common.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Auditable;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_images")
public class ProductImage extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(name = "is_cover")
    @Builder.Default
    private boolean cover = false;
}
