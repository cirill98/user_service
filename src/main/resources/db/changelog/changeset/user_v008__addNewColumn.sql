ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS banned boolean;