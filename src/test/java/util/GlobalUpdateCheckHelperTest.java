package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import service.ZoneDatabaseManager;

public class GlobalUpdateCheckHelperTest {

    private ZoneDatabaseManager mockZoneDatabaseManager;
    private NamedParameterJdbcTemplate mockJdbcTemplate;

    @BeforeEach
    void setUp() {
        mockZoneDatabaseManager = mock(ZoneDatabaseManager.class);
        mockJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        GlobalUpdateCheckHelper.setZoneDatabaseManager(mockZoneDatabaseManager);
    }

    @Test
    void testUuidValidation() {
        String validUuid = UUID.randomUUID().toString();
        assertTrue(GlobalUpdateCheckHelper.isValidUuid(validUuid));
        assertFalse(GlobalUpdateCheckHelper.isValidUuid(null));
        assertFalse(GlobalUpdateCheckHelper.isValidUuid(""));
        assertFalse(GlobalUpdateCheckHelper.isValidUuid("not-a-uuid"));
        assertFalse(GlobalUpdateCheckHelper.isValidUuid("12345"));
    }

    @Test
    void testDistrictCodeNormalization() {
        assertEquals(304, GlobalUpdateCheckHelper.normalizeDistrictCode(304));
        assertEquals(304, GlobalUpdateCheckHelper.normalizeDistrictCode("304"));
        assertNull(GlobalUpdateCheckHelper.normalizeDistrictCode(null));
        assertNull(GlobalUpdateCheckHelper.normalizeDistrictCode(""));
        assertNull(GlobalUpdateCheckHelper.normalizeDistrictCode("abc"));
    }

    @Test
    void testLocalBodyCodeNormalization() {
        assertEquals(12345L, GlobalUpdateCheckHelper.normalizeLocalBodyCode(12345L));
        assertEquals(12345L, GlobalUpdateCheckHelper.normalizeLocalBodyCode("12345"));
        assertEquals("PANCHAYAT_A", GlobalUpdateCheckHelper.normalizeLocalBodyCode("PANCHAYAT_A"));
        assertNull(GlobalUpdateCheckHelper.normalizeLocalBodyCode(null));
        assertNull(GlobalUpdateCheckHelper.normalizeLocalBodyCode(""));
    }

    @Test
    void testExtractRoleIds() {
        assertNull(GlobalUpdateCheckHelper.extractRoleIds(null));

        List<Object> single = GlobalUpdateCheckHelper.extractRoleIds(2);
        assertNotNull(single);
        assertEquals(1, single.size());
        assertEquals(2, single.get(0));

        List<Object> fromList = GlobalUpdateCheckHelper.extractRoleIds(Arrays.asList(1, 2, 3));
        assertEquals(3, fromList.size());
        assertEquals(1, fromList.get(0));

        List<Object> fromArray = GlobalUpdateCheckHelper.extractRoleIds(new int[]{4, 5});
        assertEquals(2, fromArray.size());
        assertEquals(4, fromArray.get(0));
    }

    @Test
    void testCheckingExistingStateFamily_InvalidInputs() {
        // null family_id
        assertFalse(GlobalUpdateCheckHelper.checkingExistingStateFamily(null, null, null, 1, 304, null));

        // invalid UUID family_id
        assertFalse(GlobalUpdateCheckHelper.checkingExistingStateFamily(null, "invalid-uuid", null, 1, 304, null));

        // null distCode
        String familyId = UUID.randomUUID().toString();
        assertFalse(GlobalUpdateCheckHelper.checkingExistingStateFamily(null, familyId, null, 1, null, null));

        // empty role array
        assertFalse(GlobalUpdateCheckHelper.checkingExistingStateFamily(null, familyId, null, Collections.emptyList(), 304, null));
    }

    @Test
    void testCheckingExistingStateFamily_ValidSingleRole() {
        String familyId = UUID.randomUUID().toString();
        int districtCode = 304;

        when(mockJdbcTemplate.queryForObject(anyString(), anyMap(), eq(Boolean.class))).thenReturn(true);

        boolean result = GlobalUpdateCheckHelper.checkingExistingStateFamily(
                mockJdbcTemplate, familyId, null, 2, districtCode, "1001"
        );

        assertTrue(result);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

        verify(mockJdbcTemplate).queryForObject(sqlCaptor.capture(), paramsCaptor.capture(), eq(Boolean.class));

        String sql = sqlCaptor.getValue();
        Map<String, Object> params = paramsCaptor.getValue();

        assertTrue(sql.contains("SELECT 1 FROM dbt_apy.families"));
        assertTrue(sql.contains("lgd_district_code = :distCode"));
        assertTrue(sql.contains("id = CAST(:family_id AS uuid)"));
        assertTrue(sql.contains("is_deleted = false"));
        assertTrue(sql.contains("created_by_local_body_code = :localBodyCode"));
        assertTrue(sql.contains("next_level_role_id = :roleId"));

        assertEquals(districtCode, params.get("distCode"));
        assertEquals(familyId, params.get("family_id"));
        assertEquals(1001L, params.get("localBodyCode"));
        assertEquals(2, params.get("roleId"));
    }

