CREATE SEQUENCE IF NOT EXISTS sq_users START WITH 1 INCREMENT BY 10;
CREATE SEQUENCE IF NOT EXISTS sq_notes START WITH 1 INCREMENT BY 25;

CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT                      NOT NULL PRIMARY KEY,
    email      VARCHAR(250)                NOT NULL,
    full_name  VARCHAR(500),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_email ON users (email);

-- No foreign key to users: notes and users are separate application modules.
CREATE TABLE IF NOT EXISTS notes
(
    id         BIGINT                      NOT NULL PRIMARY KEY,
    title      VARCHAR(1000),
    content    VARCHAR(3000),
    user_id    BIGINT                      NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);
