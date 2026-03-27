-- ===================================================================================
-- 1. CLEANUP (Idempotency for safe re-runs)
-- ===================================================================================
TRUNCATE TABLE seat_inventory CASCADE;
TRUNCATE TABLE train_schedule CASCADE;
TRUNCATE TABLE trains CASCADE;
TRUNCATE TABLE users CASCADE;

-- ===================================================================================
-- 2. USERS
-- ===================================================================================
INSERT INTO users (display_name, age, date_of_birth, gender, country, email)
VALUES ('Arun Kumar', 28, '1995-06-12', 'M', 'India', 'arun.kumar@example.com'),
       ('Priya Rajan', 25, '1998-09-24', 'F', 'India', 'priya.rajan@example.com'),
       ('Karthik S', 32, '1991-03-15', 'M', 'India', 'karthik.s@example.com'),
       ('Divya M', 29, '1994-11-02', 'F', 'India', 'divya.m@example.com');

-- ===================================================================================
-- 3. TRAINS (Master Data)
-- ===================================================================================
INSERT INTO trains (train_no, train_name, train_type, source_station, destination_station, runs_on)
VALUES (12675, 'Kovai Express', 'SUPERFAST', 'MAS', 'CBE', 'DAILY'),
       (12673, 'Cheran SF Express', 'SUPERFAST', 'MAS', 'CBE', 'DAILY'),
       (12671, 'Nilgiri SF Express', 'SUPERFAST', 'MAS', 'MTP', 'DAILY'),
       (12637, 'Pandian SF Express', 'SUPERFAST', 'MS', 'MDU', 'DAILY');

-- ===================================================================================
-- 4. TRAIN SCHEDULES (The Multi-Stop Route Mapping)
-- Timings based on actual Southern Railway schedules.
-- ===================================================================================

-- -----------------------------------------------------------------------------------
-- Route for 12675: Kovai Express (Day Time Train)
-- -----------------------------------------------------------------------------------
INSERT INTO train_schedule (train_no, station_code, station_name, stop_sequence, arrival_time, departure_time,
                            day_count, distance_from_source)
VALUES (12675, 'MAS', 'M.G.R. Chennai Central', 1, '06:10:00', '06:10:00', 1, 0),
       (12675, 'AJJ', 'Arakkonam Jn', 2, '07:08:00', '07:10:00', 1, 69),
       (12675, 'KPD', 'Katpadi Jn', 3, '07:58:00', '08:00:00', 1, 130),
       (12675, 'JTJ', 'Jolarpettai Jn', 4, '09:13:00', '09:15:00', 1, 214),
       (12675, 'SA', 'Salem Jn', 5, '10:52:00', '10:55:00', 1, 334),
       (12675, 'ED', 'Erode Jn', 6, '11:57:00', '12:00:00', 1, 394),
       (12675, 'TUP', 'Tiruppur', 7, '12:43:00', '12:45:00', 1, 444),
       (12675, 'CBE', 'Coimbatore Jn', 8, '14:05:00', '14:05:00', 1, 496);

-- -----------------------------------------------------------------------------------
-- Route for 12637: Pandian SF Express (Overnight Train - Notice Day 1 vs Day 2)
-- -----------------------------------------------------------------------------------
INSERT INTO train_schedule (train_no, station_code, station_name, stop_sequence, arrival_time, departure_time,
                            day_count, distance_from_source)
VALUES (12637, 'MS', 'Chennai Egmore', 1, '21:40:00', '21:40:00', 1, 0),
       (12637, 'TBM', 'Tambaram', 2, '22:08:00', '22:10:00', 1, 25),
       (12637, 'CGL', 'Chengalpattu Jn', 3, '22:38:00', '22:40:00', 1, 56),
       (12637, 'VM', 'Villupuram Jn', 4, '00:05:00', '00:10:00', 2, 159), -- Crosses midnight
       (12637, 'VRI', 'Vriddhachalam Jn', 5, '00:55:00', '00:57:00', 2, 213),
       (12637, 'TPJ', 'Tiruchchirappalli Jn', 6, '02:45:00', '02:50:00', 2, 336),
       (12637, 'DG', 'Dindigul Jn', 7, '04:02:00', '04:05:00', 2, 430),
       (12637, 'MDU', 'Madurai Jn', 8, '05:25:00', '05:25:00', 2, 493);


-- ===================================================================================
-- 5. SEAT INVENTORY
-- Generating 5 base seats for the Kovai Express for a specific date.
-- Initially, seats are available from the absolute source to the absolute destination.
-- ===================================================================================
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
VALUES (12675, '2026-03-30', 1, 'MAS', 'CBE', 'AVAILABLE'),
       (12675, '2026-03-30', 2, 'MAS', 'CBE', 'AVAILABLE'),
       (12675, '2026-03-30', 3, 'MAS', 'CBE', 'AVAILABLE'),
       (12675, '2026-03-30', 4, 'MAS', 'CBE', 'AVAILABLE'),
       (12675, '2026-03-30', 5, 'MAS', 'CBE', 'AVAILABLE');

-- Generating 5 seats for Pandian Express
INSERT INTO seat_inventory (train_no, journey_date, seat_number, source_station, destination_station, status)
VALUES (12637, '2026-03-30', 1, 'MS', 'MDU', 'AVAILABLE'),
       (12637, '2026-03-30', 2, 'MS', 'MDU', 'AVAILABLE'),
       (12637, '2026-03-30', 3, 'MS', 'MDU', 'AVAILABLE'),
       (12637, '2026-03-30', 4, 'MS', 'MDU', 'AVAILABLE'),
       (12637, '2026-03-30', 5, 'MS', 'MDU', 'AVAILABLE');

