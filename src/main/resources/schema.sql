CREATE TABLE IF NOT EXISTS telegram_user(
    user_id INTEGER PRIMARY KEY,
    language TEXT NOT NULL,
    model TEXT NOT NULL
);