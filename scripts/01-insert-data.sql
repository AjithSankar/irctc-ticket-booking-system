-- PostgreSQL Insert Data Script for IRCTC Ticket Booking System
-- This script initializes sample data for User, Train, TrainSchedule, and SeatInventory entities

-- ============================================================================
-- INSERT DATA INTO users TABLE
-- ============================================================================
INSERT INTO users (display_name, age, date_of_birth, gender, country, email) VALUES
('Rajesh Kumar', 35, '1989-05-15', 'Male', 'India', 'rajesh.kumar@email.com'),
('Priya Sharma', 28, '1998-08-22', 'Female', 'India', 'priya.sharma@email.com'),
('Amit Patel', 42, '1982-03-10', 'Male', 'India', 'amit.patel@email.com'),
('Neha Singh', 31, '1995-11-30', 'Female', 'India', 'neha.singh@email.com'),
('Vikram Verma', 38, '1988-07-14', 'Male', 'India', 'vikram.verma@email.com'),
('Ananya Gupta', 26, '2000-02-18', 'Female', 'India', 'ananya.gupta@email.com'),
('Rohan Desai', 33, '1993-09-25', 'Male', 'India', 'rohan.desai@email.com'),
('Divya Reddy', 29, '1997-06-12', 'Female', 'India', 'divya.reddy@email.com'),
('Sanjay Pandey', 45, '1981-01-08', 'Male', 'India', 'sanjay.pandey@email.com'),
('Isha Malik', 24, '2002-04-20', 'Female', 'India', 'isha.malik@email.com');


-- ============================================================================
-- INSERT DATA INTO train_schedule TABLE
-- ============================================================================
-- Schedule for Route: Delhi -> Jaipur -> Agra
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('NDLS', 'New Delhi', '2026-03-27 08:00:00', '2026-03-27 08:00:00', '0', 0),
('GGC', 'Ghaziabad', '2026-03-27 09:15:00', '2026-03-27 09:20:00', '5', 65),
('MTJ', 'Meerut', '2026-03-27 10:30:00', '2026-03-27 10:35:00', '5', 120),
('JP', 'Jaipur', '2026-03-27 12:45:00', '2026-03-27 13:15:00', '30', 280),
('BZU', 'Bhera', '2026-03-27 14:25:00', '2026-03-27 14:30:00', '5', 340),
('AGC', 'Agra Cantt', '2026-03-27 16:00:00', '2026-03-27 16:00:00', '0', 420);

-- Schedule for Route: Mumbai -> Pune -> Nashik
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('CSTM', 'Mumbai CST', '2026-03-28 07:00:00', '2026-03-28 07:00:00', '0', 0),
('TNA', 'Thane', '2026-03-28 07:40:00', '2026-03-28 07:45:00', '5', 30),
('VJA', 'Virar', '2026-03-28 08:35:00', '2026-03-28 08:40:00', '5', 72),
('VPU', 'Vapi', '2026-03-28 10:05:00', '2026-03-28 10:10:00', '5', 143),
('BL', 'Billimora', '2026-03-28 11:15:00', '2026-03-28 11:20:00', '5', 200),
('ND', 'Nashik', '2026-03-28 14:30:00', '2026-03-28 14:30:00', '0', 338);

-- Schedule for Route: Kolkata -> Howrah -> Burdwan -> Asansol
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('KOAA', 'Kolkata', '2026-03-29 06:00:00', '2026-03-29 06:00:00', '0', 0),
('HWH', 'Howrah', '2026-03-29 06:45:00', '2026-03-29 07:15:00', '30', 10),
('BWN', 'Burdwan', '2026-03-29 09:30:00', '2026-03-29 09:35:00', '5', 100),
('ASN', 'Asansol', '2026-03-29 11:15:00', '2026-03-29 11:15:00', '0', 155);

-- ============================================================================
-- TAMIL NADU ROUTES
-- ============================================================================

