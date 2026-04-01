-- Convert custom enum columns to VARCHAR
-- so JPA can handle them without type casting issues

ALTER TABLE users
ALTER COLUMN role TYPE VARCHAR(20);

ALTER TABLE workers
ALTER COLUMN skill_type TYPE VARCHAR(30),
    ALTER COLUMN status TYPE VARCHAR(20);

ALTER TABLE attendance
ALTER COLUMN attendance_type TYPE VARCHAR(20),
    ALTER COLUMN entry_type TYPE VARCHAR(20);

ALTER TABLE wage_slips
ALTER COLUMN attendance_type TYPE VARCHAR(20);

ALTER TABLE disputes
ALTER COLUMN status TYPE VARCHAR(20);

-- Drop the old PostgreSQL enum types
DROP TYPE IF EXISTS role_enum;
DROP TYPE IF EXISTS skill_type_enum;
DROP TYPE IF EXISTS worker_status_enum;
DROP TYPE IF EXISTS attendance_type_enum;
DROP TYPE IF EXISTS entry_type_enum;
DROP TYPE IF EXISTS dispute_status_enum;