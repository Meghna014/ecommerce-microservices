INSERT IGNORE INTO customers (name, email, password, phone, address, role)
VALUES
('Admin User', 'admin@example.com', '123456', '9999999999', '123 Admin St', 'ADMIN'),
('test user1', 'test1@example.com', '123456', '1234567890', '123 Main St', 'USER'),
('test user2', 'test2@example.com', '123456', '0987654321', '456 Park Ave', 'USER');