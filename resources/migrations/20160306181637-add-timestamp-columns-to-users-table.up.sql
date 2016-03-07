ALTER TABLE users
    ADD COLUMN updated_at TIMESTAMPTZ DEFAULT null,
    ADD COLUMN created_at TIMESTAMPTZ DEFAULT (NOW() AT TIME ZONE 'utc');
--;
CREATE TRIGGER update_users_updated_at_timestamp
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
