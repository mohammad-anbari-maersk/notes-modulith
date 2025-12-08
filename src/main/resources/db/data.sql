-- Ensure sequences exist (for PostgreSQL)
-- CREATE SEQUENCE IF NOT EXISTS sq_users INCREMENT BY 1 MINVALUE 1 START WITH 1;
-- CREATE SEQUENCE IF NOT EXISTS sq_notes INCREMENT BY 1 MINVALUE 1 START WITH 1;

-- Users
INSERT INTO users (id, email, full_name, created_at, updated_at)
VALUES (NEXTVAL('sq_users'), 'jane.doe@example.com', 'Jane Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, email, full_name, created_at, updated_at)
VALUES (NEXTVAL('sq_users'), 'john.doe@example.com', 'John Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Notes
INSERT INTO notes (id, title, content, user_id, created_at, updated_at)
VALUES (NEXTVAL('sq_notes'), 'Welcome note', 'This is Jane''s first note.', (select id from users where email = 'jane.doe@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (id, title, content, user_id, created_at, updated_at)
VALUES (NEXTVAL('sq_notes'), 'Todo', 'Buy milk; write unit tests.', (select id from users where email = 'jane.doe@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (id, title, content, user_id, created_at, updated_at)
VALUES(NEXTVAL('sq_notes'), 'Quick idea', 'Experiment with modulith events.', (select id from users where email = 'john.doe@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (id, title, content, user_id, created_at, updated_at)
VALUES(NEXTVAL('sq_notes'), 'Meeting notes', 'Discussed API contracts and mappers.', (select id from users where email = 'john.doe@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
