package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import service.ZoneDatabaseManager;

public class HelperTest {

    private ZoneDatabaseManager mockZoneDatabaseManager;
    private NamedParameterJdbcTemplate mockJdbcTemplate;

    @BeforeEach
    void setUp() {
        mockZoneDatabaseManager = mock(ZoneDatabaseManager.class);
        mockJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        GlobalUpdateCheckHelper.setZoneDatabaseManager(mockZoneDatabaseManager);
        when(mockZoneDatabaseManager.getReadNamedParameterJdbcTemplate(304)).thenReturn(mockJdbcTemplate);
    }

    @Test
    void testSnapshotFamilyDataValidation() {
        // Invalid family ID
        assertThrows(ResponseStatusException.class, () -> {
            Helper.snapshotFamilyData("invalid-uuid", 304);
        });

        // Null family ID
        assertThrows(ResponseStatusException.class, () -> {
            Helper.snapshotFamilyData(null, 304);
        });

        // Invalid member ID
        String familyId = UUID.randomUUID().toString();
        assertThrows(ResponseStatusException.class, () -> {
            Helper.snapshotFamilyData(familyId, "invalid-member-uuid", 304);
        });
    }

    @Test
    void testSnapshotFamilyDataSuccess() {
        String familyId = UUID.randomUUID().toString();
        String memberId = UUID.randomUUID().toString();

        Map<String, Object> familyRow = new HashMap<>();
        familyRow.put("id", familyId);
        familyRow.put("lgd_district_code", 304);
        familyRow.put("app_serial", "APP-001");

        Map<String, Object> memberRow = new HashMap<>();
        memberRow.put("id", memberId);
        memberRow.put("family_id", familyId);
        memberRow.put("next_level_role_id", 2);

        when(mockJdbcTemplate.queryForList(contains("dbt_apy.families"), anyMap()))
                .thenReturn(List.of(familyRow));
        when(mockJdbcTemplate.queryForList(contains("dbt_apy.family_members"), anyMap()))
                .thenReturn(List.of(memberRow));

        Map<String, Object> snapshot = Helper.snapshotFamilyData(familyId, memberId, 304);

        assertNotNull(snapshot);
        assertEquals(familyRow, snapshot.get("family"));
        assertEquals(memberRow, snapshot.get("member"));
    }

    @Test
    void testSnapshotFamilyOnly() {
        String familyId = UUID.randomUUID().toString();

        Map<String, Object> familyRow = new HashMap<>();
        familyRow.put("id", familyId);
        familyRow.put("lgd_district_code", 304);

        when(mockJdbcTemplate.queryForList(contains("dbt_apy.families"), anyMap()))
                .thenReturn(List.of(familyRow));

        Map<String, Object> snapshot = Helper.snapshotFamilyData(familyId, 304);

        assertNotNull(snapshot);
        assertEquals(familyRow, snapshot.get("family"));
        assertEquals(Collections.emptyMap(), snapshot.get("member"));
    }
}