-- Schedule for Route: Chennai -> Tambaram -> Chengalpattu -> Villupuram -> Katpadi -> Vellore -> Tirupati -> Tirutani -> Renigunta
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('MAS', 'Chennai Central', '2026-03-27 06:00:00', '2026-03-27 06:00:00', '0', 0),
('TMB', 'Tambaram', '2026-03-27 06:35:00', '2026-03-27 06:40:00', '5', 35),
('CGL', 'Chengalpattu', '2026-03-27 07:50:00', '2026-03-27 07:55:00', '5', 58),
('VLM', 'Villupuram', '2026-03-27 09:30:00', '2026-03-27 09:35:00', '5', 132),
('KPD', 'Katpadi', '2026-03-27 10:50:00', '2026-03-27 10:55:00', '5', 174),
('VLR', 'Vellore City', '2026-03-27 11:30:00', '2026-03-27 11:35:00', '5', 198),
('TPC', 'Tirupati', '2026-03-27 13:15:00', '2026-03-27 13:20:00', '5', 270),
('TNNI', 'Tirutani', '2026-03-27 14:20:00', '2026-03-27 14:25:00', '5', 310),
('ReniguntaJunction', 'Renigunta', '2026-03-27 15:45:00', '2026-03-27 15:45:00', '0', 365);

-- Schedule for Route: Chennai -> Arkonam -> Chengalpattu -> Kanchipuram -> Tirupati
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('MAS', 'Chennai Central', '2026-03-28 07:00:00', '2026-03-28 07:00:00', '0', 0),
('ARK', 'Arkonam', '2026-03-28 08:20:00', '2026-03-28 08:25:00', '5', 60),
('CGL', 'Chengalpattu', '2026-03-28 09:10:00', '2026-03-28 09:15:00', '5', 90),
('KPM', 'Kanchipuram', '2026-03-28 10:30:00', '2026-03-28 10:35:00', '5', 130),
('TPC', 'Tirupati', '2026-03-28 12:45:00', '2026-03-28 12:45:00', '0', 210);

-- Schedule for Route: Chennai -> Arakkonam -> Chengalpattu -> Vellore -> Katpadi -> Salem -> Erode -> Coimbatore
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('MAS', 'Chennai Central', '2026-03-29 06:30:00', '2026-03-29 06:30:00', '0', 0),
('ARK', 'Arakkonam', '2026-03-29 07:50:00', '2026-03-29 07:55:00', '5', 65),
('CGL', 'Chengalpattu', '2026-03-29 08:35:00', '2026-03-29 08:40:00', '5', 100),
('VLR', 'Vellore City', '2026-03-29 10:15:00', '2026-03-29 10:20:00', '5', 160),
('KPD', 'Katpadi', '2026-03-29 10:55:00', '2026-03-29 11:00:00', '5', 182),
('SA', 'Salem', '2026-03-29 13:30:00', '2026-03-29 13:35:00', '5', 290),
('ED', 'Erode', '2026-03-29 15:10:00', '2026-03-29 15:15:00', '5', 360),
('CBE', 'Coimbatore', '2026-03-29 17:30:00', '2026-03-29 17:30:00', '0', 440);

-- Schedule for Route: Chennai -> Perambur -> Madras Road Board -> Arakonam -> Jolarpet -> Tirupati -> Renigunta
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('MAS', 'Chennai Central', '2026-03-30 05:30:00', '2026-03-30 05:30:00', '0', 0),
('PER', 'Perambur', '2026-03-30 06:00:00', '2026-03-30 06:05:00', '5', 18),
('MRB', 'Madras Road Board', '2026-03-30 06:45:00', '2026-03-30 06:50:00', '5', 40),
('ARK', 'Arakonam', '2026-03-30 08:05:00', '2026-03-30 08:10:00', '5', 70),
('JP', 'Jolarpet', '2026-03-30 09:40:00', '2026-03-30 09:45:00', '5', 130),
('TPC', 'Tirupati', '2026-03-30 11:50:00', '2026-03-30 11:55:00', '5', 220),
('RJCT', 'Renigunta', '2026-03-30 13:15:00', '2026-03-30 13:15:00', '0', 260);

