# Hướng dẫn Thiết lập Dự án Auction Backend

Dự án này là hệ thống Backend cho website đấu giá trực tuyến, được xây dựng bằng Spring Boot.

## 1. Yêu cầu hệ thống (Prerequisites)
Trước khi bắt đầu, hãy đảm bảo máy tính của bạn đã cài đặt:
- **Java JDK 21**: Tải tại [Oracle](https://www.oracle.com/java/technologies/downloads/) hoặc [OpenJDK](https://adoptium.net/).
- **MySQL Server 8.x**: Đang chạy trên cổng `3306`.
- **Maven**: (Dự án có sẵn `./mvnw` nên không nhất thiết phải cài Maven rời).
- **IDE**: IntelliJ IDEA (Khuyến khích) hoặc VS Code.

---

## 2. Thiết lập Cơ sở dữ liệu
1. Mở MySQL Workbench hoặc Terminal.
2. Tạo database mới:
   ```sql
   CREATE DATABASE auction_db_v2;
   ```
3. Tài khoản mặc định trong code đang là `username: root`, `password: root`. Nếu bạn dùng pass khác, hãy cập nhật trong `src/main/resources/application.yaml`.

---

## 3. Cấu hình Biến môi trường (.env / Environment Variables)
Dự án sử dụng các biến môi trường để bảo mật thông tin nhạy cảm.

### Các biến cần thiết:
Bạn cần thiết lập các giá trị sau trong IDE (Environment Variables) hoặc tạo file `.env` ở thư mục gốc:

| Biến | Mô tả | Giá trị ví dụ |
| :--- | :--- | :--- |
| `MAIL_USERNAME` | Email gửi thông báo (Gmail) | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Mật khẩu ứng dụng (App Password) | `xxxx xxxx xxxx xxxx` |
| `STRIPE_API_KEY` | Stripe Secret Key | `sk_test_...` |
| `STRIPE_WEBHOOK_SECRET` | Stripe Webhook Secret | `whsec_...` |

> [!IMPORTANT]
> **Cách lấy MAIL_PASSWORD**: 
> 1. Vào [Google Account Settings](https://myaccount.google.com/).
> 2. Bật "2-Step Verification".
> 3. Tìm "App Passwords" và tạo một mật khẩu cho "Mail".

---

## 4. Cách chạy dự án

### Cách 1: Chạy từ Terminal (Dùng Maven Wrapper)
Dùng lệnh sau để cài đặt và chạy ứng dụng:
```bash
# Windows
./mvnw clean spring-boot:run

# Linux/macOS
./mvnw clean spring-boot:run
```

### Cách 2: Chạy từ IntelliJ IDEA
1. Mở dự án bằng cách chọn file `pom.xml`.
2. Đợi Maven tải hết các dependencies.
3. Vào `Edit Configurations` -> `Environment variables` -> Dán các biến ở mục 3 vào.
4. Nhấn **Run** (Nút tam giác xanh).

---

## 5. Dữ liệu mẫu (Seeding)
Mặc định khi chạy lần đầu, dự án sẽ tự động gọi `DataSeeder` để tạo:
- **Admin**: `admin@gmail.com` / `123456`
- **Seller**: `seller@gmail.com` / `123456`
- **Bidder**: `bidder1@gmail.com`, `bidder2@gmail.com` / `123456`
- Danh mục sản phẩm, Sản phẩm mẫu và Lịch sử đấu giá.

> [!TIP]
> Để ép buộc tạo lại dữ liệu mẫu, bạn có thể thêm flag `-Dapp.seed.force=true` khi chạy hoặc sửa code trong `DataSeeder.java`.

---

## 6. Tài liệu API
Sau khi ứng dụng chạy thành công, bạn có thể xem tài liệu Swagger tại:
`http://localhost:8080/swagger-ui/index.html`

---

## 7. Các tính năng chính đã hoàn thành
- [x] Đăng ký/Đăng nhập (JWT).
- [x] Đấu giá tự động (Auto Bidding).
- [x] Thanh toán Stripe (Tạo Payment Intent & Webhook).
- [x] Gửi Email thông báo (Khi bị vượt giá, thắng thầu, chặn người dùng).
- [x] Đánh giá người dùng (Rating system).
- [x] Quản lý sản phẩm, danh mục và giới hạn lượt truy cập (Rate Limiting).
