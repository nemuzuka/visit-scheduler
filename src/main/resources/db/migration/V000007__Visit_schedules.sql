CREATE TABLE visit_schedules (
    visit_schedule_id VARCHAR (128) NOT NULL,
    school_id VARCHAR (128) NOT NULL,
    visit_date DATE NOT NULL
);

ALTER TABLE visit_schedules ADD CONSTRAINT visit_schedules_primary PRIMARY KEY(visit_schedule_id);
CREATE UNIQUE INDEX ui_visit_schedules_school_id_visit_date ON visit_schedules (school_id, visit_date);
ALTER TABLE visit_schedules ADD CONSTRAINT fk_visit_schedules_schools FOREIGN KEY (school_id) REFERENCES schools (school_id) ON DELETE CASCADE;

-- school 削除時に追従するように fk 見直し
ALTER TABLE school_schedules DROP CONSTRAINT fk_school_schedules_schools;
ALTER TABLE school_schedules ADD CONSTRAINT fk_school_schedules_schools FOREIGN KEY (school_id) REFERENCES schools (school_id) ON DELETE CASCADE;
