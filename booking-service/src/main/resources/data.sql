-- Insert Admin User
-- Username: admin, Password: password123
INSERT INTO users (username, password, role) VALUES 
('admin', '$2a$10$O4OVMWsabPM.4.EBDEKvAO3VDksJqM.YHwAWVcKRelDrsHNIsAUba', 'ADMIN');

-- Insert Regular User
-- Username: user, Password: password123
INSERT INTO users (username, password, role) VALUES 
('user', '$2a$10$O4OVMWsabPM.4.EBDEKvAO3VDksJqM.YHwAWVcKRelDrsHNIsAUba', 'USER');
