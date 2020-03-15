INSERT INTO private_schedules
(
  private_schedule_id, user_id, target_date,memo,
  create_user_code, create_at, last_update_user_code, last_update_at, version_no
)

VALUES

(
  'private_schedule_id_0001', 'user_id_001', '2019-01-03', 'memo1',
  'create_user_001', 1646732810001, 'last_update_user_001', 1746732820001, 1
),
(
  'private_schedule_id_0002', 'user_id_001', '2019-01-15', null,
  'create_user_002', 1646732810002, 'last_update_user_002', 1746732820002, 2
),
(
  'private_schedule_id_0003', 'user_id_001', '2018-12-31', '12月のスケジュール',
  'create_user_003', 1646732810003, 'last_update_user_003', 1746732820003, 3
),
(
  'private_schedule_id_0004', 'user_id_002', '2019-01-28', '別のユーザのスケジュール',
  'create_user_004', 1646732810004, 'last_update_user_004', 1746732820004, 4
)
