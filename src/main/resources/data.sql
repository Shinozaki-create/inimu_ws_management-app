INSERT INTO admin_users (id, email, password_hash, name, role, enabled, created_at, updated_at)
VALUES
    (1, 'admin@example.com', '$2a$10$LIFGFmvo.8aW/sYZLpFxLuJUrAAPLllbNx3OKe2ZV9rgDrQL/rTQW', '管理者', 'ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO system_settings (id, setting_key, setting_value, description, created_at, updated_at)
VALUES
    (1, 'price_per_person', '5500', '1名あたりの参加料金', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO workshop_schedules (id, schedule_date, is_open, note, created_at, updated_at)
VALUES
    (1, DATE '2026-06-06', TRUE, 'サンプル開催日', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, DATE '2026-06-13', TRUE, 'サンプル開催日', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, DATE '2026-06-20', FALSE, '非公開サンプル', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO workshop_time_slots (id, schedule_id, start_time, end_time, capacity, reserved_count, is_active, created_at, updated_at)
VALUES
    (1, 1, TIME '11:00:00', TIME '12:00:00', 10, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, TIME '13:00:00', TIME '14:00:00', 10, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 1, TIME '15:00:00', TIME '16:00:00', 10, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 2, TIME '11:00:00', TIME '12:00:00', 10, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 2, TIME '13:00:00', TIME '14:00:00', 10, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 2, TIME '15:00:00', TIME '16:00:00', 10, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 3, TIME '11:00:00', TIME '12:00:00', 10, 0, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
