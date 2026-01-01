-- Password for all users: 123456
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcxb7//0kiZareoGLWzO8MA/WZm

INSERT INTO users (created_at, updated_at, version, full_name, email, password_hash, role, is_active, rating_pos, rating_neg)
VALUES 
(NOW(), NOW(), 0, 'Admin User', 'admin@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcxb7//0kiZareoGLWzO8MA/WZm', 'ADMIN', true, 0, 0),
(NOW(), NOW(), 0, 'Seller User', 'seller@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcxb7//0kiZareoGLWzO8MA/WZm', 'SELLER', true, 10, 2),
(NOW(), NOW(), 0, 'Bidder User 1', 'bidder1@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcxb7//0kiZareoGLWzO8MA/WZm', 'BIDDER', true, 5, 0),
(NOW(), NOW(), 0, 'Bidder User 2', 'bidder2@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcxb7//0kiZareoGLWzO8MA/WZm', 'BIDDER', true, 0, 0);
