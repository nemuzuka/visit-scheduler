CREATE TABLE schools (
    school_id VARCHAR (128) NOT NULL,
    school_code VARCHAR (128) NOT NULL,
    user_code VARCHAR (128) NOT NULL,
    name VARCHAR (256) NOT NULL,
    memo VARCHAR (1024),
    attributes TEXT,
    create_user_code VARCHAR (128) NOT NULL,
    create_at BIGINT NOT NULL,
    last_update_user_code VARCHAR (128) NOT NULL,
    last_update_at BIGINT NOT NULL,
    version_no BIGINT NOT NULL
);

ALTER TABLE schools ADD CONSTRAINT schools_primary PRIMARY KEY(school_id);
CREATE UNIQUE INDEX ui_schools_school_code_user_code ON schools (school_code, user_code);
CREATE INDEX ix_schools_user_code_create_at ON schools(user_code, create_at);
