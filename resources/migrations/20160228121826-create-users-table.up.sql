CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    nickname text,
    email text NOT NULL UNIQUE CONSTRAINT email_not_empty CHECK (email != ''),
    password text NOT NULL CONSTRAINT password_not_empty CHECK (password != '')
);
