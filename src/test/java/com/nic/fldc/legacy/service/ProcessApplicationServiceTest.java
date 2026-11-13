package com.nic.fldc.legacy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nic.fldc.legacy.reqDTO.FamilyRequest;
import com.nic.fldc.legacy.reqDTO.MemberRequest;
import com.nic.fldc.legacy.resDTO.FamilyAcceptResponse;
import com.nic.fldc.legacy.resDTO.FamilyStatusResponse;
import com.nic.fldc.legacy.resDTO.GetStatus;

public class ProcessApplicationServiceTest {

    private StatusFLDC mockStatusFLDC;
    private ZoneDatabaseManager mockZoneDatabaseManager;
    private com.nic.fldc.legacy.repository.processApplication mockRepo;
    private ProcessApplication service;

    @BeforeEach
    void setUp() {
        mockStatusFLDC = mock(StatusFLDC.class);
        mockZoneDatabaseManager = mock(ZoneDatabaseManager.class);
        mockRepo = mock(com.nic.fldc.legacy.repository.processApplication.class);

        service = new ProcessApplication(mockStatusFLDC, mockZoneDatabaseManager, mockRepo);
    }

    @Test
    void testMapToFamilyAcceptResponse() {
        String familyId = UUID.randomUUID().toString();
        FamilyAcceptResponse response = service.mapToFamilyAcceptResponse(familyId, "APP-999", 3);

        assertNotNull(response);
        assertEquals(familyId, response.getFamilyId());
        assertEquals("APP-999", response.getAppSerial());
        assertEquals(3, response.getAffectedMembers());
    }

    @Test
    void testMapToGetStatus() {
        Map<String, Object> row = new HashMap<>();
        row.put("next_level_role_id", 18);
        row.put("approval_datetime", "2026-10-29T10:00:00");
        row.put("verification_datetime", "2026-10-28T10:00:00");

        GetStatus status = service.mapToGetStatus(row);
        assertNotNull(status);
        assertEquals(18, status.getNext_level_role_id());
        assertEquals("2026-10-29T10:00:00", status.getApproval_datetime());
        assertEquals("2026-10-28T10:00:00", status.getVerification_datetime());
    }

    @Test
    void testMapToGetStatusList() {
        Map<String, Object> row1 = Map.of("next_level_role_id", 13);
        Map<String, Object> row2 = Map.of("next_level_role_id", 15);

        List<GetStatus> list = service.mapToGetStatusList(List.of(row1, row2));
        assertEquals(2, list.size());
        assertEquals(13, list.get(0).getNext_level_role_id());
        assertEquals(15, list.get(1).getNext_level_role_id());
    }

    @Test
    void testMapToFamilyStatusResponse() {
        Map<String, Object> row = Map.of("next_level_role_id", 18);
        FamilyStatusResponse res = service.mapToFamilyStatusResponse("Accepted", 18, List.of(row));

        assertNotNull(res);
        assertEquals("Accepted", res.getRoleStatusName());
        assertEquals(18, res.getNextLevelRoleId());
        assertEquals(1, res.getMembers().size());
    }

    @Test
    void testMapToMemberRequest() {
        FamilyRequest freq = FamilyRequest.builder()
                .districtCode(304)
                .familyId("fam-1")
                .memberId("mem-1")
                .localBodyCode(101)
                .appserial(1)
                .familyserial(2)
                .build();

        MemberRequest mreq = service.mapToMemberRequest(freq);
        assertNotNull(mreq);
        assertEquals(304, mreq.getDistrictCode());
        assertEquals("fam-1", mreq.getFamilyId());
        assertEquals("mem-1", mreq.getMemberId());
        assertEquals(101, mreq.getLocalBodyCode());
        assertEquals(1, mreq.getAppserial());
        assertEquals(2, mreq.getFamilyserial());
    }

    @Test
    void testGetStatus_NotFound() {
        when(mockStatusFLDC.getStatusByDistrict(anyInt(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        ResponseEntity<?> response = service.getStatus(304, "fam-1", "mem-1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetStatus_Success() {
        Map<String, Object> row = new HashMap<>();
        row.put("next_level_role_id", 18);
        when(mockStatusFLDC.getStatusByDistrict(304, "fam-1", "mem-1"))
                .thenReturn(List.of(row));

        ResponseEntity<?> response = service.getStatus(304, "fam-1", "mem-1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("status"));
    }

    @Test
    void testGetStatusByFamily_Validation() {
        ResponseEntity<?> response = service.getStatusByFamily(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        FamilyRequest reqNoDistrict = FamilyRequest.builder().familyId("fam-1").build();
        assertEquals(HttpStatus.BAD_REQUEST, service.getStatusByFamily(reqNoDistrict).getStatusCode());

        FamilyRequest reqNoFam = FamilyRequest.builder().districtCode(304).build();
        assertEquals(HttpStatus.BAD_REQUEST, service.getStatusByFamily(reqNoFam).getStatusCode());
    }

    @Test
    void testGetStatusByFamily_Success() {
        Map<String, Object> row = Map.of("id", UUID.randomUUID().toString());
        when(mockStatusFLDC.getStatusByFamily(304, "fam-1")).thenReturn(List.of(row));

        FamilyRequest req = FamilyRequest.builder().districtCode(304).familyId("fam-1").build();
        ResponseEntity<?> response = service.getStatusByFamily(req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetStatusByMember_Validation() {
        ResponseEntity<?> response = service.getStatusByMember(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        MemberRequest reqMissing = MemberRequest.builder().districtCode(304).familyId("fam-1").build();
        assertEquals(HttpStatus.BAD_REQUEST, service.getStatusByMember(reqMissing).getStatusCode());
    }
}