    @Test
    void testCheckingExistingStateFamily_MultipleRoles() {
        String familyId = UUID.randomUUID().toString();
        int districtCode = 304;

        when(mockJdbcTemplate.queryForObject(anyString(), anyMap(), eq(Boolean.class))).thenReturn(false);

        boolean result = GlobalUpdateCheckHelper.checkingExistingStateFamily(
                mockJdbcTemplate, familyId, null, Arrays.asList(2, 3), districtCode, null
        );

        assertFalse(result);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

        verify(mockJdbcTemplate).queryForObject(sqlCaptor.capture(), paramsCaptor.capture(), eq(Boolean.class));

        String sql = sqlCaptor.getValue();
        Map<String, Object> params = paramsCaptor.getValue();

        assertTrue(sql.contains("next_level_role_id IN (:roleIds)"));
        assertFalse(sql.contains("created_by_local_body_code"));

        @SuppressWarnings("unchecked")
        List<Object> roleIds = (List<Object>) params.get("roleIds");
        assertEquals(2, roleIds.size());
    }

    @Test
    void testCheckingExistingStateMembers_ValidWithOptionalFamilyId() {
        String familyId = UUID.randomUUID().toString();
        String memberId = UUID.randomUUID().toString();
        int districtCode = 306;

        when(mockJdbcTemplate.queryForObject(anyString(), anyMap(), eq(Boolean.class))).thenReturn(true);

        boolean result = GlobalUpdateCheckHelper.checkingExistingStateMembers(
                mockJdbcTemplate, familyId, memberId, 1, districtCode, null
        );

        assertTrue(result);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

        verify(mockJdbcTemplate).queryForObject(sqlCaptor.capture(), paramsCaptor.capture(), eq(Boolean.class));

        String sql = sqlCaptor.getValue();
        Map<String, Object> params = paramsCaptor.getValue();

        assertTrue(sql.contains("SELECT 1 FROM dbt_apy.family_members"));
        assertTrue(sql.contains("id = CAST(:member_id AS uuid)"));
        assertTrue(sql.contains("family_id = CAST(:family_id AS uuid)"));
        assertTrue(sql.contains("lgd_district_code = :distCode"));
        assertTrue(sql.contains("next_level_role_id = :roleId"));

        assertEquals(memberId, params.get("member_id"));
        assertEquals(familyId, params.get("family_id"));
        assertEquals(districtCode, params.get("distCode"));
    }

    @Test
    void testCheckingExistingStateMembers_WithoutFamilyId() {
        String memberId = UUID.randomUUID().toString();
        int districtCode = 306;

        when(mockJdbcTemplate.queryForObject(anyString(), anyMap(), eq(Boolean.class))).thenReturn(true);

        // family_id is null / omitted
        boolean result = GlobalUpdateCheckHelper.checkingExistingStateMembers(
                mockJdbcTemplate, null, memberId, null, districtCode, null
        );

        assertTrue(result);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockJdbcTemplate).queryForObject(sqlCaptor.capture(), anyMap(), eq(Boolean.class));

        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("id = CAST(:member_id AS uuid)"));
        assertFalse(sql.contains("family_id = CAST(:family_id AS uuid)"));
        assertFalse(sql.contains("next_level_role_id"));
    }

    @Test
    void testResolveNamedParameterJdbcTemplate_WithConnectionName() {
        when(mockZoneDatabaseManager.getNamedParameterJdbcTemplateByConnectionName("zone2_read")).thenReturn(mockJdbcTemplate);

        NamedParameterJdbcTemplate resolved = GlobalUpdateCheckHelper.resolveNamedParameterJdbcTemplate("zone2_read", 304);
        assertNotNull(resolved);
        assertEquals(mockJdbcTemplate, resolved);
        verify(mockZoneDatabaseManager).getNamedParameterJdbcTemplateByConnectionName("zone2_read");
    }

    @Test
    void testResolveNamedParameterJdbcTemplate_WithNullConn_UsesDistrictCode() {
        when(mockZoneDatabaseManager.getReadNamedParameterJdbcTemplate(304)).thenReturn(mockJdbcTemplate);

        NamedParameterJdbcTemplate resolved = GlobalUpdateCheckHelper.resolveNamedParameterJdbcTemplate(null, 304);
        assertNotNull(resolved);
        assertEquals(mockJdbcTemplate, resolved);
        verify(mockZoneDatabaseManager).getReadNamedParameterJdbcTemplate(304);
    }
}
