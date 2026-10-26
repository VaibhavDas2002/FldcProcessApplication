package processWorkflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import jakarta.servlet.http.HttpServletRequest;
import resDTO.GetFamilyInput;
import service.StatusFLDC;
import service.ZoneDatabaseManager;
import util.GlobalUpdateCheckHelper;

public class ProcessApplicationTest {

    private StatusFLDC mockStatusFLDC;
    private ZoneDatabaseManager mockZoneDatabaseManager;
    private NamedParameterJdbcTemplate mockReadJdbcTemplate;
    private DataSource mockWriteDataSource;
    private Connection mockConnection;
    private HttpServletRequest mockRequest;

    private ProcessApplication controller;

    @BeforeEach
    void setUp() throws Exception {
        mockStatusFLDC = mock(StatusFLDC.class);
        mockZoneDatabaseManager = mock(ZoneDatabaseManager.class);
        mockReadJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        mockWriteDataSource = mock(DataSource.class);
        mockConnection = mock(Connection.class);
        mockRequest = mock(HttpServletRequest.class);

        DatabaseMetaData mockMetaData = mock(DatabaseMetaData.class);
        when(mockMetaData.getDriverName()).thenReturn("PostgreSQL JDBC Driver");
        when(mockMetaData.supportsBatchUpdates()).thenReturn(true);
        when(mockConnection.getMetaData()).thenReturn(mockMetaData);
        when(mockWriteDataSource.getConnection()).thenReturn(mockConnection);

        GlobalUpdateCheckHelper.setZoneDatabaseManager(mockZoneDatabaseManager);
        when(mockZoneDatabaseManager.getReadNamedParameterJdbcTemplate(304)).thenReturn(mockReadJdbcTemplate);
        when(mockZoneDatabaseManager.getWriteDataSource(304)).thenReturn(mockWriteDataSource);

        controller = new ProcessApplication(mockStatusFLDC, mockZoneDatabaseManager);
    }

    @Test
    void testGetTest() {
        ResponseEntity<?> response = controller.getTest();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(true, body.get("status"));
        assertEquals("Family members retrieved successfully", body.get("message"));
    }

    @Test
    void testAcceptFamily_NullBody() {
        ResponseEntity<?> response = controller.acceptFamily(null, mockRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(false, body.get("status"));
        assertTrue(body.get("message").toString().contains("Request body is required"));
    }

    @Test
    void testAcceptFamily_InvalidFamilyId() {
        GetFamilyInput input = new GetFamilyInput();
        input.setFamily_id("invalid-uuid");
        input.setLgd_district_code(304);

        ResponseEntity<?> response = controller.acceptFamily(input, mockRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(false, body.get("status"));
        assertTrue(body.get("message").toString().contains("Invalid Family ID"));
    }

    @Test
    void testAcceptFamily_RemarksTooLong() {
        GetFamilyInput input = new GetFamilyInput();
        input.setFamily_id(UUID.randomUUID().toString());
        input.setLgd_district_code(304);
        input.setRemarks("a".repeat(1001));

        ResponseEntity<?> response = controller.acceptFamily(input, mockRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(false, body.get("status"));
        assertTrue(body.get("message").toString().contains("Remarks cannot exceed 1000 characters"));
    }

    @Test
    void testAcceptFamily_MissingDistrictCode() {
        GetFamilyInput input = new GetFamilyInput();
        input.setFamily_id(UUID.randomUUID().toString());
        input.setLgd_district_code(0);
        when(mockZoneDatabaseManager.getDistrictId()).thenReturn(null);

        ResponseEntity<?> response = controller.acceptFamily(input, mockRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(false, body.get("status"));
        assertTrue(body.get("message").toString().contains("Invalid or missing lgd_district_code"));
    }

    @Test
    void testAcceptFamily_ApplicationNotFound() {
        String familyId = UUID.randomUUID().toString();
        GetFamilyInput input = new GetFamilyInput();
        input.setFamily_id(familyId);
        input.setLgd_district_code(304);

        when(mockReadJdbcTemplate.queryForList(contains("dbt_apy.families"), anyMap()))
                .thenReturn(Collections.emptyList());

        ResponseEntity<?> response = controller.acceptFamily(input, mockRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(false, body.get("status"));
        assertEquals("Application not found.", body.get("message"));
    }

    @Test
    void testAcceptFamily_StateNotValid() {
        String familyId = UUID.randomUUID().toString();
        GetFamilyInput input = new GetFamilyInput();
        input.setFamily_id(familyId);
        input.setLgd_district_code(304);

        Map<String, Object> familyRow = new HashMap<>();
        familyRow.put("id", familyId);
        familyRow.put("lgd_district_code", 304);
        familyRow.put("created_by_local_body_code", 1001L);
        familyRow.put("app_serial", "APP-001");

        when(mockReadJdbcTemplate.queryForList(contains("dbt_apy.families"), anyMap()))
                .thenReturn(List.of(familyRow));

        when(mockReadJdbcTemplate.queryForObject(contains("EXISTS"), anyMap(), eq(Boolean.class)))
                .thenReturn(false);

        ResponseEntity<?> response = controller.acceptFamily(input, mockRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(false, body.get("status"));
        assertEquals("Application is not available for verification", body.get("message"));
    }

    @Test
    void testAcceptFamily_SingleArgOverload() {
        ResponseEntity<?> response = controller.acceptFamily(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAcceptFamily_Success() throws Exception {
        String familyId = UUID.randomUUID().toString();
        String memberId = UUID.randomUUID().toString();
        GetFamilyInput input = new GetFamilyInput();
        input.setFamily_id(familyId);
        input.setLgd_district_code(304);
        input.setRemarks("Verified OK");
        input.setUser_id(42L);

        Map<String, Object> familyRow = new HashMap<>();
        familyRow.put("id", familyId);
        familyRow.put("lgd_district_code", 304);
        familyRow.put("created_by_local_body_code", 1001L);
        familyRow.put("app_serial", "APP-12345");
        familyRow.put("tmp_application_id", UUID.randomUUID().toString());

        when(mockReadJdbcTemplate.queryForList(contains("dbt_apy.families"), anyMap()))
                .thenReturn(List.of(familyRow));

        when(mockReadJdbcTemplate.queryForObject(contains("EXISTS"), anyMap(), eq(Boolean.class)))
                .thenReturn(true);

        java.sql.PreparedStatement mockPs = mock(java.sql.PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.getConnection()).thenReturn(mockConnection);
        when(mockPs.executeUpdate()).thenReturn(1);
        when(mockPs.executeBatch()).thenReturn(new int[]{1, 1});

        java.sql.ResultSet mockRs = mock(java.sql.ResultSet.class);
        java.sql.ResultSetMetaData mockRsMeta = mock(java.sql.ResultSetMetaData.class);
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.getMetaData()).thenReturn(mockRsMeta);
        when(mockRsMeta.getColumnCount()).thenReturn(3);
        when(mockRsMeta.getColumnLabel(1)).thenReturn("id");
        when(mockRsMeta.getColumnLabel(2)).thenReturn("family_serial");
        when(mockRsMeta.getColumnLabel(3)).thenReturn("lgd_district_code");
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getObject(1)).thenReturn(memberId);
        when(mockRs.getObject(2)).thenReturn(101L);
        when(mockRs.getObject(3)).thenReturn(304);

        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        ResponseEntity<?> response = controller.acceptFamily(input, mockRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Body was: " + response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(true, body.get("status"));
        assertEquals(200, body.get("statusCode"));
        assertTrue(body.get("message").toString().contains("Family Application Accepted. Family ID: APP-12345"));
        verify(mockConnection).commit();
    }
}
