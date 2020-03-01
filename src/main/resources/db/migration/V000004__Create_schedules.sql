CREATE TABLE schedules (
    schedule_id VARCHAR (128) NOT NULL,
    schedule_code VARCHAR (128) NOT NULL,
    user_id VARCHAR (128) NOT NULL,
    target_start_date DATE NOT NULL,
    attributes TEXT,
    create_user_code VARCHAR (128) NOT NULL,
    create_at BIGINT NOT NULL,
    last_update_user_code VARCHAR (128) NOT NULL,
    last_update_at BIGINT NOT NULL,
    version_no BIGINT NOT NULL
);

ALTER TABLE schedules ADD CONSTRAINT schedules_primary PRIMARY KEY(schedule_id);
CREATE UNIQUE INDEX ui_schedules_schedule_code ON schedules (schedule_code);
CREATE UNIQUE INDEX ui_schedules_user_id_target_start_date ON schedules (user_id, target_start_date);
ALTER TABLE schedules ADD CONSTRAINT fk_schedules_users FOREIGN KEY (user_id) REFERENCES users (user_id);

CREATE TABLE schedule_school_connections (
    schedule_school_connection_id VARCHAR (128) NOT NULL,
    schedule_id VARCHAR (128) NOT NULL,
    school_id VARCHAR (128) NOT NULL,
    connection_index BIGINT NOT NULL,
    calculation_target BOOLEAN NOT NULL
);

ALTER TABLE schedule_school_connections ADD CONSTRAINT schedule_school_connections_primary PRIMARY KEY(schedule_school_connection_id);
CREATE UNIQUE INDEX ui_schedule_school_connections_schedule_id_school_id ON schedule_school_connections (schedule_id, school_id);
CREATE INDEX ix_schedule_school_connections_schedule_id_connection_index ON schedule_school_connections (schedule_id, connection_index);
ALTER TABLE schedule_school_connections ADD CONSTRAINT fk_schedule_school_connections_schedules FOREIGN KEY (schedule_id) REFERENCES schedules (schedule_id) ON DELETE CASCADE;
ALTER TABLE schedule_school_connections ADD CONSTRAINT fk_schedule_school_connections_schools FOREIGN KEY (school_id) REFERENCES schools (school_id) ON DELETE CASCADE;
