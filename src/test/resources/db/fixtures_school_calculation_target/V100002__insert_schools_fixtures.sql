INSERT INTO schools

(school_id, school_code, user_code, name, memo, attributes,
 create_user_code, create_at, last_update_user_code, last_update_at, version_no)

VALUES

(
 'school_id_002', 'school_code_002', 'user_code_001', '学校1', null, null,
 'create_user_002', 1646732800002, 'last_update_user_002', 1746732800002, 2
),
(
 'school_id_003', 'school_code_003', 'user_code_001', '学校2', 'メモ2', '{"hige":"hage"}',
 'create_user_001', 1646732800001, 'last_update_user_001', 1746732800001, 1
),
(
 'school_id_003_1', 'school_code_003_1', 'user_code_001', '学校3', 'メモ3', null,
 'create_user_004', 1646732800004, 'last_update_user_004', 1746732800004, 14
),
(
 'school_id_004', 'school_code_004', 'user_code_002', '学校2-1', 'メモ2-1', '{"hige1":"hage1"}',
 'create_user_003', 1646732800003, 'last_update_user_003', 1746732800003, 4
);
