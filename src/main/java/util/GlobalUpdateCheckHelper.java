package util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;

import service.ZoneDatabaseManager;

/**
 * Port of Laravel's {@code App\Helpers\GlobalUpdateCheckHelper}.
 * Checks existing next_level_role_id status for families and family members.
 *
 * <p>Supports both static calls mirroring Laravel's Facade/Helper usage:
 * {@code GlobalUpdateCheckHelper.checkingExistingStateFamily(...)}
 * {@code GlobalUpdateCheckHelper.checkingExistingStateMembers(...)}
 * and Spring-managed dependency injection.
 */
@Component
public class GlobalUpdateCheckHelper {

    private static final Logger log = LoggerFactory.getLogger(GlobalUpdateCheckHelper.class);

    private static ZoneDatabaseManager zoneDatabaseManager;

    public GlobalUpdateCheckHelper(ZoneDatabaseManager zoneDatabaseManager) {
        GlobalUpdateCheckHelper.zoneDatabaseManager = zoneDatabaseManager;
    }

    public static void setZoneDatabaseManager(ZoneDatabaseManager manager) {
        GlobalUpdateCheckHelper.zoneDatabaseManager = manager;
    }

    public static ZoneDatabaseManager getZoneDatabaseManager() {
        if (zoneDatabaseManager != null) {
            return zoneDatabaseManager;
        }
        try {
            return ZoneDB.getManager();
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Resolves the appropriate {@link NamedParameterJdbcTemplate} from a connection object,
     * connection name, or district code.
     *
     * @param conn Connection specification (NamedParameterJdbcTemplate, JdbcTemplate, DataSource, Connection, String, or null)
     * @param distCode District code used to route to zone database when conn is null
     * @return Resolved {@link NamedParameterJdbcTemplate}
     */
    public static NamedParameterJdbcTemplate resolveNamedParameterJdbcTemplate(Object conn, Object distCode) {
        if (conn instanceof NamedParameterJdbcTemplate namedTemplate) {
            return namedTemplate;
        }
        if (conn instanceof JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }
        if (conn instanceof DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }
        if (conn instanceof Connection connection) {
            return new NamedParameterJdbcTemplate(new SingleConnectionDataSource(connection, false));
        }

        ZoneDatabaseManager manager = getZoneDatabaseManager();
        if (manager == null) {
            throw new IllegalStateException("ZoneDatabaseManager is not initialized and no valid connection/JdbcTemplate was provided.");
        }

        if (conn instanceof String connName && !connName.trim().isEmpty()) {
            return manager.getNamedParameterJdbcTemplateByConnectionName(connName);
        }

        Integer districtId = normalizeDistrictCode(distCode);
        return manager.getReadNamedParameterJdbcTemplate(districtId);
    }

    /**
     * Check existing state for a family record in {@code dbt_apy.families}.
     *
     * @param conn Connection name or template (nullable)
     * @param family_id UUID of the family
     * @param member_id Retained for signature parity with PHP helper (not used in family table query)
     * @param roleId Target next_level_role_id (Integer, Collection, Array, or null)
     * @param distCode LGD district code
     * @param localBodyCode Created-by local body code (optional)
     * @return true if matching family record exists; false otherwise
     */
    public static boolean checkingExistingStateFamily(
            Object conn,
            Object family_id,
            Object member_id,
            Object roleId,
            Object distCode,
            Object localBodyCode) {

        if (family_id == null) {
            return false;
        }
        String familyIdStr = family_id.toString().trim();
        if (familyIdStr.isEmpty() || !isValidUuid(familyIdStr)) {
            return false;
        }

        Integer districtCode = normalizeDistrictCode(distCode);
        if (districtCode == null) {
            return false;
        }

        List<Object> roleIds = null;
        if (roleId != null) {
            roleIds = extractRoleIds(roleId);
            if (roleIds.isEmpty()) {
                return false;
            }
        }

        NamedParameterJdbcTemplate jdbcTemplate = resolveNamedParameterJdbcTemplate(conn, districtCode);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT EXISTS (");
        sql.append("SELECT 1 FROM dbt_apy.families ");
        sql.append("WHERE lgd_district_code = :distCode ");
        sql.append("AND id = CAST(:family_id AS uuid) ");
        sql.append("AND is_deleted = false");

        Map<String, Object> params = new HashMap<>();
        params.put("distCode", districtCode);
        params.put("family_id", familyIdStr);

        Object normalizedLbc = normalizeLocalBodyCode(localBodyCode);
        if (normalizedLbc != null) {
            sql.append(" AND created_by_local_body_code = :localBodyCode");
            params.put("localBodyCode", normalizedLbc);
        }

        if (roleIds != null) {
            if (roleIds.size() == 1) {
                sql.append(" AND next_level_role_id = :roleId");
                params.put("roleId", roleIds.get(0));
            } else {
                sql.append(" AND next_level_role_id IN (:roleIds)");
                params.put("roleIds", roleIds);
            }
        }

        sql.append(")");

        try {
            Boolean exists = jdbcTemplate.queryForObject(sql.toString(), params, Boolean.class);
            return Boolean.TRUE.equals(exists);
        } catch (DataAccessException e) {
            log.error("Database error in checkingExistingStateFamily [family_id={}, distCode={}]: {}",
                    familyIdStr, districtCode, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Check existing state for a member record in {@code dbt_apy.family_members}.
     *
     * @param conn Connection name or template (nullable)
     * @param family_id Family UUID (optional)
     * @param member_id Member UUID (required)
     * @param roleId Target next_level_role_id (Integer, Collection, Array, or null)
     * @param distCode LGD district code
     * @param localBodyCode Created-by local body code (optional)
     * @return true if matching family member record exists; false otherwise
     */
    public static boolean checkingExistingStateMembers(
            Object conn,
            Object family_id,
            Object member_id,
            Object roleId,
            Object distCode,
            Object localBodyCode) {

        if (member_id == null) {
            return false;
        }
        String memberIdStr = member_id.toString().trim();
        if (memberIdStr.isEmpty() || !isValidUuid(memberIdStr)) {
            return false;
        }

        String familyIdStr = null;
        if (family_id != null) {
            String temp = family_id.toString().trim();
            if (!temp.isEmpty()) {
                if (!isValidUuid(temp)) {
                    return false;
                }
                familyIdStr = temp;
            }
        }

        Integer districtCode = normalizeDistrictCode(distCode);
        if (districtCode == null) {
            return false;
        }

        List<Object> roleIds = null;
        if (roleId != null) {
            roleIds = extractRoleIds(roleId);
            if (roleIds.isEmpty()) {
                return false;
            }
        }

        NamedParameterJdbcTemplate jdbcTemplate = resolveNamedParameterJdbcTemplate(conn, districtCode);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT EXISTS (");
        sql.append("SELECT 1 FROM dbt_apy.family_members ");
        sql.append("WHERE lgd_district_code = :distCode ");
        sql.append("AND id = CAST(:member_id AS uuid) ");
        sql.append("AND is_deleted = false");

        Map<String, Object> params = new HashMap<>();
        params.put("distCode", districtCode);
        params.put("member_id", memberIdStr);

        if (familyIdStr != null) {
            sql.append(" AND family_id = CAST(:family_id AS uuid)");
            params.put("family_id", familyIdStr);
        }

        Object normalizedLbc = normalizeLocalBodyCode(localBodyCode);
        if (normalizedLbc != null) {
            sql.append(" AND created_by_local_body_code = :localBodyCode");
            params.put("localBodyCode", normalizedLbc);
        }

        if (roleIds != null) {
            if (roleIds.size() == 1) {
                sql.append(" AND next_level_role_id = :roleId");
                params.put("roleId", roleIds.get(0));
            } else {
                sql.append(" AND next_level_role_id IN (:roleIds)");
                params.put("roleIds", roleIds);
            }
        }

        sql.append(")");

        try {
            Boolean exists = jdbcTemplate.queryForObject(sql.toString(), params, Boolean.class);
            return Boolean.TRUE.equals(exists);
        } catch (DataAccessException e) {
            log.error("Database error in checkingExistingStateMembers [member_id={}, family_id={}, distCode={}]: {}",
                    memberIdStr, familyIdStr, districtCode, e.getMessage(), e);
            throw e;
        }
    }

    // ── Convenience Overloads ────────────────────────────────────────────

    public static boolean checkingExistingStateFamily(Object family_id, Object roleId, Object distCode) {
        return checkingExistingStateFamily(null, family_id, null, roleId, distCode, null);
    }

    public static boolean checkingExistingStateFamily(Object family_id, Object member_id, Object roleId, Object distCode) {
        return checkingExistingStateFamily(null, family_id, member_id, roleId, distCode, null);
    }

    public static boolean checkingExistingStateFamily(Object family_id, Object member_id, Object roleId, Object distCode, Object localBodyCode) {
        return checkingExistingStateFamily(null, family_id, member_id, roleId, distCode, localBodyCode);
    }

    public static boolean checkingExistingStateMembers(Object family_id, Object member_id, Object roleId, Object distCode) {
        return checkingExistingStateMembers(null, family_id, member_id, roleId, distCode, null);
    }

    public static boolean checkingExistingStateMembers(Object family_id, Object member_id, Object roleId, Object distCode, Object localBodyCode) {
        return checkingExistingStateMembers(null, family_id, member_id, roleId, distCode, localBodyCode);
    }

    // ── Instance Methods for DI ──────────────────────────────────────────

    public boolean checkExistingStateFamily(
            Object conn,
            Object family_id,
            Object member_id,
            Object roleId,
            Object distCode,
            Object localBodyCode) {
        return checkingExistingStateFamily(conn, family_id, member_id, roleId, distCode, localBodyCode);
    }

    public boolean checkExistingStateMembers(
            Object conn,
            Object family_id,
            Object member_id,
            Object roleId,
            Object distCode,
            Object localBodyCode) {
        return checkingExistingStateMembers(conn, family_id, member_id, roleId, distCode, localBodyCode);
    }

    // ── Helper Utility Methods ───────────────────────────────────────────

    public static Integer normalizeDistrictCode(Object distCode) {
        if (distCode == null) {
            return null;
        }
        if (distCode instanceof Number number) {
            return number.intValue();
        }
        String str = distCode.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Object normalizeLocalBodyCode(Object localBodyCode) {
        if (localBodyCode == null) {
            return null;
        }
        if (localBodyCode instanceof Number) {
            return localBodyCode;
        }
        String str = localBodyCode.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        try {
            if (str.matches("^-?\\d+$")) {
                return Long.valueOf(str);
            }
        } catch (NumberFormatException ignored) {
        }
        return str;
    }

    public static List<Object> extractRoleIds(Object roleId) {
        if (roleId == null) {
            return null;
        }
        List<Object> list = new ArrayList<>();
        if (roleId instanceof Collection<?> col) {
            for (Object item : col) {
                if (item != null) {
                    list.add(normalizeRoleId(item));
                }
            }
            return list;
        }
        if (roleId instanceof Object[] arr) {
            for (Object item : arr) {
                if (item != null) {
                    list.add(normalizeRoleId(item));
                }
            }
            return list;
        }
        if (roleId instanceof int[] arr) {
            for (int item : arr) {
                list.add(item);
            }
            return list;
        }
        if (roleId instanceof long[] arr) {
            for (long item : arr) {
                list.add(item);
            }
            return list;
        }
        if (roleId instanceof Iterable<?> iter) {
            for (Object item : iter) {
                if (item != null) {
                    list.add(normalizeRoleId(item));
                }
            }
            return list;
        }
        list.add(normalizeRoleId(roleId));
        return list;
    }

    public static Object normalizeRoleId(Object roleId) {
        if (roleId == null) {
            return null;
        }
        if (roleId instanceof Number) {
            return roleId;
        }
        String str = roleId.toString().trim();
        try {
            if (str.matches("^-?\\d+$")) {
                return Long.valueOf(str);
            }
        } catch (NumberFormatException ignored) {
        }
        return str;
    }

    public static boolean isValidUuid(String str) {
        if (str == null) {
            return false;
        }
        String clean = str.trim();
        if (clean.length() != 36) {
            return false;
        }
        try {
            UUID.fromString(clean);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}
