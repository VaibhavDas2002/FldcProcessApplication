package repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import service.ZoneDatabaseManager;

@Repository
public class familymemberRepository {

    private final ZoneDatabaseManager zoneDatabaseManager;

    public familymemberRepository(ZoneDatabaseManager zoneDatabaseManager) {
        this.zoneDatabaseManager = zoneDatabaseManager;
    }

    public List<Map<String, Object>> findByDistrictCode(int districtCode, String family_id, String member_id) {
        NamedParameterJdbcTemplate jdbcTemplate = zoneDatabaseManager.getReadNamedParameterJdbcTemplate(districtCode);

        String sql = "SELECT next_level_role_id, approval_datetime, verification_datetime FROM dbt_apy.family_members WHERE lgd_district_code = :dis and family_id = CAST(:family_id AS uuid) and id = CAST(:member_id AS uuid)";
        Map<String, Object> params = new HashMap<>();
        params.put("dis", districtCode);
        params.put("family_id", family_id);
        params.put("member_id", member_id);

        return jdbcTemplate.queryForList(sql, params);
    }

    public List<Map<String, Object>> findByFamilyId(int districtCode, String family_id) {
        NamedParameterJdbcTemplate jdbcTemplate = zoneDatabaseManager.getReadNamedParameterJdbcTemplate(districtCode);

        String sql = "SELECT id, next_level_role_id, approval_datetime, verification_datetime FROM dbt_apy.family_members WHERE lgd_district_code = :dis and family_id = CAST(:family_id AS uuid)";
        Map<String, Object> params = new HashMap<>();
        params.put("dis", districtCode);
        params.put("family_id", family_id);

        return jdbcTemplate.queryForList(sql, params);
    }

    public List<Map<String, Object>> findCurrentStatusByDistrictCode(int districtCode, String family_id,
            String member_id) {
        return findByDistrictCode(districtCode, family_id, member_id);
    }
}
