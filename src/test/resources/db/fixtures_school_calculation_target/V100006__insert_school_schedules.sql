INSERT INTO school_schedules
(
  school_schedule_id, school_id, target_date,priority,memo,
  create_user_code, create_at, last_update_user_code, last_update_at, version_no
)

VALUES

(
  'school_schedule_id_001', 'school_id_002', '2019-01-05', 'POSSIBLE', 'memo1',
  'create_user_001', 1746732810001, 'last_update_user_001', 1846732820001, 1
),
(
  'school_schedule_id_002', 'school_id_002', '2019-01-20', 'DONT_COME', null,
  'create_user_002', 1746732810002, 'last_update_user_002', 1846732820002, 2
),
(
  'school_schedule_id_003', 'school_id_002', '2018-12-31', 'ABSOLUTELY', '12月分',
  'create_user_003', 1746732810003, 'last_update_user_003', 1846732820003, 3
),
(
  'school_schedule_id_004', 'school_id_003_1', '2019-01-13', 'DONT_COME', null,
  'create_user_004', 1746732810004, 'last_update_user_004', 1846732820004, 4
)
