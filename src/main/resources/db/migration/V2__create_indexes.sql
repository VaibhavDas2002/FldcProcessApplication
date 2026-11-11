-- ====================================================================
-- V2__create_indexes.sql
-- Optimizes query performance for role-wise queues and LGD filtering
-- ====================================================================

CREATE INDEX IF NOT EXISTS idx_families_district_role 
    ON dbt_apy.families (lgd_district_code, next_level_role_id) 
    WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_families_under_enquiry 
    ON dbt_apy.families (lgd_district_code, next_level_role_id, under_enquiry) 
    WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_families_create_mode 
    ON dbt_apy.families (lgd_district_code, create_mode) 
    WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_family_members_family_id 
    ON dbt_apy.family_members (family_id) 
    WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_family_members_role_gender 
    ON dbt_apy.family_members (lgd_district_code, next_level_role_id, gender, applying_for_annapurna_bhandar) 
    WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_accept_reject_info_family 
    ON dbt_apy.accept_reject_info (family_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_accept_reject_info_member 
    ON dbt_apy.accept_reject_info (family_member_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_cross_check_paid_lookup 
    ON cross_check.lb_data_paid_apy (family_id, lgd_district_code);

CREATE INDEX IF NOT EXISTS idx_ay_lot_master_status 
    ON cross_check.ay_lot_master (lot_status, lgd_district_code);

