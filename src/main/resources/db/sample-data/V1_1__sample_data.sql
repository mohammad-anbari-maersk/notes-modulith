-- Demo data. Applied once by Flyway because db/sample-data is listed in spring.flyway.locations.
-- Remove that location for any environment that must start empty.

INSERT INTO users (id, email, full_name, created_at, updated_at)
VALUES (NEXTVAL('sq_users'), 'jane.doe@example.com', 'Jane Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, email, full_name, created_at, updated_at)
VALUES (NEXTVAL('sq_users'), 'john.doe@example.com', 'John Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (id, title, content, user_id, created_at, updated_at)
VALUES (NEXTVAL('sq_notes'), 'Welcome note', 'This is Jane''s first note.', (SELECT id FROM users WHERE email = 'jane.doe@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (id, title, content, user_id, created_at, updated_at)
VALUES (NEXTVAL('sq_notes'), 'Todo', 'Buy milk; write unit tests.', (SELECT id FROM users WHERE email = 'jane.doe@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (id, title, content, user_id, created_at, updated_at)
VALUES (NEXTVAL('sq_notes'), 'Quick idea', 'Experiment with modulith events.', (SELECT id FROM users WHERE email = 'john.doe@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notes (id, title, content, user_id, created_at, updated_at)
VALUES (NEXTVAL('sq_notes'), 'Meeting notes', 'Discussed API contracts and mappers.', (SELECT id FROM users WHERE email = 'john.doe@example.com'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
