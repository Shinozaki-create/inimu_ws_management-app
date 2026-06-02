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
    (4, 2, TIME '11:00:00', TIME '12:00:00', 10, 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 2, TIME '13:00:00', TIME '14:00:00', 10, 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 2, TIME '15:00:00', TIME '16:00:00', 10, 9, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 3, TIME '11:00:00', TIME '12:00:00', 10, 0, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO reservations (
    id,
    reservation_code,
    time_slot_id,
    reservation_date,
    reservation_time,
    reservation_count,
    customer_family_name,
    customer_given_name,
    customer_family_kana,
    customer_given_kana,
    customer_email,
    customer_tel,
    customer_message,
    total_amount,
    status,
    admin_memo,
    created_at,
    updated_at
)
VALUES
    (
        1,
        'WS-20260613-0001',
        4,
        DATE '2026-06-13',
        TIME '11:00:00',
        3,
        '佐藤',
        '美咲',
        'サトウ',
        'ミサキ',
        'misaki.sato@example.com',
        '09011112222',
        '親子3名で参加予定です。',
        16500,
        'CONFIRMED',
        '電話確認済み',
        TIMESTAMP '2026-06-01 09:30:00',
        TIMESTAMP '2026-06-01 10:00:00'
    ),
    (
        2,
        'WS-20260613-0002',
        5,
        DATE '2026-06-13',
        TIME '13:00:00',
        2,
        '鈴木',
        '太郎',
        'スズキ',
        'タロウ',
        'taro.suzuki@example.com',
        '09022223333',
        '領収書が必要です。',
        11000,
        'COMPLETED',
        '領収書発行済み',
        TIMESTAMP '2026-06-01 11:15:00',
        TIMESTAMP '2026-06-01 11:40:00'
    ),
    (
        3,
        'WS-20260613-0003',
        6,
        DATE '2026-06-13',
        TIME '15:00:00',
        4,
        '田中',
        '彩',
        'タナカ',
        'アヤ',
        'aya.tanaka@example.com',
        '09033334444',
        '友人家族と参加します。',
        22000,
        'PENDING',
        '支払い確認待ち',
        TIMESTAMP '2026-06-01 13:20:00',
        TIMESTAMP '2026-06-01 13:20:00'
    ),
    (
        4,
        'WS-20260613-0004',
        6,
        DATE '2026-06-13',
        TIME '15:00:00',
        5,
        '高橋',
        '健',
        'タカハシ',
        'ケン',
        'ken.takahashi@example.com',
        '09044445555',
        '団体で参加します。',
        27500,
        'CONFIRMED',
        '残席少ないため注意',
        TIMESTAMP '2026-06-01 15:45:00',
        TIMESTAMP '2026-06-01 16:00:00'
    ),
    (
        5,
        'WS-20260620-0001',
        7,
        DATE '2026-06-20',
        TIME '11:00:00',
        1,
        '伊藤',
        '葵',
        'イトウ',
        'アオイ',
        'aoi.ito@example.com',
        '09055556666',
        '都合によりキャンセルしました。',
        5500,
        'CANCELLED',
        'キャンセル済み。残席には反映しない。',
        TIMESTAMP '2026-06-01 17:00:00',
        TIMESTAMP '2026-06-01 18:10:00'
    );

INSERT INTO inquiries (
    id,
    customer_family_name,
    customer_given_name,
    customer_family_kana,
    customer_given_kana,
    customer_email,
    customer_tel,
    customer_message,
    status,
    admin_memo,
    created_at,
    updated_at
)
VALUES
    (
        1,
        '中村',
        '悠',
        'ナカムラ',
        'ユウ',
        'yu.nakamura@example.com',
        '09066667777',
        '小学生でも参加できますか。',
        'OPEN',
        NULL,
        TIMESTAMP '2026-06-01 08:40:00',
        TIMESTAMP '2026-06-01 08:40:00'
    ),
    (
        2,
        '小林',
        '真奈',
        'コバヤシ',
        'マナ',
        'mana.kobayashi@example.com',
        '09077778888',
        '持ち物を教えてください。',
        'IN_PROGRESS',
        '返信文面を準備中',
        TIMESTAMP '2026-06-01 12:05:00',
        TIMESTAMP '2026-06-01 12:30:00'
    ),
    (
        3,
        '渡辺',
        '亮',
        'ワタナベ',
        'リョウ',
        'ryo.watanabe@example.com',
        '09088889999',
        '法人参加は可能ですか。',
        'CLOSED',
        'メール返信済み',
        TIMESTAMP '2026-06-01 16:25:00',
        TIMESTAMP '2026-06-01 17:10:00'
    );
