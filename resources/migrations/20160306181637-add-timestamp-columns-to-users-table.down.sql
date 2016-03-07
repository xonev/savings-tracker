ALTER TABLE IF EXISTS users
    DROP COLUMN IF EXISTS updated_at,
    DROP COLUMN IF EXISTS created_at;
--;
DROP TRIGGER update_users_updated_at_timestamp ON users;