-- Schedule for Route: Chennai -> Villupuram -> Tirupati -> Mettupalayam -> Coonoor -> Ooty
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('MAS', 'Chennai Central', '2026-03-31 06:00:00', '2026-03-31 06:00:00', '0', 0),
('VLM', 'Villupuram', '2026-03-31 08:30:00', '2026-03-31 08:35:00', '5', 140),
('TPC', 'Tirupati', '2026-03-31 10:45:00', '2026-03-31 10:50:00', '5', 210),
('MTP', 'Mettupalayam', '2026-03-31 13:20:00', '2026-03-31 13:25:00', '5', 340),
('CNR', 'Coonoor', '2026-03-31 14:45:00', '2026-03-31 14:50:00', '5', 380),
('OTY', 'Ooty', '2026-03-31 16:15:00', '2026-03-31 16:15:00', '0', 420);

-- Schedule for Route: Chennai -> Chengalpattu -> Madurai -> Srivilliputtur -> Tenkasi -> Nagercoil
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('MAS', 'Chennai Central', '2026-04-01 08:00:00', '2026-04-01 08:00:00', '0', 0),
('CGL', 'Chengalpattu', '2026-04-01 09:15:00', '2026-04-01 09:20:00', '5', 58),
('MDU', 'Madurai', '2026-04-01 13:00:00', '2026-04-01 13:10:00', '10', 250),
('SVP', 'Srivilliputtur', '2026-04-01 14:25:00', '2026-04-01 14:30:00', '5', 310),
('TK', 'Tenkasi', '2026-04-01 15:45:00', '2026-04-01 15:50:00', '5', 360),
('NCJ', 'Nagercoil', '2026-04-01 17:30:00', '2026-04-01 17:30:00', '0', 425);

-- Schedule for Route: Chennai -> Villupuram -> Tirupati -> Mettupalayam -> Coonoor (Cheran SF Express)
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('MAS', 'Chennai Central', '2026-04-02 06:15:00', '2026-04-02 06:15:00', '0', 0),
('VLM', 'Villupuram', '2026-04-02 08:45:00', '2026-04-02 08:50:00', '5', 140),
('TPC', 'Tirupati', '2026-04-02 11:00:00', '2026-04-02 11:05:00', '5', 210),
('MTP', 'Mettupalayam', '2026-04-02 13:35:00', '2026-04-02 13:40:00', '5', 340),
('CNR', 'Coonoor', '2026-04-02 15:05:00', '2026-04-02 15:05:00', '0', 380);

-- Schedule for Route: Chennai -> Villupuram -> Tirupati -> Mettupalayam -> Coonoor -> Ooty (Nilgiri SF Express)
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('MAS', 'Chennai Central', '2026-04-03 09:00:00', '2026-04-03 09:00:00', '0', 0),
('VLM', 'Villupuram', '2026-04-03 11:30:00', '2026-04-03 11:35:00', '5', 140),
('TPC', 'Tirupati', '2026-04-03 13:45:00', '2026-04-03 13:50:00', '5', 210),
('MTP', 'Mettupalayam', '2026-04-03 16:20:00', '2026-04-03 16:25:00', '5', 340),
('CNR', 'Coonoor', '2026-04-03 17:45:00', '2026-04-03 17:50:00', '5', 380),
('OTY', 'Ooty', '2026-04-03 19:15:00', '2026-04-03 19:15:00', '0', 420);

