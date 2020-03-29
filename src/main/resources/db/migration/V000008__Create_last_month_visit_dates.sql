CREATE TABLE last_month_visit_dates (
    last_month_visit_date_id VARCHAR (128) NOT NULL,
    school_id VARCHAR (128) NOT NULL,
    target_year_and_month VARCHAR (7) NOT NULL,
    last_month_visit_date DATE
);

ALTER TABLE last_month_visit_dates ADD CONSTRAINT last_month_visit_dates_primary PRIMARY KEY(last_month_visit_date_id);
CREATE UNIQUE INDEX ui_last_month_visit_dates_school_id_target_year_and_month ON last_month_visit_dates (school_id, target_year_and_month);
ALTER TABLE last_month_visit_dates ADD CONSTRAINT fk_last_month_visit_dates_schools FOREIGN KEY (school_id) REFERENCES schools (school_id) ON DELETE CASCADE;
