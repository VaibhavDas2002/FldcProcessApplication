package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * General helper utility class ported from Laravel helpers.
 */
public class Helper {

    private Helper() {
        // Utility class, prevent direct instantiation
    }

    /**
     * Snapshots family and optional member data from PostgreSQL database.
     * Equivalent to Laravel's {@code snapshotFamilyData}.
     *
     * @param familyId        UUID string of the family (required)
     * @param memberId        UUID string of the member (optional, nullable)
     * @param conn            Optional connection name or template (pass null to route automatically by district)
     * @param lgdDistrictCode District code used for routing and filtering
     * @return Map with "family" and "member" snapshots
     */
    public static Map<String, Object> snapshotFamilyData(
            String familyId,
            String memberId,
            String conn,
            Integer lgdDistrictCode) {

        if (familyId == null || !isValidUuid(familyId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Family ID");
        }

        if (memberId != null && !memberId.trim().isEmpty() && !isValidUuid(memberId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Member ID");
        }

        NamedParameterJdbcTemplate jdbcTemplate = GlobalUpdateCheckHelper.resolveNamedParameterJdbcTemplate(conn, lgdDistrictCode);

        String familyQuery = """
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

        Map<String, Object> familyParams = new HashMap<>();
        familyParams.put("distCode", lgdDistrictCode);
        familyParams.put("familyId", familyId.trim());

        List<Map<String, Object>> familyList = jdbcTemplate.queryForList(familyQuery, familyParams);
        Map<String, Object> family = familyList.isEmpty() ? new HashMap<>() : familyList.get(0);

        Integer effectiveDistCode = lgdDistrictCode;
        if (family.get("lgd_district_code") instanceof Number num) {
            effectiveDistCode = num.intValue();
        }

        Map<String, Object> member = new HashMap<>();
        if (memberId != null && !memberId.trim().isEmpty()) {
            String memberQuery = """
                    SELECT id, family_id, family_serial, next_level_role_id,
                           application_status, created_by_dist_code, lgd_district_code,
                           lgd_block_mc_code, lgd_gp_ward_code, create_mode
                    FROM dbt_apy.family_members
                    WHERE lgd_district_code = :distCode
                      AND created_by_dist_code = :distCode
                      AND id = CAST(:memberId AS uuid)
                      AND is_deleted = false
                    LIMIT 1
                    """;

            Map<String, Object> memberParams = new HashMap<>();
            memberParams.put("distCode", effectiveDistCode);
            memberParams.put("memberId", memberId.trim());

            List<Map<String, Object>> memberList = jdbcTemplate.queryForList(memberQuery, memberParams);
            if (!memberList.isEmpty()) {
                member = memberList.get(0);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("family", family);
        response.put("member", member);
        return response;
    }

    /**
     * Overload of {@code snapshotFamilyData} without explicit connection parameter.
     */
    public static Map<String, Object> snapshotFamilyData(String familyId, String memberId, Integer lgdDistrictCode) {
        return snapshotFamilyData(familyId, memberId, null, lgdDistrictCode);
    }

    /**
     * Overload of {@code snapshotFamilyData} for family only.
     */
    public static Map<String, Object> snapshotFamilyData(String familyId, Integer lgdDistrictCode) {
        return snapshotFamilyData(familyId, null, null, lgdDistrictCode);
    }

    // ── GlobalUpdateCheckHelper Delegations ───────────────────────────────

    public static boolean checkingExistingStateFamily(
            Object conn,
            Object familyId,
            Object memberId,
            Object roleId,
            Object distCode,
            Object localBodyCode) {
        return GlobalUpdateCheckHelper.checkingExistingStateFamily(conn, familyId, memberId, roleId, distCode, localBodyCode);
    }

    public static boolean checkingExistingStateFamily(Object familyId, Object roleId, Object distCode) {
        return GlobalUpdateCheckHelper.checkingExistingStateFamily(familyId, roleId, distCode);
    }

    public static boolean checkingExistingStateMembers(
            Object conn,
            Object familyId,
            Object memberId,
            Object roleId,
            Object distCode,
            Object localBodyCode) {
        return GlobalUpdateCheckHelper.checkingExistingStateMembers(conn, familyId, memberId, roleId, distCode, localBodyCode);
    }

    public static boolean checkingExistingStateMembers(Object familyId, Object memberId, Object roleId, Object distCode) {
        return GlobalUpdateCheckHelper.checkingExistingStateMembers(familyId, memberId, roleId, distCode);
    }

    private static boolean isValidUuid(String val) {
        if (val == null || val.trim().length() != 36) {
            return false;
        }
        try {
            UUID.fromString(val.trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
