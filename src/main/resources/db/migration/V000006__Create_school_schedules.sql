CREATE TABLE school_schedules (
    school_schedule_id VARCHAR (128) NOT NULL,
    school_id VARCHAR (128) NOT NULL,
    target_date DATE NOT NULL,
    priority VARCHAR (10) NOT NULL,
    memo VARCHAR (1024),
    create_user_code VARCHAR (128) NOT NULL,
    create_at BIGINT NOT NULL,
    last_update_user_code VARCHAR (128) NOT NULL,
    last_update_at BIGINT NOT NULL,
    version_no BIGINT NOT NULL
);

ALTER TABLE school_schedules ADD CONSTRAINT school_schedules_primary PRIMARY KEY(school_schedule_id);
CREATE UNIQUE INDEX ui_school_schedules_school_id_target_date ON school_schedules (school_id, target_date);
ALTER TABLE school_schedules ADD CONSTRAINT fk_school_schedules_schools FOREIGN KEY (school_id) REFERENCES schools (school_id);
