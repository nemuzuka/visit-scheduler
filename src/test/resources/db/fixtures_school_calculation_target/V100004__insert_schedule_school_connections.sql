INSERT INTO schedule_school_connections
(
  schedule_school_connection_id, schedule_id, school_id,
  connection_index, calculation_target
)

VALUES

(
  'schedule_school_connection_001', 'schedule_id_001', 'school_id_002',
  1, true
),
(
  'schedule_school_connection_002', 'schedule_id_001', 'school_id_003',
  2, false
),
(
  'schedule_school_connection_003', 'schedule_id_002', 'school_id_004',
  1, false
)
