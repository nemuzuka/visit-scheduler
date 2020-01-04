CREATE TABLE authenticated_principals (
    authenticated_principal_id VARCHAR (128) NOT NULL,
    principal VARCHAR (128) NOT NULL,
    authorized_client_registration_id VARCHAR (128) NOT NULL
);

ALTER TABLE authenticated_principals ADD CONSTRAINT authenticated_principals_primary PRIMARY KEY(authenticated_principal_id);
CREATE UNIQUE INDEX ui_authenticated_principals_principal_id ON authenticated_principals (principal, authorized_client_registration_id);


CREATE TABLE users (
    user_id VARCHAR (128) NOT NULL,
    user_code VARCHAR (128) NOT NULL,
    authenticated_principal_id VARCHAR (128) NOT NULL,
    user_name VARCHAR (128) NOT NULL
);

ALTER TABLE users ADD CONSTRAINT users_primary PRIMARY KEY(user_id);
CREATE UNIQUE INDEX ui_users_user_code ON users (user_code);
ALTER TABLE users ADD CONSTRAINT fk_users_authenticated_principals FOREIGN KEY (authenticated_principal_id) REFERENCES authenticated_principals(authenticated_principal_id);
