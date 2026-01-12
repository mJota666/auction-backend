package com.auction.auction_backend.common.persistence;

import com.auction.auction_backend.common.enums.BidType;
import com.auction.auction_backend.common.enums.OrderStatus;
import com.auction.auction_backend.common.enums.ProductStatus;
import com.auction.auction_backend.common.enums.UserRole;
import com.auction.auction_backend.common.utils.AppUtils;
import com.auction.auction_backend.modules.bidding.entity.Bid;
import com.auction.auction_backend.modules.bidding.repository.BidRepository;
import com.auction.auction_backend.modules.order.repository.OrderRepository;
import com.auction.auction_backend.modules.product.entity.Category;
import com.auction.auction_backend.modules.product.entity.Product;
import com.auction.auction_backend.modules.product.entity.ProductImage;
import com.auction.auction_backend.modules.product.repository.CategoryRepository;
import com.auction.auction_backend.modules.product.repository.ProductRepository;
import com.auction.auction_backend.modules.user.entity.Rating;
import com.auction.auction_backend.modules.user.entity.User;
import com.auction.auction_backend.modules.user.repository.RatingRepository;
import com.auction.auction_backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

        private final UserRepository userRepository;
        private final CategoryRepository categoryRepository;
        private final ProductRepository productRepository;
        private final BidRepository bidRepository;
        private final PasswordEncoder passwordEncoder;
        private final OrderRepository orderRepository;
        private final RatingRepository ratingRepository;

        @Override
        @Transactional
        public void run(String... args) {
                // ==== 0) OPTIONAL: FORCE RESEED ====
                // Run with: -Dapp.seed.force=true
                boolean force = Boolean.parseBoolean(System.getProperty("app.seed.force", "false"));
                if (force) {
                        log.warn("FORCE reseed enabled -> clearing database...");
                        clearDatabase();
                } else {
                        // ==== 0.1) SAFER "already seeded" check ====
                        // Tránh case DB có mỗi user => seeder skip và ratings không được tạo
                        if (userRepository.existsByEmail("admin@gmail.com")) {
                                log.info("Database already seeded (admin@gmail.com exists). Skipping...");
                                return;
                        }
                }

                log.info("Seeding database...");

                // 1. Users (NOTE: ratingPositive/Negative = 0, sẽ tính lại từ bảng Rating ở
                // cuối)
                User admin = createUser("Admin User", "admin@gmail.com", UserRole.ADMIN);
                User seller = createUser("Seller User", "seller@gmail.com", UserRole.SELLER);
                User seller2 = createUser("Tech Shop Official", "techshop@gmail.com", UserRole.SELLER);
                User bidder1 = createUser("Nguyen Van BidderOne", "bidder1@gmail.com", UserRole.BIDDER);
                User bidder2 = createUser("Tran Thi BidderTwo", "bidder2@gmail.com", UserRole.BIDDER);

                // 2. Categories
                Category electronics = createCategory("Điện tử", null);
                Category tablets = createCategory("Máy tính bảng", electronics.getId());

                Category phones = createCategory("Điện thoại", electronics.getId());
                Category laptops = createCategory("Laptop", electronics.getId());
                Category accessories = createCategory("Phụ kiện", electronics.getId());

                Category fashion = createCategory("Thời trang", null);
                Category menFashion = createCategory("Thời trang nam", fashion.getId());
                Category womenFashion = createCategory("Thời trang nữ", fashion.getId());

                Category homeLiving = createCategory("Nhà cửa & Đời sống", null);
                Category furniture = createCategory("Nội thất", homeLiving.getId());
                Category decoration = createCategory("Trang trí", homeLiving.getId());

                Category sports = createCategory("Thể thao & Du lịch", null);
                Category gym = createCategory("Gym & Yoga", sports.getId());

                Category books = createCategory("Sách & Văn phòng phẩm", null);

                // 3. Products (giữ như bạn seed)
                // --- Electronics ---
                createProduct(seller, tablets, "iPad Pro 11 inch M4 WiFi 256GB",
                                "<p><strong>iPad Pro M4</strong> siêu mỏng nhẹ, hiệu năng cực mạnh cho học tập & sáng tạo.</p>"
                                                + "<ul>"
                                                + "<li>Chip Apple M4</li>"
                                                + "<li>Màn hình Ultra Retina XDR (OLED)</li>"
                                                + "<li>Hỗ trợ Apple Pencil Pro</li>"
                                                + "</ul>",
                                new BigDecimal("10000000"), new BigDecimal("29000000"),
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/f/r/frame_100_1_2__2_2.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/ipad-pro-m4-11-inch_3_.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/ipad-pro-m4-11-inch_3_.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/ipad-pro-m4-11-inch_6_.png"));

                createProduct(seller, phones, "iPhone 15 Pro Max Titanium 256GB",
                                "<p><strong>iPhone 15 Pro Max</strong> thiết kế titan chuẩn hàng không vũ trụ.</p><ul><li>Chip A17 Pro mạnh mẽ.</li><li>Camera 48MP chuyên nghiệp.</li><li>Nút Action mới.</li></ul>",
                                new BigDecimal("28000000"), new BigDecimal("35000000"),
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_3.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_4__1.jpg",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_5__1.jpg",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_6__1.jpg"),
                                bidder1, bidder2);

                createProduct(seller, phones, "Samsung Galaxy S24 Ultra 512GB",
                                "<p>Quyền năng Galaxy AI. Khung viền Titan bền bỉ.</p>",
                                new BigDecimal("25000000"), new BigDecimal("32000000"),
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/s/ss-s24-ultra-den-600_1_1_1_1_1.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung_galaxy_s24_ultra_512-_16_1_1_1_1.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung_galaxy_s24_ultra_256gb_-_15_1_1_1_1_1.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung_galaxy_s24_ultra_256gb_-_9_1_1_1_1_1.png"),
                                bidder1, bidder2);

                createProduct(seller, laptops, "MacBook Pro 14 M3 Max",
                                "<p>Laptop chuyên nghiệp cho dân đồ họa.</p><p>Hiệu năng vượt trội với chip M3 Max.</p>",
                                new BigDecimal("45000000"), new BigDecimal("60000000"),
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/m/a/macbook-pro-14-inch-m3-2023_1__2.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/f/r/frame_104_6_5.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/m/a/macbook-pro-14-inch-m3-2023_5__2.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/m/a/macbook-pro-14-inch-m3-2023_8__2.png"),
                                bidder1, bidder2);

                createProduct(seller2, laptops, "Dell XPS 13 Plus",
                                "<p>Thiết kế tương lai, hiệu năng mạnh mẽ.</p>",
                                new BigDecimal("30000000"), new BigDecimal("40000000"),
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_ng_n_3__7_102.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_ng_n_7__4_128.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_ng_n_4__7_156.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_ng_n_5__9_163.png"),
                                bidder1, bidder2);

                createProduct(seller2, accessories, "Tai nghe Sony WH-1000XM5",
                                "<p>Chống ồn đỉnh cao, âm thanh Hi-Res.</p>",
                                new BigDecimal("5000000"), new BigDecimal("8000000"),
                                List.of(
                                                "https://shopdunk.com/images/thumbs/0018913_den_550.jpeg",
                                                "https://shopdunk.com/images/thumbs/0018914_den_550.jpeg",
                                                "https://shopdunk.com/images/thumbs/0018915_den_550.jpeg",
                                                "https://shopdunk.com/images/thumbs/0018916_den_550.jpeg"),
                                bidder1, bidder2);

                createProduct(seller, accessories, "Bàn phím cơ Keychron K2 Pro",
                                "<p>Bàn phím cơ không dây custom, LED RGB.</p>",
                                new BigDecimal("1500000"), new BigDecimal("2500000"),
                                List.of(
                                                "https://owlgaming.vn/wp-content/uploads/2023/02/1-3.jpg",
                                                "https://owlgaming.vn/wp-content/uploads/2023/02/2-3.jpg",
                                                "https://owlgaming.vn/wp-content/uploads/2023/02/4-3.jpg",
                                                "https://owlgaming.vn/wp-content/uploads/2023/02/6-3.jpg"),
                                bidder1, bidder2);

                // --- Fashion ---
                createProduct(seller, menFashion, "Áo Thun Gucci Chính Hãng",
                                "<p>Áo thun cotton cao cấp, họa tiết logo đặc trưng.</p>",
                                new BigDecimal("500000"), new BigDecimal("2000000"),
                                List.of(
                                                "https://www.mytheresa.com/media/1094/1238/100/72/P01050993.jpg",
                                                "https://www.mytheresa.com/media/1094/1238/100/72/P01050993_b1.jpg",
                                                "https://www.mytheresa.com/media/1094/1238/100/72/P01050993_b2.jpg",
                                                "https://www.mytheresa.com/media/1094/1238/100/72/P01050993_d2.jpg"),
                                bidder1, bidder2);

                createProduct(seller, menFashion, "Giày Nike Air Jordan 1 High",
                                "<p>Huyền thoại trở lại. Phối màu Chicago kinh điển.</p>",
                                new BigDecimal("3000000"), new BigDecimal("5000000"),
                                List.of(
                                                "https://www.nicekicks.com/files/2023/10/air-jordan-1-high-og-chicago-reimagined-lost-and-found.jpg",
                                                "https://static.nike.com/a/images/f_auto/dpr_1.0%2Ccs_srgb/w_1212%2Cc_limit/89c121fc-3d07-4de0-aef6-bcc9c2764a2c/air-jordan-1-2022-lost-and-found-chicago-the-inspiration-behind-the-design.jpg",
                                                "https://cdn.flightclub.com/3000/TEMPLATE/307016/1.jpg",
                                                "https://images.squarespace-cdn.com/content/v1/5c97c2c834c4e28454c66e64/3dbf148e-7925-4274-8d30-8042f364fb8b/60EB4EAF-9556-44EA-98E1-605DD5088A30.jpeg"),
                                bidder1, bidder2);

                createProduct(seller, womenFashion, "Túi Xách Chanel Classic Flap",
                                "<p>Biểu tượng thời trang vượt thời gian.</p>",
                                new BigDecimal("100000000"), new BigDecimal("150000000"),
                                List.of(
                                                "https://a.1stdibscdn.com/chanel-jumbo-classic-flap-cc-quilted-black-lambskin-shoulder-bag-crossbody-for-sale/v_29522/v_169202421662646906560/v_16920242_1662646908001_bg_processed.jpg",
                                                "https://a.1stdibscdn.com/chanel-2001-vintage-black-lambskin-medium-classic-double-flap-bag-shw-64681-for-sale/v_29472/v_229889721717064316985/v_22988972_1717064317641_bg_processed.jpg",
                                                "https://luxuryevermore.com/cdn/shop/files/c59a947d08a778c8cdfdea79fd92ae83.jpg?v=1739636062&width=1600",
                                                "https://cdn.mos.cms.futurecdn.net/whowhatwear/posts/309447/chanel-classic-flap-bag-309447-1695219308561-main-1024-80.jpg"),
                                bidder1, bidder2);

                createProduct(seller, womenFashion, "Giày Onitsuka Tiger Mexico 66",
                                "<p>Thiết kế retro dễ phối đồ, nhẹ và êm chân, hợp đi chơi hằng ngày.</p>",
                                new BigDecimal("1800000"), new BigDecimal("3200000"),
                                List.of(
                                                "https://images.asics.com/is/image/asics/1183C600_100_SR_RT_GLB?qlt=100&wid=1280&hei=1452&bgc=255,255,255&resMode=bisharp",
                                                "https://images.asics.com/is/image/asics/1183C600_100_SB_FR_GLB?qlt=100&wid=1280&hei=1452&bgc=255,255,255&resMode=bisharp",
                                                "https://images.asics.com/is/image/asics/1183C600_100_SR_LT_GLB?qlt=100&wid=1280&hei=1452&bgc=255,255,255&resMode=bisharp",
                                                "https://images.asics.com/is/image/asics/1183C600_100_SB_BK_GLB?qlt=100&wid=1280&hei=1452&bgc=255,255,255&resMode=bisharp"),
                                bidder1, bidder2);

                // --- Home & Living ---
                createProduct(seller2, furniture, "Ghế Công Thái Học Herman Miller",
                                "<p>Bảo vệ cột sống, thoải mái làm việc cả ngày.</p>",
                                new BigDecimal("20000000"), new BigDecimal("30000000"),
                                List.of(
                                                "https://www.hermanmiller.com/content/dam/hmicom/page_assets/products/aeron_chair/202106/it_prd_ovw_aeron_chair_03.gif.rendition.1600.1600.png",
                                                "https://m.media-amazon.com/images/I/71VVk7m8aIL._AC_SL1500_.jpg",
                                                "https://seated.com.au/wp-content/uploads/2020/08/Herman-Miller-Aeron-Remastered-Posturefit-Polished-Chassis-front.jpg",
                                                "https://image.architonic.com/pro2-3/1420760/aeron-chair-rek-18155-20160811174127084-pro-g-arcit18.jpg"),
                                bidder1, bidder2);

                createProduct(seller2, decoration, "Đèn Ngủ Mặt Trăng 3D",
                                "<p>Mô phỏng bề mặt mặt trăng, ánh sáng dịu nhẹ.</p>",
                                new BigDecimal("200000"), new BigDecimal("500000"),
                                List.of(
                                                "https://static-01.daraz.lk/p/0124d7b576716ce61894584f7cff545d.jpg",
                                                "https://m.media-amazon.com/images/I/61838y%2BM%2BlL._AC_.jpg",
                                                "https://m.media-amazon.com/images/I/61goowr13VL._AC_.jpg",
                                                "https://m.media-amazon.com/images/I/71aVAdhFDvL._AC_.jpg"),
                                bidder1, bidder2);

                createProduct(seller2, homeLiving, "Robot Hút Bụi Roborock S8",
                                "<p>Lực hút 6000Pa, lau rung Sonic Mopping.</p>",
                                new BigDecimal("12000000"), new BigDecimal("18000000"),
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/r/o/roborock-s8-maxv-ultra-1_1.jpg",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/r/o/roborock-s8-maxv-ultra-2.jpg",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/r/o/roborock-s8-maxv-ultra-3.jpg",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/r/o/roborock-s8-maxv-ultra-5_1.jpg"),
                                bidder1, bidder2);

                // --- Sports ---
                createProduct(seller, gym, "Găng Tay Tập Gym Chống Trượt",
                                "<p>Lót đệm lòng bàn tay, thoáng khí, hạn chế chai tay khi tập tạ.</p>",
                                new BigDecimal("90000"), new BigDecimal("220000"),
                                List.of(
                                                "https://assets.adidas.com/images/h_2000,f_auto,q_auto,fl_lossy,c_fill,g_auto/68cbfbb588a9438db223b061fc41b883_9366/Gang_Tay_Aditech_24_Mot_Chiec_DJen_IN6687_01_standard.jpg",
                                                "https://assets.adidas.com/images/h_2000,f_auto,q_auto,fl_lossy,c_fill,g_auto/883f797d71e24c77a15b1365854d9f3f_9366/Gang_Tay_Aditech_24_Mot_Chiec_DJen_IN6687_02_standard_hover.jpg",
                                                "https://assets.adidas.com/images/h_2000,f_auto,q_auto,fl_lossy,c_fill,g_auto/6081ff9ffd264fa5acde26a66ec0ce13_9366/Gang_Tay_Aditech_24_Mot_Chiec_DJen_IN6687_41_detail.jpg",
                                                "https://assets.adidas.com/images/h_2000,f_auto,q_auto,fl_lossy,c_fill,g_auto/a705bdb5a580452b941ace620149010d_9366/Gang_Tay_Aditech_24_Mot_Chiec_DJen_IN6687_42_detail.jpg"),
                                bidder1, bidder2);

                createProduct(seller, sports, "Xe Đạp Địa Hình Giant ATX",
                                "<p>Khung nhôm Aluxx nhẹ, bền bỉ.</p>",
                                new BigDecimal("8000000"), new BigDecimal("12000000"),
                                List.of(
                                                "https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=600&q=80",
                                                "https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=600&q=80",
                                                "https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=600&q=80",
                                                "https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=600&q=80"),
                                bidder1, bidder2);

                createProduct(seller, gym, "Bộ Tạ Tay Điều Chỉnh",
                                "<p>Tiết kiệm diện tích, thay đổi mức tạ linh hoạt.</p>",
                                new BigDecimal("2000000"), new BigDecimal("4000000"),
                                List.of(
                                                "https://www.shonival.com/image/cache/images2/20220407/4fed3eed2bfb5175eec8e59ebb5c352a-800x800.jpg",
                                                "https://www.bowflex.ca/on/demandware.static/-/Sites-nautilus-master-catalog/default/dwa1e50944/images/bowflex/selecttech/552/100131/bowflex-selecttech-552-dumbbell-set.png",
                                                "https://www.plsyeri.com/image/cache/catalog/587-1000x1000.jpg",
                                                "https://corehomefitness.com/cdn/shop/files/CHF_Dumbbell_Twist_Red.jpg?v=1763305626&width=1500"),
                                bidder1, bidder2);

                // --- Books ---
                createProduct(seller2, books, "Sách Clean Code (Bản Tiếng Việt)",
                                "<p>Kinh thánh cho lập trình viên.</p>",
                                new BigDecimal("200000"), new BigDecimal("400000"),
                                List.of(
                                                "https://cdn0.fahasa.com/media/catalog/product/8/9/8936107813361.jpg",
                                                "https://cdn1.fahasa.com/media/catalog/product/c/l/clean_code_tai_ban_3_b4.jpg",
                                                "https://cdn0.fahasa.com/media/flashmagazine/images/page_images/clean_code___ma_sach_va_con_duong_tro_thanh_lap_trinh_vien_gioi_tai_ban_2023/2023_06_22_16_46_16_5-390x510.jpg",
                                                "https://cdn0.fahasa.com/media/flashmagazine/images/page_images/clean_code___ma_sach_va_con_duong_tro_thanh_lap_trinh_vien_gioi_tai_ban_2023/2023_06_22_16_46_16_6-390x510.jpg"),
                                bidder1, bidder2);

                createProduct(seller2, books, "Bộ Sách Harry Potter (Full 7 Tập)",
                                "<p>Trọn bộ huyền thoại thế giới phù thủy.</p>",
                                new BigDecimal("1500000"), new BigDecimal("2500000"),
                                List.of(
                                                "https://thebookland.vn/thumbnail_1200/Complete_Harry_Potter772.jpg",
                                                "https://thebookland.vn/images/1699961827548_The%20Complete%20Harry%20Potter%20Collection%20(%20(1).jpg",
                                                "https://thebookland.vn/images/1699961827548_z4864225843065_33e5533ef63ad8720a217b601260ea6c.jpg",
                                                "https://thebookland.vn/images/1699961827548_z4864225824666_396f7398bb2ed569d8694ac8032d1cc2.jpg"),
                                bidder1, bidder2);

                createProduct(seller2, books, "Sapiens: Lược Sử Loài Người",
                                "<p>Cuốn sách bán chạy nhất toàn cầu về lịch sử nhân loại.</p>",
                                new BigDecimal("150000"), new BigDecimal("300000"),
                                List.of(
                                                "https://i1-giaitri.vnecdn.net/2024/04/16/z5340470929124-2b85642f88d68e5-6285-1928-1713269462.jpg?w=680&h=0&q=100&dpr=1&fit=crop&s=udsC5t9xPAaKtazCEsq1Nw",
                                                "https://bizweb.dktcdn.net/thumb/1024x1024/100/488/358/products/z5356258024040-c3475c921fda9f05462002d8b5bcf32a-1713329499225.jpg?v=1715342950093",
                                                "https://bizweb.dktcdn.net/thumb/1024x1024/100/488/358/products/16-c7682635-58ed-4b12-a898-2dfc53beb692.png?v=1715342952803",
                                                "https://bizweb.dktcdn.net/thumb/1024x1024/100/488/358/products/17-ca5e32d4-397c-4bf6-8b1b-8d48ad1c026e.png?v=1715342952170"),
                                bidder1, bidder2);

                // --- More Electronics ---
                createProduct(seller, electronics, "Máy Ảnh Sony Alpha A7 IV",
                                "<p>Cảm biến Exmor R CMOS 33MP, quay 4K 60fps.</p>",
                                new BigDecimal("55000000"), new BigDecimal("65000000"),
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_ng_n_2__10_170.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_ng_n_3__8_141.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_ng_n_4__8_147.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/e/text_ng_n_6__5_119.png"),
                                bidder1, bidder2);

                // 4. Completed Orders & Ratings (seed ratings thật)
                createCompletedOrderAndRating(seller, bidder1, phones, "iPhone 14 Pro Max 256GB Deep Purple",
                                "Màn hình Dynamic Island, Camera 48MP siêu nét.",
                                new BigDecimal("20000000"), 1, "Máy đẹp, giao hàng nhanh!",
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/t/_/t_m_20.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone_14_pro_max_512gb_-_2_1__1.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone_14_pro_max_512gb_-_6_1.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone_14_pro_max_512gb_-_3_1__1.png"));

                createCompletedOrderAndRating(seller, bidder2, laptops, "MacBook Air M1 2020 Gray",
                                "Chip M1 vẫn quá ngon trong tầm giá, pin trâu.",
                                new BigDecimal("15000000"), 1, "Shop uy tín, đóng gói cẩn thận.",
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/_/0/_0000_macbook-air-gallery1-20201110_geo_us_5_1.jpg",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/_/0/_0003_macbook-air-gallery4-20201110_5_1.jpg",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/_/0/_0001_macbook-air-gallery2-20201110_5_1.jpg",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/_/0/_0002_dsc03721_2_2_1.jpg"));

                createCompletedOrderAndRating(seller2, bidder1, accessories, "Chuột Logitech MX Master 3S",
                                "Chuột công thái học tốt nhất cho dân văn phòng.",
                                new BigDecimal("2000000"), 1, "Hàng chính hãng, dùng rất sướng.",
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/c/h/chuot-khong-day-bluetooth-logitech-mx-master-3s.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/c/h/chuot-khong-day-bluetooth-logitech-mx-master-3s_5_.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/c/h/chuot-khong-day-bluetooth-logitech-mx-master-3s_2_.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/c/h/chuot-khong-day-bluetooth-logitech-mx-master-3s_1_.png"));

                // Negative rating example
                createCompletedOrderAndRating(seller, bidder2, menFashion, "Quần Jean Levi's 501 Original",
                                "Quần jean ống đứng cổ điển, vải denim bền bỉ.",
                                new BigDecimal("500000"), -1, "Hàng không giống mô tả, rất thất vọng.",
                                List.of(
                                                "https://shophangus.vn/wp-content/uploads/2023/06/size-30-.jpg",
                                                "https://shophangus.vn/wp-content/uploads/2023/06/1-6.jpg",
                                                "https://shophangus.vn/wp-content/uploads/2023/06/2-4.jpg",
                                                "https://vanhoaduongpho.com/storage/news/nhin-lai-levis-501-mau-quan-jean-xanh-dau-tien-tren-the-gioi-1684827925.jpeg"));

                // 5. Test Orders for Flow
                createTestOrderForFlow(seller, bidder1, electronics, "Sony PlayStation 5 Standard Edition",
                                "Máy chơi game console thế hệ mới nhất của Sony. Chiến game 4K 120fps mượt mà.",
                                new BigDecimal("12000000"),
                                OrderStatus.PENDING_PAYMENT,
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/m/a/may-choi-game-sony-playstation-5-slim-1.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/m/a/may-choi-game-sony-playstation-5-slim-3.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/m/a/may-choi-game-sony-playstation-5-slim-2.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/m/a/may-choi-game-sony-playstation-5-slim-4.png"));

                createTestOrderForFlow(seller, bidder2, electronics, "iPad Pro 11 inch 2022 M2 WiFi 128GB",
                                "Chip M2 siêu mạnh, màn hình Liquid Retina 120Hz.",
                                new BigDecimal("18000000"),
                                OrderStatus.PAID,
                                List.of(
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/ipad_pro_11_128gb_-_1.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/ipad_pro_11_128gb_-_3.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/ipad_pro_11_128gb_-_2.png",
                                                "https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/ipad_pro_11_256gb_-_4.png"));

                // ==== 6) CRITICAL: Recalculate rating counters so User fields match Rating
                // table ====
                recalculateUserRatingCounters();

                log.info("Database seeded successfully!");
        }

        private void clearDatabase() {
                // Thứ tự xoá để tránh FK constraint (rating -> order -> product -> category ->
                // user)
                safeDeleteAll(ratingRepository);
                safeDeleteAll(orderRepository);
                safeDeleteAll(bidRepository);
                safeDeleteAll(productRepository);
                safeDeleteAll(categoryRepository);
                safeDeleteAll(userRepository);
        }

        private void safeDeleteAll(Object repo) {
                try {
                        // deleteAllInBatch nếu repo là JpaRepository
                        repo.getClass().getMethod("deleteAllInBatch").invoke(repo);
                } catch (Exception ignored) {
                        try {
                                repo.getClass().getMethod("deleteAll").invoke(repo);
                        } catch (Exception e) {
                                throw new RuntimeException(
                                                "Cannot delete from repository: " + repo.getClass().getName(), e);
                        }
                }
        }

        private User createUser(String name, String email, UserRole role) {
                return userRepository.save(User.builder()
                                .fullName(name)
                                .email(email)
                                .password(passwordEncoder.encode("123456"))
                                .role(role)
                                .address("TP. Hồ Chí Minh")
                                .dob(LocalDate.of(1995, 1, 1))
                                .active(true)
                                // IMPORTANT: seed = 0, sẽ sync theo Rating ở cuối
                                .ratingPositive(0)
                                .ratingNegative(0)
                                .build());
        }

        private Category createCategory(String name, Long parentId) {
                return categoryRepository.save(Category.builder()
                                .name(name)
                                .parentId(parentId)
                                .build());
        }

        private void createProduct(User seller, Category category, String title, String desc,
                        BigDecimal startPrice, BigDecimal buyNowPrice,
                        List<String> imageUrls, User... potentialBidders) {

                Product product = Product.builder()
                                .seller(seller)
                                .category(category)
                                .title(title)
                                .titleNormalized(AppUtils.removeAccent(title))
                                .description(desc)
                                .startPrice(startPrice)
                                .currentPrice(startPrice)
                                .stepPrice(new BigDecimal("100000"))
                                .buyNowPrice(buyNowPrice)
                                .startAt(LocalDateTime.now().minusHours(1))
                                .endAt(LocalDateTime.now().plusMinutes(300 + new Random().nextInt(4300)))
                                .status(ProductStatus.ACTIVE)
                                .autoExtendEnabled(true)
                                .allowUnratedBidder(true)
                                .build();

                List<ProductImage> images = new ArrayList<>();
                if (imageUrls != null) {
                        for (String url : imageUrls) {
                                images.add(ProductImage.builder().product(product).url(url).build());
                        }
                }
                product.setImages(images);

                product = productRepository.save(product);

                if (potentialBidders != null && potentialBidders.length > 0) {
                        createRandomBids(product, potentialBidders);
                }
                productRepository.save(product);
        }

        private void createRandomBids(Product product, User... bidders) {
                Random random = new Random();
                int bidCount = 5 + random.nextInt(4); // 5 to 8 bids
                BigDecimal currentBidAmount = product.getStartPrice();

                for (int i = 0; i < bidCount; i++) {
                        User bidder = bidders[random.nextInt(bidders.length)];
                        int steps = 1 + random.nextInt(5);
                        BigDecimal increase = product.getStepPrice().multiply(new BigDecimal(steps));
                        currentBidAmount = currentBidAmount.add(increase);

                        if (product.getBuyNowPrice() != null
                                        && currentBidAmount.compareTo(product.getBuyNowPrice()) >= 0) {
                                break;
                        }

                        Bid bid = Bid.builder()
                                        .product(product)
                                        .bidder(bidder)
                                        .bidAmount(currentBidAmount)
                                        .bidType(BidType.MANUAL)
                                        .build();

                        bidRepository.save(bid);
                        product.setCurrentPrice(currentBidAmount);
                }
        }

        private void createCompletedOrderAndRating(User seller, User winner, Category category, String productName,
                        String description, BigDecimal price,
                        int score, String comment, List<String> imageUrls) {

                Product product = Product.builder()
                                .seller(seller)
                                .category(category)
                                .title(productName)
                                .titleNormalized(AppUtils.removeAccent(productName))
                                .description(description)
                                .startPrice(price.subtract(new BigDecimal("100000")))
                                .currentPrice(price)
                                .stepPrice(new BigDecimal("50000"))
                                .buyNowPrice(price.add(new BigDecimal("1000000")))
                                .startAt(LocalDateTime.now().minusDays(10))
                                .endAt(LocalDateTime.now().minusDays(5))
                                .status(ProductStatus.SOLD)
                                .currentWinner(winner)
                                .autoExtendEnabled(true)
                                .allowUnratedBidder(true)
                                .build();

                List<ProductImage> images = new ArrayList<>();
                if (imageUrls != null) {
                        for (String url : imageUrls) {
                                images.add(ProductImage.builder().product(product).url(url).build());
                        }
                }
                product.setImages(images);

                product = productRepository.save(product);

                com.auction.auction_backend.modules.order.entity.Order order = com.auction.auction_backend.modules.order.entity.Order
                                .builder()
                                .product(product)
                                .seller(seller)
                                .winner(winner)
                                .finalPrice(price)
                                .status(OrderStatus.DELIVERED)
                                .paymentMethod(com.auction.auction_backend.common.enums.PaymentMethod.COD)
                                .shippingAddress("123 Seed Street, HCM")
                                .build();
                order = orderRepository.save(order);

                Rating rating = Rating.builder()
                                .order(order)
                                .rater(winner)
                                .ratedUser(seller)
                                .score(score) // +1 / -1 (giữ theo domain hiện tại của bạn)
                                .comment(comment)
                                .build();
                ratingRepository.save(rating);
        }

        private void createTestOrderForFlow(User seller, User winner, Category category, String productName,
                        String description, BigDecimal price, OrderStatus status,
                        List<String> imageUrls) {

                Product product = Product.builder()
                                .seller(seller)
                                .category(category)
                                .title(productName)
                                .titleNormalized(AppUtils.removeAccent(productName))
                                .description(description)
                                .startPrice(price.subtract(new BigDecimal("500000")))
                                .currentPrice(price)
                                .stepPrice(new BigDecimal("100000"))
                                .startAt(LocalDateTime.now().minusDays(2))
                                .endAt(LocalDateTime.now().minusMinutes(10))
                                .status(ProductStatus.SOLD)
                                .currentWinner(winner)
                                .autoExtendEnabled(true)
                                .allowUnratedBidder(true)
                                .build();

                List<ProductImage> images = new ArrayList<>();
                if (imageUrls != null) {
                        for (String url : imageUrls) {
                                images.add(ProductImage.builder().product(product).url(url).build());
                        }
                }
                product.setImages(images);

                product = productRepository.save(product);

                com.auction.auction_backend.modules.order.entity.Order order = com.auction.auction_backend.modules.order.entity.Order
                                .builder()
                                .product(product)
                                .seller(seller)
                                .winner(winner)
                                .finalPrice(price)
                                .status(status)
                                .shippingAddress("456 Flow Test St, HCM")
                                .build();

                if (status == OrderStatus.PAID) {
                        order.setPaymentProofUrl("https://example.com/demo-payment-proof.jpg");
                        order.setPaidAt(LocalDateTime.now().minusMinutes(5));
                }

                orderRepository.save(order);
        }

        /**
         * Recalculate ratingPositive/ratingNegative from Rating table
         * => đảm bảo dữ liệu User luôn khớp Rating.
         */
        private void recalculateUserRatingCounters() {
                List<User> users = userRepository.findAll();
                List<Rating> ratings = ratingRepository.findAll();

                Map<Long, int[]> counterMap = new HashMap<>();
                for (Rating r : ratings) {
                        if (r.getRatedUser() == null || r.getRatedUser().getId() == null)
                                continue;
                        long userId = r.getRatedUser().getId();
                        int[] counters = counterMap.computeIfAbsent(userId, k -> new int[] { 0, 0 });
                        if (r.getScore() > 0)
                                counters[0]++; // positive
                        else if (r.getScore() < 0)
                                counters[1]++; // negative
                }

                for (User u : users) {
                        int[] counters = counterMap.getOrDefault(u.getId(), new int[] { 0, 0 });
                        u.setRatingPositive(counters[0]);
                        u.setRatingNegative(counters[1]);
                }

                userRepository.saveAll(users);

                log.info("Recalculated user rating counters from Rating table.");
                for (User u : users) {
                        log.info("User {} ({}) -> +{} / -{}",
                                        u.getFullName(), u.getEmail(), u.getRatingPositive(), u.getRatingNegative());
                }
        }
}