-- Schedule for Route: Chennai -> Arakkonam -> Chengalpattu -> Vellore -> Katpadi -> Salem -> Erode -> Coimbatore (Kovai Express)
INSERT INTO train_schedule (station_code, station_name, arrival_time, departure_time, halt_time_in_minutes, distance) VALUES
('MAS', 'Chennai Central', '2026-04-04 07:00:00', '2026-04-04 07:00:00', '0', 0),
('ARK', 'Arakkonam', '2026-04-04 08:20:00', '2026-04-04 08:25:00', '5', 65),
('CGL', 'Chengalpattu', '2026-04-04 09:05:00', '2026-04-04 09:10:00', '5', 100),
('VLR', 'Vellore City', '2026-04-04 10:40:00', '2026-04-04 10:45:00', '5', 160),
('KPD', 'Katpadi', '2026-04-04 11:15:00', '2026-04-04 11:20:00', '5', 182),
('SA', 'Salem', '2026-04-04 13:50:00', '2026-04-04 13:55:00', '5', 290),
('ED', 'Erode', '2026-04-04 15:30:00', '2026-04-04 15:35:00', '5', 360),
('CBE', 'Coimbatore', '2026-04-04 17:50:00', '2026-04-04 17:50:00', '0', 440);


-- ============================================================================
-- INSERT DATA INTO trains TABLE
-- ============================================================================
INSERT INTO trains (train_no, train_name, train_type, from_station, destination_station, runs_on, schedule_id) VALUES
(12001, 'Rajdhani Express', 'RAJDHANI', 'New Delhi', 'Agra Cantt', 'Monday,Tuesday,Wednesday,Thursday,Friday', 1),
(12345, 'Shatabdi Express', 'SHATABDI', 'Mumbai CST', 'Nashik', 'Monday,Tuesday,Wednesday,Thursday,Friday,Saturday', 2),
(12835, 'Howrah Mail', 'EXPRESS', 'Kolkata', 'Asansol', 'Daily', 3),
(12006, 'AP Express', 'EXPRESS', 'New Delhi', 'Agra Cantt', 'Daily', 1),
(19014, 'Surbandhan Express', 'SUPERFAST', 'Mumbai CST', 'Nashik', 'Tuesday,Thursday,Saturday', 2),
(15101, 'Jyotiba Phule Exp', 'PASSENGER', 'New Delhi', 'Agra Cantt', 'Daily', 1),
(12869, 'Howrah Rajdhani', 'RAJDHANI', 'Kolkata', 'Asansol', 'Wednesday,Friday,Sunday', 3),
(12305, 'Rajdhani Express', 'RAJDHANI', 'Mumbai CST', 'Nashik', 'Monday,Wednesday,Friday', 2),
(10102, 'New Delhi Fast Pass', 'PASSENGER', 'New Delhi', 'Agra Cantt', 'Daily', 1),
(15002, 'Kolkata Express', 'EXPRESS', 'Kolkata', 'Asansol', 'Daily', 3),
-- Tamil Nadu Routes
(16205, 'Chennai-Tirupati Exp', 'EXPRESS', 'Chennai Central', 'Renigunta', 'Daily', 4),
(16206, 'Tirupati Passenger', 'PASSENGER', 'Chennai Central', 'Tirupati', 'Daily', 5),
(16501, 'Chennai-Coimbatore Exp', 'EXPRESS', 'Chennai Central', 'Coimbatore', 'Monday,Wednesday,Friday', 6),
(16502, 'Nilgiri Express', 'SUPERFAST', 'Chennai Central', 'Ooty', 'Tuesday,Thursday,Saturday', 7),
(16505, 'Chennai-Nagercoil Exp', 'EXPRESS', 'Chennai Central', 'Nagercoil', 'Daily', 8),
(56401, 'Chennai Tirupati Pass', 'PASSENGER', 'Chennai Central', 'Renigunta', 'Daily', 4),
(16503, 'Coimbatore Express', 'EXPRESS', 'Chennai Central', 'Coimbatore', 'Tuesday,Saturday', 6),
(16504, 'Madurai Express', 'EXPRESS', 'Chennai Central', 'Nagercoil', 'Monday,Wednesday,Friday,Sunday', 8),
-- ...existing code...
(56402, 'Ooty Mountain Exp', 'PASSENGER', 'Chennai Central', 'Ooty', 'Daily', 7),
(16205, 'Temple Route Express', 'EXPRESS', 'Chennai Central', 'Renigunta', 'Wednesday,Friday', 4),
-- Additional Tamil Nadu Superfast Trains
(12674, 'Cheran SF Express', 'SUPERFAST', 'Chennai Central', 'Coonoor', 'Tuesday,Thursday,Saturday,Sunday', 9),
(12671, 'Nilgiri SF Express', 'SUPERFAST', 'Chennai Central', 'Ooty', 'Wednesday,Friday,Sunday', 10),
(12672, 'Nilgiri SF Express', 'SUPERFAST', 'Chennai Central', 'Ooty', 'Monday,Wednesday,Friday,Saturday', 10),
(12675, 'Kovai Express', 'SUPERFAST', 'Chennai Central', 'Coimbatore', 'Daily', 11);


