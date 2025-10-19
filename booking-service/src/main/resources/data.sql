-- Insert Admin User
-- Пароль: admin123 (BCrypt encoded)
INSERT INTO users (username, password, role) VALUES 
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');

-- Insert Regular Users
-- Пароль: user123 (BCrypt encoded)
INSERT INTO users (username, password, role) VALUES 
('user', '$2a$10$DowJonesIA.8s3N/qKJCRqunT/Y8kJvZs9p.gQOZW.LCq6A6T6NU2', 'USER');