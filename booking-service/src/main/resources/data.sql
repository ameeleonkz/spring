-- Insert Admin User
-- Username: admin, Password: admin123
INSERT INTO users (username, password, role) VALUES 
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ADMIN');

-- Insert Regular User
-- Username: user, Password: user123
INSERT INTO users (username, password, role) VALUES 
('user', '$2a$10$Jd8gHn2S8gRgeK8Yd1vSduSGPTpB2ACXKpItgqOKXMPxZjexJxEjC', 'USER');