-- ============================================================================
-- INSERT DATA INTO seat_inventory TABLE
-- Seats for each train and journey date (50 seats per train)
-- ============================================================================

-- Train 12001 (Rajdhani Express) - Journey Date: 2026-03-27
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 12001, '2026-03-27', seat_num, 'New Delhi', 'Agra Cantt', 'AVAILABLE'
FROM (SELECT generate_series(1, 50) as seat_num) t;

-- Train 12345 (Shatabdi Express) - Journey Date: 2026-03-28
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 12345, '2026-03-28', seat_num, 'Mumbai CST', 'Nashik', 'AVAILABLE'
FROM (SELECT generate_series(1, 50) as seat_num) t;

-- Train 12835 (Howrah Mail) - Journey Date: 2026-03-29
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 12835, '2026-03-29', seat_num, 'Kolkata', 'Asansol', 'AVAILABLE'
FROM (SELECT generate_series(1, 50) as seat_num) t;

-- Train 12006 (AP Express) - Journey Date: 2026-03-27
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 12006, '2026-03-27', seat_num, 'New Delhi', 'Agra Cantt', 'AVAILABLE'
FROM (SELECT generate_series(1, 50) as seat_num) t;

-- Train 19014 (Surbandhan Express) - Journey Date: 2026-03-28
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 19014, '2026-03-28', seat_num, 'Mumbai CST', 'Nashik', 'AVAILABLE'
FROM (SELECT generate_series(1, 50) as seat_num) t;

-- Train 15101 (Jyotiba Phule Exp) - Journey Date: 2026-03-27
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 15101, '2026-03-27', seat_num, 'New Delhi', 'Agra Cantt', 'AVAILABLE'
FROM (SELECT generate_series(1, 50) as seat_num) t;

-- Train 12869 (Howrah Rajdhani) - Journey Date: 2026-03-29
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 12869, '2026-03-29', seat_num, 'Kolkata', 'Asansol', 'AVAILABLE'
FROM (SELECT generate_series(1, 50) as seat_num) t;

-- Train 12305 (Rajdhani Express) - Journey Date: 2026-03-28
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 12305, '2026-03-28', seat_num, 'Mumbai CST', 'Nashik', 'AVAILABLE'
FROM (SELECT generate_series(1, 50) as seat_num) t;

-- Train 10102 (New Delhi Fast Pass) - Journey Date: 2026-03-27
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 10102, '2026-03-27', seat_num, 'New Delhi', 'Agra Cantt', 'AVAILABLE'
FROM (SELECT generate_series(1, 50) as seat_num) t;

-- Train 15002 (Kolkata Express) - Journey Date: 2026-03-29
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 15002, '2026-03-29', seat_num, 'Kolkata', 'Asansol', 'AVAILABLE'
FROM (SELECT generate_series(1, 50) as seat_num) t;


-- ============================================================================
-- TAMIL NADU TRAIN SEAT INVENTORY
-- ============================================================================

-- Train 16205 (Chennai-Tirupati Exp) - Journey Date: 2026-03-27
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 16205, '2026-03-27', seat_num, 'Chennai Central', 'Renigunta', 'AVAILABLE'
FROM (SELECT generate_series(1, 60) as seat_num) t;

