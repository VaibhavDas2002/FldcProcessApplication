package processWorkflow;

import static responseHandler.ResponseHandler.error;
import static responseHandler.ResponseHandler.success;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import resDTO.GetFamilyInput;
import service.StatusFLDC;
import service.ZoneDatabaseManager;
import util.GlobalUpdateCheckHelper;
import util.Helper;
import util.ZoneDB;

@RestController
@RequestMapping("/api/v2/workflow")
public class ProcessApplication {

    private final StatusFLDC statusFLDC;
    private final ZoneDatabaseManager zoneDatabaseManager;

    public ProcessApplication(StatusFLDC statusFLDC) {
        this(statusFLDC, null);
    }

    public ProcessApplication(StatusFLDC statusFLDC, ZoneDatabaseManager zoneDatabaseManager) {
        this.statusFLDC = statusFLDC;
        this.zoneDatabaseManager = zoneDatabaseManager;
    }

    private ZoneDatabaseManager getZoneDatabaseManager() {
        if (this.zoneDatabaseManager != null) {
            return this.zoneDatabaseManager;
        }
        return ZoneDB.getManager();
    }

    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.ok(success("Family members retrieved successfully"));
    }

    @PostMapping("/status")
    public ResponseEntity<List<Map<String, Object>>> getStatus(
            @RequestParam("districtCode") int districtCode,
            @RequestParam("family_id") String familyId,
            @RequestParam("member_id") String memberId) {
        return ResponseEntity.ok(statusFLDC.getStatusByDistrict(districtCode, familyId, memberId));
    }

    @PostMapping("/acceptFamily")
    public ResponseEntity<?> acceptFamily(
            @RequestBody GetFamilyInput data,
            HttpServletRequest request) {
        if (data == null) {
            return ResponseEntity.badRequest().body(error("Request body is required", HttpStatus.BAD_REQUEST));
        }

        String familyId = data.getFamily_id();
        if (familyId == null || familyId.trim().isEmpty() || !GlobalUpdateCheckHelper.isValidUuid(familyId)) {
            return ResponseEntity.badRequest().body(error("Invalid Family ID", HttpStatus.BAD_REQUEST));
        }
        familyId = familyId.trim();

        String remarks = data.getRemarks();
        if (remarks != null && remarks.length() > 1000) {
            return ResponseEntity.badRequest().body(error("Remarks cannot exceed 1000 characters", HttpStatus.BAD_REQUEST));
        }

        ZoneDatabaseManager dbManager = getZoneDatabaseManager();
        Integer distCode = null;
        if (data.getLgd_district_code() > 0) {
            distCode = data.getLgd_district_code();
        } else if (dbManager != null) {
            distCode = dbManager.getDistrictId();
        }
        if (distCode == null || distCode <= 0) {
            return ResponseEntity.badRequest().body(error("Invalid or missing lgd_district_code", HttpStatus.BAD_REQUEST));
        }

        try {
            // Snapshot family data
            Map<String, Object> familyData = Helper.snapshotFamilyData(familyId, distCode);
            @SuppressWarnings("unchecked")
            Map<String, Object> family = (familyData != null) ? (Map<String, Object>) familyData.get("family") : null;
            if (family == null || family.isEmpty()) {
                return ResponseEntity.badRequest().body(error("Application not found.", HttpStatus.BAD_REQUEST));
            }

            Object localBodyCode = data.getLocal_body_code();
            if (localBodyCode == null) {
                localBodyCode = family.get("created_by_local_body_code");
            }

            Object userId = data.getUser_id();
            String ipAddress = data.getIp_address();
            if (ipAddress == null || ipAddress.isBlank()) {
                if (request != null) {
                    String forwarded = request.getHeader("X-Forwarded-For");
                    ipAddress = (forwarded != null && !forwarded.isBlank())
                            ? forwarded.split(",")[0].trim()
                            : request.getRemoteAddr();
                }
            }

            // Check existing state validity
            boolean isStateValid = GlobalUpdateCheckHelper.checkingExistingStateFamily(
                null,
                familyId,
                null,
                List.of(13, 15),
                distCode,
                localBodyCode
            );

            if (!isStateValid) {
                return ResponseEntity.badRequest().body(error("Application is not available for verification", HttpStatus.BAD_REQUEST));
            }

            // Begin DB transaction on connWrite
            DataSource writeDataSource = dbManager.getWriteDataSource(distCode);
            try (Connection conn = writeDataSource.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    NamedParameterJdbcTemplate writeJdbcTemplate = new NamedParameterJdbcTemplate(new SingleConnectionDataSource(conn, true));

                    // Update dbt_apy.families
                    StringBuilder familyQuery = new StringBuilder("""
                        UPDATE dbt_apy.families
                        SET application_status = 'ACCEPTED',
                            next_level_role_id = 18
                        WHERE lgd_district_code = :distCode
                          AND created_by_dist_code = :distCode
                          AND id = CAST(:family_id AS uuid)
                          AND is_deleted = false
                    """);

                    MapSqlParameterSource familyParams = new MapSqlParameterSource();
                    familyParams.addValue("distCode", distCode);
                    familyParams.addValue("family_id", familyId);

                    if (localBodyCode != null) {
                        familyQuery.append(" AND created_by_local_body_code = :localBodyCode");
                        familyParams.addValue("localBodyCode", GlobalUpdateCheckHelper.normalizeLocalBodyCode(localBodyCode));
                    }

                    int familyUpdated = writeJdbcTemplate.update(familyQuery.toString(), familyParams);
                    boolean is_saved = familyUpdated > 0;

                    // Update dbt_apy.family_members
                    StringBuilder memberQuery = new StringBuilder("""
                        UPDATE dbt_apy.family_members
                        SET application_status = 'ACCEPTED',
                            next_level_role_id = 18
                        WHERE lgd_district_code = :distCode
                          AND created_by_dist_code = :distCode
                          AND family_id = CAST(:family_id AS uuid)
                          AND next_level_role_id <> 30
                          AND is_deleted = false
                    """);

                    MapSqlParameterSource memberParams = new MapSqlParameterSource();
                    memberParams.addValue("distCode", distCode);
                    memberParams.addValue("family_id", familyId);

                    if (localBodyCode != null) {
                        memberQuery.append(" AND created_by_local_body_code = :localBodyCode");
                        memberParams.addValue("localBodyCode", GlobalUpdateCheckHelper.normalizeLocalBodyCode(localBodyCode));
                    }

                    writeJdbcTemplate.update(memberQuery.toString(), memberParams);

                    // Select affected members for logging
                    StringBuilder affectedMembersQuery = new StringBuilder("""
                        SELECT id, family_serial, lgd_district_code
                        FROM dbt_apy.family_members
                        WHERE lgd_district_code = :distCode
                          AND created_by_dist_code = :distCode
                          AND family_id = CAST(:family_id AS uuid)
                          AND next_level_role_id <> 30
                          AND is_deleted = false
                    """);

                    if (localBodyCode != null) {
                        affectedMembersQuery.append(" AND created_by_local_body_code = :localBodyCode");
                    }

                    List<Map<String, Object>> affectedMembers = writeJdbcTemplate.queryForList(affectedMembersQuery.toString(), memberParams);

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
                        mLog.addValue("lgd_district_code", GlobalUpdateCheckHelper.normalizeDistrictCode(memberDist), Types.INTEGER);
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
                    fLog.addValue("lgd_district_code", GlobalUpdateCheckHelper.normalizeDistrictCode(family.get("lgd_district_code")), Types.INTEGER);
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

                    int[] batchResults = writeJdbcTemplate.batchUpdate(insertLogSql, logRows.toArray(new SqlParameterSource[0]));
                    boolean isLogSaved = batchResults.length > 0;

                    if (is_saved && isLogSaved) {
                        conn.commit();
                        String serialOrId = (appSerial != null) ? appSerial.toString() : familyId;
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put("family_id", familyId);
                        dataMap.put("app_serial", appSerial);
                        dataMap.put("affected_members", affectedMembers.size());
                        return ResponseEntity.ok(success("Family Application Accepted. Family ID: " + serialOrId, dataMap));
                    } else {
                        conn.rollback();
                        return ResponseEntity.badRequest().body(error("Failed to accept family application.", HttpStatus.BAD_REQUEST));
                    }
                } catch (Exception e) {
                    conn.rollback();
                    throw e;
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(error("Failed to accept family application: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    public ResponseEntity<?> acceptFamily(GetFamilyInput data) {
        return acceptFamily(data, null);
    }

    private static UUID parseUuid(Object val) {
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

    private static Object normalizeUserId(Object userId) {
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
