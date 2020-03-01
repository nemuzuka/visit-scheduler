CREATE TABLE private_schedules (
    private_schedule_id VARCHAR (128) NOT NULL,
    user_id VARCHAR (128) NOT NULL,
    target_date DATE NOT NULL,
    memo VARCHAR (1024),
    create_user_code VARCHAR (128) NOT NULL,
    create_at BIGINT NOT NULL,
    last_update_user_code VARCHAR (128) NOT NULL,
    last_update_at BIGINT NOT NULL,
    version_no BIGINT NOT NULL
);

ALTER TABLE private_schedules ADD CONSTRAINT private_schedules_primary PRIMARY KEY(private_schedule_id);
CREATE UNIQUE INDEX ui_private_schedules_user_id_target_date ON private_schedules (user_id, target_date);
ALTER TABLE private_schedules ADD CONSTRAINT fk_private_schedules_users FOREIGN KEY (user_id) REFERENCES users (user_id);
