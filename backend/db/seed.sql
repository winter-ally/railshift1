-- Sample data taken from the app's FakeRailshiftRepository. Dev/testing only.
BEGIN;

INSERT INTO stations (code, name, city) VALUES
 ('BZA','Vijayawada Jn','Vijayawada'), ('MAS','Chennai Central','Chennai'),
 ('GNT','Guntur Jn','Guntur'),         ('HYB','Hyderabad','Hyderabad'),
 ('SC','Secunderabad','Secunderabad'), ('VSKP','Visakhapatnam','Visakhapatnam'),
 ('RJY','Rajahmundry','Rajahmundry')
ON CONFLICT DO NOTHING;

INSERT INTO trains (number, name, from_station, to_station, departure_time, arrival_time, duration_minutes) VALUES
 ('12711','Pinakini Express','BZA','MAS','06:25','12:40',375),
 ('17215','Visakhapatnam Express','VSKP','BZA','14:00','20:00',360),
 ('12839','Howrah Mail','VSKP','BZA','09:30','16:30',420)
ON CONFLICT DO NOTHING;

INSERT INTO train_classes (train_number, class_code, base_fare, total_berths) VALUES
 ('12711','SL',280,72), ('12711','3A',640,64), ('12711','2A',960,48), ('12711','CC',420,78),
 ('17215','SL',280,72), ('17215','3A',640,64),
 ('12839','SL',320,72), ('12839','3A',700,64), ('12839','2A',960,48)
ON CONFLICT DO NOTHING;

-- availability for the next 30 days
INSERT INTO seat_inventory (train_number, journey_date, class_code, available_berths)
SELECT c.train_number, d::date, c.class_code, c.total_berths
FROM train_classes c, generate_series(current_date, current_date + 30, interval '1 day') d
ON CONFLICT DO NOTHING;

-- demo accounts: password_hash is a placeholder, real hashes are created by the API on signup
INSERT INTO users (name, email, password_hash, role) VALUES
 ('Ravi Kumar','ravi.kumar@example.com','!placeholder','passenger'),
 ('Demo TTE','tte@example.com','!placeholder','tte')
ON CONFLICT DO NOTHING;

INSERT INTO wallets (user_id, balance)
SELECT id, 1240.00 FROM users WHERE email = 'ravi.kumar@example.com'
ON CONFLICT DO NOTHING;

COMMIT;
