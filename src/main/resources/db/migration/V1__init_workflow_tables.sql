-- ====================================================================
-- V1__init_workflow_tables.sql
-- Initializes schemas and core FLDC / Annapurna Yojana tables
-- ====================================================================

CREATE SCHEMA IF NOT EXISTS dbt_apy;
CREATE SCHEMA IF NOT EXISTS cross_check;

-- 1. Family Master Application Table
CREATE TABLE IF NOT EXISTS dbt_apy.families (
    id UUID PRIMARY KEY,
    app_serial BIGINT,
    tmp_application_id BIGINT,
    lgd_district_code INTEGER NOT NULL,
    created_by_dist_code INTEGER,
    created_by_local_body_code BIGINT,
    lgd_block_mc_code INTEGER,
    lgd_gp_ward_code INTEGER,
    create_mode CHAR(1) DEFAULT 'O',
    next_level_role_id INTEGER NOT NULL DEFAULT 0,
    application_status VARCHAR(50) DEFAULT 'DRAFT',
    detail_status INTEGER,
    under_enquiry BOOLEAN DEFAULT FALSE,
    is_agreed BOOLEAN DEFAULT FALSE,
    is_claim BOOLEAN DEFAULT FALSE,
    jks_id VARCHAR(50),
    verification_remarks TEXT,
    verification_datetime TIMESTAMP,
    verified_by BIGINT,
    approval_remarks TEXT,
    approval_datetime TIMESTAMP,
    approved_by BIGINT,
    enquiry_remarks TEXT,
    enquiry_datetime TIMESTAMP,
    enquiry_conducted_by BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT
);

-- 2. Family Members Table
CREATE TABLE IF NOT EXISTS dbt_apy.family_members (
    id UUID PRIMARY KEY,
    family_id UUID NOT NULL REFERENCES dbt_apy.families(id),
    family_serial BIGINT,
    gender CHAR(1),
    dob DATE,
    age INTEGER,
    applying_for_annapurna_bhandar BOOLEAN DEFAULT FALSE,
    lgd_district_code INTEGER NOT NULL,
    created_by_dist_code INTEGER,
    created_by_local_body_code BIGINT,
    lgd_block_mc_code INTEGER,
    lgd_gp_ward_code INTEGER,
    create_mode CHAR(1) DEFAULT 'O',
    next_level_role_id INTEGER NOT NULL DEFAULT 0,
    application_status VARCHAR(50) DEFAULT 'DRAFT',
    detail_status INTEGER,
    under_enquiry BOOLEAN DEFAULT FALSE,
    lb_data_paid_apy BOOLEAN DEFAULT FALSE,
    july_payment BOOLEAN DEFAULT FALSE,
    is_claim BOOLEAN DEFAULT FALSE,
    is_asap BOOLEAN DEFAULT FALSE,
    not_in_asdd BOOLEAN DEFAULT FALSE,
    appeal_tribunal TEXT,
    verification_remarks TEXT,
    verification_datetime TIMESTAMP,
    verified_by BIGINT,
    approval_remarks TEXT,
    approval_datetime TIMESTAMP,
    approved_by BIGINT,
    enquiry_remarks TEXT,
    enquiry_datetime TIMESTAMP,
    enquiry_conducted_by BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT
);

-- 3. Immutable Audit Trail Table
CREATE TABLE IF NOT EXISTS dbt_apy.accept_reject_info (
    id BIGSERIAL PRIMARY KEY,
    family_id UUID NOT NULL,
    family_member_id UUID,
    lgd_district_code INTEGER NOT NULL,
    op_type VARCHAR(50) NOT NULL,
    reject_revert_cause_id INTEGER,
    remarks TEXT,
    user_id BIGINT NOT NULL,
    role_id INTEGER,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Cross-Check Paid Beneficiaries Table
CREATE TABLE IF NOT EXISTS cross_check.lb_data_paid_apy (
    id BIGSERIAL PRIMARY KEY,
    family_id UUID NOT NULL,
    lgd_district_code INTEGER NOT NULL
);

-- 5. Batch Lot Master Table
CREATE TABLE IF NOT EXISTS cross_check.ay_lot_master (
    id BIGSERIAL PRIMARY KEY,
    lot_no VARCHAR(50),
    lgd_district_code INTEGER NOT NULL,
    total_members INTEGER,
    lot_status INTEGER DEFAULT 0,
    approved_by BIGINT,
    approved_at TIMESTAMP,
    approver_remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Batch Lot Details Table
CREATE TABLE IF NOT EXISTS cross_check.ay_lot_details (
    id BIGSERIAL PRIMARY KEY,
    lot_id BIGINT NOT NULL REFERENCES cross_check.ay_lot_master(id),
    family_id UUID NOT NULL,
    family_member_id UUID NOT NULL,
    lgd_district_code INTEGER NOT NULL,
    is_rejected BOOLEAN DEFAULT FALSE,
    approved_by BIGINT,
    approved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

