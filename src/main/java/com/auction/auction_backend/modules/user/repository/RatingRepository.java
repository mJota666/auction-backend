package com.auction.auction_backend.modules.user.repository;

import com.auction.auction_backend.modules.user.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Page<Rating> findByRatedUserId(Long ratedUserId, Pageable pageable);

    void deleteByRaterId(Long raterId);

    void deleteByRatedUserId(Long ratedUserId);
}
