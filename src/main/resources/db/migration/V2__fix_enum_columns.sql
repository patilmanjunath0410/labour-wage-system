-- Convert custom enum columns to VARCHAR
-- so JPA can handle them without type casting issues
-- Step 1: Remove DEFAULT constraints (VERY IMPORTANT)

ALTER TABLE users ALTER COLUMN role DROP DEFAULT;

ALTER TABLE workers
    ALTER COLUMN skill_type DROP DEFAULT,
    ALTER COLUMN status DROP DEFAULT;

ALTER TABLE attendance
    ALTER COLUMN attendance_type DROP DEFAULT,
    ALTER COLUMN entry_type DROP DEFAULT;

ALTER TABLE disputes ALTER COLUMN status DROP DEFAULT;

ALTER TABLE min_wage_rules ALTER COLUMN skill_type DROP DEFAULT;


-- Step 2: Convert ENUM columns to VARCHAR

ALTER TABLE users
    ALTER COLUMN role TYPE VARCHAR(20) USING role::text;

ALTER TABLE workers
    ALTER COLUMN skill_type TYPE VARCHAR(30) USING skill_type::text,
    ALTER COLUMN status TYPE VARCHAR(20) USING status::text;

ALTER TABLE attendance
    ALTER COLUMN attendance_type TYPE VARCHAR(20) USING attendance_type::text,
    ALTER COLUMN entry_type TYPE VARCHAR(20) USING entry_type::text;

ALTER TABLE disputes
    ALTER COLUMN status TYPE VARCHAR(20) USING status::text;

ALTER TABLE min_wage_rules
    ALTER COLUMN skill_type TYPE VARCHAR(30) USING skill_type::text;


-- Step 3: Drop ENUM types safely

DROP TYPE IF EXISTS role_enum CASCADE;
DROP TYPE IF EXISTS skill_type_enum CASCADE;
DROP TYPE IF EXISTS worker_status_enum CASCADE;
DROP TYPE IF EXISTS attendance_type_enum CASCADE;
DROP TYPE IF EXISTS entry_type_enum CASCADE;
DROP TYPE IF EXISTS dispute_status_enum CASCADE;
/*ALTER TABLE users
ALTER COLUMN role TYPE VARCHAR(20);

ALTER TABLE workers
ALTER COLUMN skill_type TYPE VARCHAR(30),
    ALTER COLUMN status TYPE VARCHAR(20);

ALTER TABLE attendance
ALTER COLUMN attendance_type TYPE VARCHAR(20),
    ALTER COLUMN entry_type TYPE VARCHAR(20);


ALTER TABLE disputes
ALTER COLUMN status TYPE VARCHAR(20);

ALTER TABLE min_wage_rules
ALTER COLUMN skill_type TYPE VARCHAR(30);

-- Drop the old PostgreSQL enum types
DROP TYPE IF EXISTS role_enum;
DROP TYPE IF EXISTS skill_type_enum;
DROP TYPE IF EXISTS worker_status_enum;
DROP TYPE IF EXISTS attendance_type_enum;
DROP TYPE IF EXISTS entry_type_enum;
DROP TYPE IF EXISTS dispute_status_enum;

 */