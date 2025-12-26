package com.auction.auction_backend.modules.bidding;

import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.common.enums.UserRole;
import com.auction.auction_backend.modules.bidding.dto.request.PlaceBidRequest;
import com.auction.auction_backend.modules.bidding.service.impl.BidService;
import com.auction.auction_backend.modules.product.entity.Category;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.repository.CategoryRepository;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import com.auction.auction_backend.security.userdetail.UserPrincipal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BiddingScenarioTest {
    @Autowired
    private BidService bidService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private void mockLogin(User user) {
        UserPrincipal principal = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private User createUser(String email, String name) {
        return userRepository.save(User.builder()
                .email(email)
                .password("hashedpass")
                .fullName(name)
                .role(UserRole.BIDDER)
                .active(true)
                .build());
    }
    @Test
    public void testComplexAutoBiddingScenario() {
        User user1 = createUser("user1@test.com", "Bidder #1");
        User user2 = createUser("user2@test.com", "Bidder #2");
        User user3 = createUser("user3@test.com", "Bidder #3");
        User user4 = createUser("user4@test.com", "Bidder #4");

        User seller = createUser("seller@test.com", "Seller");

        Category category = categoryRepository.save(Category.builder()
                .name("Phone")
                .build());

        Product product = productRepository.save(Product.builder()
                .title("iPhone 15 Pro Max")
                .titleNormalized("iPhone 15 Pro Max")
                .description("<p>Test description</p>")
                .startPrice(new BigDecimal("10000000"))
                .currentPrice(new BigDecimal("10000000"))
                .stepPrice(new BigDecimal("100000"))
                .startAt(LocalDateTime.now().minusHours(1))
                .endAt(LocalDateTime.now().plusHours(1))
                .status(ProductStatus.ACTIVE)
                .seller(seller)
                .category(category)
                .build());
        Long productId = product.getId();
        // --- BẮT ĐẦU TEST THEO KỊCH BẢN ---

        // =================================================================
        // BƯỚC 1: Bidder #1 vào giá 11,000,000
        // Kỳ vọng: Giá SP = 10tr, Winner = #1
        // =================================================================
        System.out.println("--- STEP 1: User 1 bids 11.0m ---");
        mockLogin(user1);
        placeBid(productId, new BigDecimal("11000000"));

        Product p1 = productRepository.findById(productId).get();
        Assertions.assertEquals(0, new BigDecimal("10000000").compareTo(p1.getCurrentPrice()), "Step 1: Giá phải là 10tr");
        Assertions.assertEquals(user1.getId(), p1.getCurrentWinner().getId(), "Step 1: Winner là User 1");


        // =================================================================
        // BƯỚC 2: Bidder #2 vào giá 10,800,000
        // Kỳ vọng: Giá SP = 10.8tr + 0.1tr = 10.9tr, Winner = #1 (Vì 11 > 10.9)
        // =================================================================
        System.out.println("--- STEP 2: User 2 bids 10.8m ---");
        mockLogin(user2);
        placeBid(productId, new BigDecimal("10800000"));

        Product p2 = productRepository.findById(productId).get();
        Assertions.assertEquals(0, new BigDecimal("10900000").compareTo(p2.getCurrentPrice()), "Step 2: Giá phải là 10.9tr (Max User 2 + Step)");
        Assertions.assertEquals(user1.getId(), p2.getCurrentWinner().getId(), "Step 2: Winner vẫn là User 1");


        // =================================================================
        // BƯỚC 3: Bidder #3 vào giá 11,500,000
        // Kỳ vọng: User 3 đấu với User 1 (Max 11tr).
        // Giá mới = Max User 1 (11tr) + Step (0.1tr) = 11.1tr.
        // Winner = User 3
        // =================================================================
        System.out.println("--- STEP 3: User 3 bids 11.5m ---");
        mockLogin(user3);
        placeBid(productId, new BigDecimal("11500000"));

        Product p3 = productRepository.findById(productId).get();
        Assertions.assertEquals(0, new BigDecimal("11100000").compareTo(p3.getCurrentPrice()), "Step 3: Giá phải là 11.1tr");
        Assertions.assertEquals(user3.getId(), p3.getCurrentWinner().getId(), "Step 3: Winner là User 3");


        // =================================================================
        // BƯỚC 4: Bidder #4 vào giá 11,500,000
        // Kỳ vọng: User 4 đấu với User 3 (Max 11.5tr).
        // Max User 4 cũng là 11.5tr.
        // Logic Ceiling: Giá đẩy lên 11.5tr. Người đến trước (User 3) thắng.
        // =================================================================
        System.out.println("--- STEP 4: User 4 bids 11.5m ---");
        mockLogin(user4);
        placeBid(productId, new BigDecimal("11500000"));

        Product p4 = productRepository.findById(productId).get();
        Assertions.assertEquals(0, new BigDecimal("11500000").compareTo(p4.getCurrentPrice()), "Step 4: Giá chạm trần 11.5tr");
        Assertions.assertEquals(user3.getId(), p4.getCurrentWinner().getId(), "Step 4: Winner vẫn là User 3 (ưu tiên người trước)");


        // =================================================================
        // BƯỚC 5: Bidder #4 tăng giá lên 11,700,000
        // Kỳ vọng: User 4 (11.7) đấu với User 3 (11.5).
        // Giá mới = Max User 3 (11.5) + Step (0.1) = 11.6tr.
        // Winner = User 4.
        // =================================================================
        System.out.println("--- STEP 5: User 4 bids 11.7m ---");
        mockLogin(user4);
        placeBid(productId, new BigDecimal("11700000"));

        Product p5 = productRepository.findById(productId).get();
        Assertions.assertEquals(0, new BigDecimal("11600000").compareTo(p5.getCurrentPrice()), "Step 5: Giá phải là 11.6tr");
        Assertions.assertEquals(user4.getId(), p5.getCurrentWinner().getId(), "Step 5: Winner đổi thành User 4");

        System.out.println("✅ TEST PASSED: KỊCH BẢN CHẠY ĐÚNG 100%");
    }
    private void placeBid(Long productId, BigDecimal amount) {
        PlaceBidRequest request = new PlaceBidRequest();
        request.setProductId(productId);
        request.setAmount(amount);
        bidService.placeBid(request);
    }
}
