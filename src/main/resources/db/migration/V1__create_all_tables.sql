-- ENUMS
CREATE TYPE role_enum AS ENUM (
    'ADMIN', 'CONTRACTOR', 'SUPERVISOR', 'WORKER'
);

CREATE TYPE attendance_type_enum AS ENUM (
    'FULL_DAY', 'HALF_DAY', 'OVERTIME',
    'DOUBLE_OVERTIME', 'ABSENT'
);

CREATE TYPE entry_type_enum AS ENUM (
    'QR_SCAN', 'MANUAL_ENTRY', 'SYSTEM_AUTO'
);

CREATE TYPE skill_type_enum AS ENUM (
    'MASON', 'CARPENTER', 'ELECTRICIAN',
    'PLUMBER', 'WELDER', 'PAINTER',
    'HELPER', 'SUPERVISOR_SKILL', 'OTHER'
);

CREATE TYPE worker_status_enum AS ENUM (
    'ACTIVE', 'INACTIVE', 'SUSPENDED'
);

CREATE TYPE dispute_status_enum AS ENUM (
    'OPEN', 'UNDER_REVIEW', 'RESOLVED', 'REJECTED'
);

-- ─────────────────────────────────────────
-- TABLE: contractors
-- ─────────────────────────────────────────
CREATE TABLE contractors (
                             id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             company_name    VARCHAR(200) NOT NULL,
                             owner_name      VARCHAR(100) NOT NULL,
                             phone           VARCHAR(15)  NOT NULL UNIQUE,
                             email           VARCHAR(150) UNIQUE,
                             gstin           VARCHAR(15),
                             state           VARCHAR(50)  NOT NULL,
                             address         TEXT,
                             is_active       BOOLEAN NOT NULL DEFAULT TRUE,
                             created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                             updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─────────────────────────────────────────
-- TABLE: sites
-- ─────────────────────────────────────────
CREATE TABLE sites (
                       id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       contractor_id   UUID NOT NULL REFERENCES contractors(id),
                       site_name       VARCHAR(200) NOT NULL,
                       site_code       VARCHAR(20)  NOT NULL UNIQUE,
                       location        TEXT NOT NULL,
                       state           VARCHAR(50)  NOT NULL,
                       city            VARCHAR(100) NOT NULL,
                       is_active       BOOLEAN NOT NULL DEFAULT TRUE,
                       start_date      DATE,
                       end_date        DATE,
                       created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─────────────────────────────────────────
-- TABLE: users (auth for all roles)
-- ─────────────────────────────────────────
CREATE TABLE users (
                       id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name            VARCHAR(100) NOT NULL,
                       phone           VARCHAR(15)  NOT NULL UNIQUE,
                       email           VARCHAR(150) UNIQUE,
                       password_hash   VARCHAR(255) NOT NULL,
                       role            role_enum    NOT NULL,
                       contractor_id   UUID REFERENCES contractors(id),
                       site_id         UUID REFERENCES sites(id),
                       is_active       BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─────────────────────────────────────────
-- TABLE: workers
-- ─────────────────────────────────────────
CREATE TABLE workers (
                         id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         worker_code         VARCHAR(30)  NOT NULL UNIQUE,
                         full_name           VARCHAR(100) NOT NULL,
                         phone               VARCHAR(15)  NOT NULL,
                         aadhaar_hash        VARCHAR(64)  NOT NULL UNIQUE,
                         aadhaar_last_four   CHAR(4)      NOT NULL,
                         e_shram_card_no     VARCHAR(20),
                         date_of_birth       DATE         NOT NULL,
                         gender              VARCHAR(10)  NOT NULL,
                         address             TEXT         NOT NULL,
                         photo_url           VARCHAR(500),
                         skill_type          skill_type_enum NOT NULL,
                         daily_wage_rate     NUMERIC(10,2)   NOT NULL,
                         bank_account_enc    VARCHAR(500),
                         ifsc_code           VARCHAR(11),
                         upi_id              VARCHAR(100),
                         emergency_contact   VARCHAR(15),
                         status              worker_status_enum NOT NULL DEFAULT 'ACTIVE',
                         site_id             UUID NOT NULL REFERENCES sites(id),
                         contractor_id       UUID NOT NULL REFERENCES contractors(id),
                         qr_url              VARCHAR(500),
                         otp_verified        BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─────────────────────────────────────────
-- TABLE: attendance
-- ─────────────────────────────────────────
CREATE TABLE attendance (
                            id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            worker_id           UUID NOT NULL REFERENCES workers(id),
                            site_id             UUID NOT NULL REFERENCES sites(id),
                            contractor_id       UUID NOT NULL REFERENCES contractors(id),
                            supervisor_id       UUID NOT NULL REFERENCES users(id),
                            attendance_date     DATE NOT NULL,
                            attendance_type     attendance_type_enum NOT NULL,
                            wage_multiplier     NUMERIC(3,2)  NOT NULL,
                            daily_wage_rate     NUMERIC(10,2) NOT NULL,
                            computed_wage       NUMERIC(10,2) NOT NULL,
                            scanned_at          TIMESTAMPTZ   NOT NULL,
                            synced_at           TIMESTAMPTZ,
                            entry_type          entry_type_enum NOT NULL DEFAULT 'QR_SCAN',
                            is_verified         BOOLEAN NOT NULL DEFAULT TRUE,
                            override_reason     TEXT,
                            is_disputed         BOOLEAN NOT NULL DEFAULT FALSE,
                            created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                            updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                            CONSTRAINT uq_worker_date_site
                                UNIQUE (worker_id, attendance_date, site_id)
);

-- ─────────────────────────────────────────
-- TABLE: wage_slips
-- ─────────────────────────────────────────
CREATE TABLE wage_slips (
                            id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            worker_id           UUID NOT NULL REFERENCES workers(id),
                            site_id             UUID NOT NULL REFERENCES sites(id),
                            contractor_id       UUID NOT NULL REFERENCES contractors(id),
                            slip_month          DATE NOT NULL,
                            total_days_present  INTEGER NOT NULL DEFAULT 0,
                            full_days           INTEGER NOT NULL DEFAULT 0,
                            half_days           INTEGER NOT NULL DEFAULT 0,
                            overtime_days       INTEGER NOT NULL DEFAULT 0,
                            gross_wage          NUMERIC(10,2) NOT NULL,
                            pf_deduction        NUMERIC(10,2) NOT NULL DEFAULT 0,
                            esi_deduction       NUMERIC(10,2) NOT NULL DEFAULT 0,
                            advance_deduction   NUMERIC(10,2) NOT NULL DEFAULT 0,
                            net_wage            NUMERIC(10,2) NOT NULL,
                            pdf_url             VARCHAR(500),
                            generated_at        TIMESTAMPTZ,
                            created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                            CONSTRAINT uq_worker_slip_month
                                UNIQUE (worker_id, slip_month, site_id)
);

-- ─────────────────────────────────────────
-- TABLE: disputes
-- ─────────────────────────────────────────
CREATE TABLE disputes (
                          id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          attendance_id       UUID NOT NULL REFERENCES attendance(id),
                          worker_id           UUID NOT NULL REFERENCES workers(id),
                          contractor_id       UUID NOT NULL REFERENCES contractors(id),
                          raised_by           UUID NOT NULL REFERENCES users(id),
                          reason              TEXT NOT NULL,
                          status              dispute_status_enum NOT NULL DEFAULT 'OPEN',
                          resolution_note     TEXT,
                          resolved_by         UUID REFERENCES users(id),
                          resolved_at         TIMESTAMPTZ,
                          created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─────────────────────────────────────────
-- TABLE: min_wage_rules
-- ─────────────────────────────────────────
CREATE TABLE min_wage_rules (
                                id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                state               VARCHAR(50)     NOT NULL,
                                skill_type          skill_type_enum NOT NULL,
                                min_daily_rate      NUMERIC(10,2)   NOT NULL,
                                effective_from      DATE            NOT NULL,
                                created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                                CONSTRAINT uq_state_skill_date
                                    UNIQUE (state, skill_type, effective_from)
);

-- ─────────────────────────────────────────
-- INDEXES
-- ─────────────────────────────────────────
CREATE INDEX idx_workers_site         ON workers(site_id);
CREATE INDEX idx_workers_contractor   ON workers(contractor_id);
CREATE INDEX idx_att_worker_date      ON attendance(worker_id, attendance_date);
CREATE INDEX idx_att_site_date        ON attendance(site_id, attendance_date);
CREATE INDEX idx_att_contractor_date  ON attendance(contractor_id, attendance_date);
CREATE INDEX idx_att_scanned_at       ON attendance(scanned_at);
CREATE INDEX idx_att_disputed         ON attendance(is_disputed) WHERE is_disputed = TRUE;
CREATE INDEX idx_wage_slips_worker    ON wage_slips(worker_id, slip_month);
CREATE INDEX idx_sites_contractor     ON sites(contractor_id);

-- ─────────────────────────────────────────
-- AUTO updated_at TRIGGER
-- ─────────────────────────────────────────
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_contractors_updated_at
    BEFORE UPDATE ON contractors
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_sites_updated_at
    BEFORE UPDATE ON sites
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_workers_updated_at
    BEFORE UPDATE ON workers
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_attendance_updated_at
    BEFORE UPDATE ON attendance
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_disputes_updated_at
    BEFORE UPDATE ON disputes
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─────────────────────────────────────────
-- SEED: min wage rules (Karnataka sample)
-- ─────────────────────────────────────────
INSERT INTO min_wage_rules (state, skill_type, min_daily_rate, effective_from) VALUES
                                                                                   ('Karnataka', 'MASON',       '738.00', '2024-01-01'),
                                                                                   ('Karnataka', 'CARPENTER',   '738.00', '2024-01-01'),
                                                                                   ('Karnataka', 'ELECTRICIAN', '738.00', '2024-01-01'),
                                                                                   ('Karnataka', 'PLUMBER',     '738.00', '2024-01-01'),
                                                                                   ('Karnataka', 'WELDER',      '738.00', '2024-01-01'),
                                                                                   ('Karnataka', 'PAINTER',     '672.00', '2024-01-01'),
                                                                                   ('Karnataka', 'HELPER',      '602.00', '2024-01-01'),
                                                                                   ('Karnataka', 'OTHER',       '602.00', '2024-01-01'),
                                                                                   ('Maharashtra','MASON',      '712.00', '2024-01-01'),
                                                                                   ('Maharashtra','HELPER',     '578.00', '2024-01-01'),
                                                                                   ('Delhi',      'MASON',      '792.00', '2024-01-01'),
                                                                                   ('Delhi',      'HELPER',     '648.00', '2024-01-01');