-- Train 16206 (Tirupati Passenger) - Journey Date: 2026-03-28
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 16206, '2026-03-28', seat_num, 'Chennai Central', 'Tirupati', 'AVAILABLE'
FROM (SELECT generate_series(1, 72) as seat_num) t;

-- Train 16501 (Chennai-Coimbatore Exp) - Journey Date: 2026-03-29
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 16501, '2026-03-29', seat_num, 'Chennai Central', 'Coimbatore', 'AVAILABLE'
FROM (SELECT generate_series(1, 64) as seat_num) t;

-- Train 16502 (Nilgiri Express) - Journey Date: 2026-03-31
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 16502, '2026-03-31', seat_num, 'Chennai Central', 'Ooty', 'AVAILABLE'
FROM (SELECT generate_series(1, 48) as seat_num) t;

-- Train 16505 (Chennai-Nagercoil Exp) - Journey Date: 2026-04-01
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 16505, '2026-04-01', seat_num, 'Chennai Central', 'Nagercoil', 'AVAILABLE'
FROM (SELECT generate_series(1, 56) as seat_num) t;

-- Train 56401 (Chennai Tirupati Pass) - Journey Date: 2026-03-27
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 56401, '2026-03-27', seat_num, 'Chennai Central', 'Renigunta', 'AVAILABLE'
FROM (SELECT generate_series(1, 80) as seat_num) t;

-- Train 16503 (Coimbatore Express) - Journey Date: 2026-03-30
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 16503, '2026-03-30', seat_num, 'Chennai Central', 'Coimbatore', 'AVAILABLE'
FROM (SELECT generate_series(1, 64) as seat_num) t;

-- Train 16504 (Madurai Express) - Journey Date: 2026-04-01
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 16504, '2026-04-01', seat_num, 'Chennai Central', 'Nagercoil', 'AVAILABLE'
FROM (SELECT generate_series(1, 56) as seat_num) t;

-- Train 56402 (Ooty Mountain Exp) - Journey Date: 2026-03-31
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 56402, '2026-03-31', seat_num, 'Chennai Central', 'Ooty', 'AVAILABLE'
FROM (SELECT generate_series(1, 48) as seat_num) t;


-- ============================================================================
-- ADDITIONAL TAMIL NADU SUPERFAST TRAINS SEAT INVENTORY
-- ============================================================================

-- Train 12674 (Cheran SF Express) - Journey Date: 2026-04-02
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 12674, '2026-04-02', seat_num, 'Chennai Central', 'Coonoor', 'AVAILABLE'
FROM (SELECT generate_series(1, 56) as seat_num) t;

-- Train 12671 (Nilgiri SF Express) - Journey Date: 2026-04-03
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 12671, '2026-04-03', seat_num, 'Chennai Central', 'Ooty', 'AVAILABLE'
FROM (SELECT generate_series(1, 52) as seat_num) t;

-- Train 12672 (Nilgiri SF Express) - Journey Date: 2026-04-04
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 12672, '2026-04-04', seat_num, 'Chennai Central', 'Ooty', 'AVAILABLE'
FROM (SELECT generate_series(1, 52) as seat_num) t;

-- Train 12675 (Kovai Express) - Journey Date: 2026-04-04
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
SELECT 12675, '2026-04-04', seat_num, 'Chennai Central', 'Coimbatore', 'AVAILABLE'
FROM (SELECT generate_series(1, 60) as seat_num) t;


-- ============================================================================
-- VERIFY DATA INSERTION
-- ============================================================================
-- Uncomment the following SELECT statements to verify the inserted data:

-- SELECT COUNT(*) as user_count FROM users;
-- SELECT COUNT(*) as train_schedule_count FROM train_schedule;
-- SELECT COUNT(*) as train_count FROM trains;
-- SELECT COUNT(*) as seat_inventory_count FROM seat_inventory;

-- SELECT * FROM users LIMIT 5;
-- SELECT * FROM train_schedule LIMIT 5;
-- SELECT * FROM trains LIMIT 5;
-- SELECT * FROM seat_inventory LIMIT 10;

