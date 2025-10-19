INSERT INTO hotels (name, address, city, description) VALUES
('Grand Hotel', '123 Main St', 'New York', 'Luxury hotel in downtown'),
('Beach Resort', '456 Ocean Ave', 'Miami', 'Beautiful beachfront resort');

INSERT INTO rooms (hotel_id, number, type, price, available, times_booked) VALUES
(1, '101', 'Single', 100.00, true, 0),
(1, '102', 'Double', 150.00, true, 0),
(1, '201', 'Suite', 300.00, true, 0),
(2, '101', 'Single', 120.00, true, 0),
(2, '102', 'Double', 180.00, true, 0);