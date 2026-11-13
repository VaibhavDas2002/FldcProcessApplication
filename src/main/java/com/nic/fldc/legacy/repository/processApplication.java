package com.nic.fldc.legacy.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Repository;

import com.nic.fldc.legacy.service.ZoneDatabaseManager;
import com.nic.fldc.legacy.util.GlobalUpdateCheckHelper;
import com.nic.fldc.legacy.util.ZoneDB;

@Repository("processApplicationRepository")
public class processApplication {

    private final ZoneDatabaseManager zoneDatabaseManager;

    public processApplication() {
        this(null);
    }

    public processApplication(ZoneDatabaseManager zoneDatabaseManager) {
        this.zoneDatabaseManager = zoneDatabaseManager;
    }

    private ZoneDatabaseManager getZoneDatabaseManager() {
        if (this.zoneDatabaseManager != null) {
            return this.zoneDatabaseManager;
        }
        return ZoneDB.getManager();
    }

    /**
     * Updates family application status and next_level_role_id in dbt_apy.families.
     */
    public int updateFamilyStatus(
            NamedParameterJdbcTemplate jdbcTemplate,
            int distCode,
            String familyId,
            Object localBodyCode,
            String applicationStatus,
            int nextLevelRoleId) {

        StringBuilder query = new StringBuilder("""
                UPDATE dbt_apy.families
                SET application_status = :applicationStatus,
                    next_level_role_id = :nextLevelRoleId
                WHERE lgd_district_code = :distCode
                  AND created_by_dist_code = :distCode
                  AND id = CAST(:family_id AS uuid)
                  AND is_deleted = false
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("distCode", distCode);
        params.addValue("family_id", familyId);
        params.addValue("applicationStatus", applicationStatus);
        params.addValue("nextLevelRoleId", nextLevelRoleId);

        if (localBodyCode != null) {
            query.append(" AND created_by_local_body_code = :localBodyCode");
            params.addValue("localBodyCode", GlobalUpdateCheckHelper.normalizeLocalBodyCode(localBodyCode));
        }

        return jdbcTemplate.update(query.toString(), params);
    }

    /**
     * Updates family members status and next_level_role_id in dbt_apy.family_members.
     */
    public int updateFamilyMembersStatus(
            NamedParameterJdbcTemplate jdbcTemplate,
            int distCode,
            String familyId,
            Object localBodyCode,
            String applicationStatus,
            int nextLevelRoleId) {

        StringBuilder query = new StringBuilder("""
                UPDATE dbt_apy.family_members
                SET application_status = :applicationStatus,
                    next_level_role_id = :nextLevelRoleId
                WHERE lgd_district_code = :distCode
                  AND created_by_dist_code = :distCode
                  AND family_id = CAST(:family_id AS uuid)
                  AND next_level_role_id <> 30
                  AND is_deleted = false
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("distCode", distCode);
        params.addValue("family_id", familyId);
        params.addValue("applicationStatus", applicationStatus);
        params.addValue("nextLevelRoleId", nextLevelRoleId);

        if (localBodyCode != null) {
            query.append(" AND created_by_local_body_code = :localBodyCode");
            params.addValue("localBodyCode", GlobalUpdateCheckHelper.normalizeLocalBodyCode(localBodyCode));
        }

        return jdbcTemplate.update(query.toString(), params);
    }

    /**
     * Queries members affected by the status update for audit logging.
     */
    public List<Map<String, Object>> findAffectedMembers(
            NamedParameterJdbcTemplate jdbcTemplate,
            int distCode,
            String familyId,
            Object localBodyCode) {

        StringBuilder query = new StringBuilder("""
                SELECT id, family_serial, lgd_district_code
                FROM dbt_apy.family_members
                WHERE lgd_district_code = :distCode
                  AND created_by_dist_code = :distCode
                  AND family_id = CAST(:family_id AS uuid)
                  AND next_level_role_id <> 30
                  AND is_deleted = false
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("distCode", distCode);
        params.addValue("family_id", familyId);

        if (localBodyCode != null) {
            query.append(" AND created_by_local_body_code = :localBodyCode");
            params.addValue("localBodyCode", GlobalUpdateCheckHelper.normalizeLocalBodyCode(localBodyCode));
        }

        return jdbcTemplate.queryForList(query.toString(), params);
    }

    /**
     * Batch inserts audit trail into dbt_apy.accept_reject_info.
     */
    public int[] insertAcceptRejectLogs(
            NamedParameterJdbcTemplate jdbcTemplate,
            List<SqlParameterSource> logRows) {

        String insertLogSql = """
                INSERT INTO dbt_apy.accept_reject_info (
                    tmp_application_id,
                    lgd_district_code,
                    created_by_dist_code,
                    created_by_local_body_code,
                    op_type,
                    user_id,
                    remarks,
                    ip_address,
                    app_serial,
                    family_serial,
                    family_id,
                    member_id,
                    created_at,
                    updated_at
                ) VALUES (
                    :tmp_application_id,
                    :lgd_district_code,
                    :created_by_dist_code,
                    :created_by_local_body_code,
                    :op_type,
                    :user_id,
                    :remarks,
                    :ip_address,
                    :app_serial,
                    :family_serial,
                    :family_id,
                    :member_id,
                    :created_at,
                    :updated_at
                )
                """;

        return jdbcTemplate.batchUpdate(insertLogSql, logRows.toArray(new SqlParameterSource[0]));
    }

    /**
     * Executes the complete transactional accept family operation.
     */
    public Map<String, Object> acceptFamily(
            int distCode,
            String familyId,
            Object localBodyCode,
            Object userId,
            String remarks,
            String ipAddress,
            Map<String, Object> family) throws SQLException {

        ZoneDatabaseManager dbManager = getZoneDatabaseManager();
        DataSource writeDataSource = dbManager.getWriteDataSource(distCode);

        try (Connection conn = writeDataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                NamedParameterJdbcTemplate writeJdbcTemplate = new NamedParameterJdbcTemplate(
                        new SingleConnectionDataSource(conn, true));

                // 1. Update dbt_apy.families
                int familyUpdated = updateFamilyStatus(
                        writeJdbcTemplate,
                        distCode,
                        familyId,
                        localBodyCode,
                        "ACCEPTED",
                        18);

                boolean isSaved = familyUpdated > 0;

                // 2. Update dbt_apy.family_members
                updateFamilyMembersStatus(
                        writeJdbcTemplate,
                        distCode,
                        familyId,
                        localBodyCode,
                        "ACCEPTED",
                        18);

                // 3. Query affected members
                List<Map<String, Object>> affectedMembers = findAffectedMembers(
                        writeJdbcTemplate,
                        distCode,
                        familyId,
                        localBodyCode);

                // 4. Build audit log rows
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                List<SqlParameterSource> logRows = new ArrayList<>();

                UUID tmpAppUuid = parseUuid(family.get("tmp_application_id"));
                UUID familyUuid = UUID.fromString(familyId);
                Object appSerial = family.get("app_serial");
                Object normalizedLbc = GlobalUpdateCheckHelper.normalizeLocalBodyCode(localBodyCode);
                Object normalizedUserId = normalizeUserId(userId);

                for (Map<String, Object> affectedMember : affectedMembers) {
                    MapSqlParameterSource mLog = new MapSqlParameterSource();
                    mLog.addValue("tmp_application_id", tmpAppUuid, Types.OTHER);

                    Object memberDist = affectedMember.get("lgd_district_code");
                    if (memberDist == null) {
                        memberDist = family.get("lgd_district_code");
                    }
                    mLog.addValue("lgd_district_code", GlobalUpdateCheckHelper.normalizeDistrictCode(memberDist),
                            Types.INTEGER);
                    mLog.addValue("created_by_dist_code", distCode, Types.INTEGER);
                    mLog.addValue("created_by_local_body_code", normalizedLbc);
                    mLog.addValue("op_type", "ACCEPT_FAMILY", Types.VARCHAR);
                    mLog.addValue("user_id", normalizedUserId);
                    mLog.addValue("remarks", remarks, Types.VARCHAR);
                    mLog.addValue("ip_address", ipAddress, Types.VARCHAR);
                    mLog.addValue("app_serial", appSerial);
                    mLog.addValue("family_serial", affectedMember.get("family_serial"));
                    mLog.addValue("family_id", familyUuid, Types.OTHER);
                    mLog.addValue("member_id", parseUuid(affectedMember.get("id")), Types.OTHER);
                    mLog.addValue("created_at", now, Types.TIMESTAMP);
                    mLog.addValue("updated_at", now, Types.TIMESTAMP);
                    logRows.add(mLog);
                }

                // Family-level log row
                MapSqlParameterSource fLog = new MapSqlParameterSource();
                fLog.addValue("tmp_application_id", tmpAppUuid, Types.OTHER);
                fLog.addValue("lgd_district_code",
                        GlobalUpdateCheckHelper.normalizeDistrictCode(family.get("lgd_district_code")),
                        Types.INTEGER);
                fLog.addValue("created_by_dist_code", distCode, Types.INTEGER);
                fLog.addValue("created_by_local_body_code", normalizedLbc);
                fLog.addValue("op_type", "ACCEPT_FAMILY", Types.VARCHAR);
                fLog.addValue("user_id", normalizedUserId);
                fLog.addValue("remarks", remarks, Types.VARCHAR);
                fLog.addValue("ip_address", ipAddress, Types.VARCHAR);
                fLog.addValue("app_serial", appSerial);
                fLog.addValue("family_serial", null);
                fLog.addValue("family_id", familyUuid, Types.OTHER);
                fLog.addValue("member_id", null, Types.OTHER);
                fLog.addValue("created_at", now, Types.TIMESTAMP);
                fLog.addValue("updated_at", now, Types.TIMESTAMP);
                logRows.add(fLog);

                // 5. Batch insert audit logs
                int[] batchResults = insertAcceptRejectLogs(writeJdbcTemplate, logRows);
                boolean isLogSaved = batchResults.length > 0;

                Map<String, Object> result = new HashMap<>();
                if (isSaved && isLogSaved) {
                    conn.commit();
                    result.put("success", true);
                    result.put("affected_members", affectedMembers.size());
                    result.put("app_serial", appSerial);
                } else {
                    conn.rollback();
                    result.put("success", false);
                }
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Find family by district code and family ID.
     */
    public Map<String, Object> findFamilyById(int districtCode, String familyId) {
        NamedParameterJdbcTemplate jdbcTemplate = getZoneDatabaseManager().getReadNamedParameterJdbcTemplate(districtCode);
        String sql = """
                SELECT id, tmp_application_id, app_serial, created_by_dist_code,
                       created_by_local_body_code, next_level_role_id, application_status,
                       lgd_district_code, create_mode
                FROM dbt_apy.families
                WHERE lgd_district_code = :distCode
                  AND created_by_dist_code = :distCode
                  AND id = CAST(:familyId AS uuid)
                  AND is_deleted = false
                LIMIT 1
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("distCode", districtCode);
        params.put("familyId", familyId);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public static UUID parseUuid(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof UUID u) {
            return u;
        }
        String str = val.toString().trim();
        if (str.length() == 36 && GlobalUpdateCheckHelper.isValidUuid(str)) {
            try {
                return UUID.fromString(str);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static Object normalizeUserId(Object userId) {
        if (userId == null) {
            return null;
        }
        if (userId instanceof Number) {
            return userId;
        }
        String str = userId.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException ignored) {
            return str;
        }
    }
}
