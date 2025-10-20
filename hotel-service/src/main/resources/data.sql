INSERT INTO hotels (name, address) VALUES 
('Grand Hotel', '123 Main St, New York'),
('Beach Resort', '456 Ocean Ave, Miami');

INSERT INTO rooms (hotel_id, number, available, times_booked, price) VALUES
(1, '101', true, 5, 150.00),
(1, '102', true, 3, 150.00),
(1, '201', true, 8, 200.00),
(2, '101', true, 2, 180.00),
(2, '102', true, 1, 180.00),
(2, '301', true, 10, 250.00);