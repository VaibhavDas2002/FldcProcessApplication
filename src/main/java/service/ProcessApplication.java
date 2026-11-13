package service;

import static responseHandler.ResponseHandler.error;
import static responseHandler.ResponseHandler.success;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import config.RoleId;
import repository.FamilyMemberJpaRepository;
import repository.LegacyFamilyMemberRepository;
import repository.processApplication;
import reqDTO.FamilyRequest;
import reqDTO.MemberRequest;
import resDTO.FamilyAcceptResponse;
import resDTO.FamilyStatusResponse;
import reqDTO.GetFamilyInput;
import resDTO.GetStatus;
import util.GlobalUpdateCheckHelper;
import util.Helper;
import util.ZoneDB;

/**
 * Service Layer for FLDC Process Application.
 * Handles Business Logic, Transactions, and DTO Mapping.
 */
@Service("processApplicationService")
public class ProcessApplication {

    private static final Logger log = LoggerFactory.getLogger(ProcessApplication.class);

    private final StatusFLDC statusFLDC;
    private final ZoneDatabaseManager zoneDatabaseManager;
    private final processApplication processApplicationRepo;
    private final FamilyMemberJpaRepository familyMemberJpaRepo;
    private final LegacyFamilyMemberRepository memberRepo;

    public ProcessApplication() {
        this(null, null, null, null, null);
    }

    public ProcessApplication(StatusFLDC statusFLDC) {
        this(statusFLDC, null, null, null, null);
    }

    public ProcessApplication(StatusFLDC statusFLDC, ZoneDatabaseManager zoneDatabaseManager) {
        this(statusFLDC, zoneDatabaseManager, null, null, null);
    }

    public ProcessApplication(
            StatusFLDC statusFLDC,
            ZoneDatabaseManager zoneDatabaseManager,
            processApplication processApplicationRepo) {
        this(statusFLDC, zoneDatabaseManager, processApplicationRepo, null, null);
    }

    @Autowired
    public ProcessApplication(
            @Autowired(required = false) StatusFLDC statusFLDC,
            @Autowired(required = false) ZoneDatabaseManager zoneDatabaseManager,
            @Autowired(required = false) processApplication processApplicationRepo,
            @Autowired(required = false) FamilyMemberJpaRepository familyMemberJpaRepo,
            @Autowired(required = false) LegacyFamilyMemberRepository memberRepo) {
        this.statusFLDC = statusFLDC;
        this.zoneDatabaseManager = zoneDatabaseManager;
        this.processApplicationRepo = processApplicationRepo;
        this.familyMemberJpaRepo = familyMemberJpaRepo;
        this.memberRepo = memberRepo;
    }

    private ZoneDatabaseManager getZoneDatabaseManager() {
        if (this.zoneDatabaseManager != null) {
            return this.zoneDatabaseManager;
        }
        return ZoneDB.getManager();
    }

    private processApplication getProcessApplicationRepo() {
        if (this.processApplicationRepo != null) {
            return this.processApplicationRepo;
        }
        return new processApplication(getZoneDatabaseManager());
    }

    // =========================================================================
    // Business Logic & Transactions
    // =========================================================================

    /**
     * Business Logic for accepting a family application.
     * Validates input state, snapshots family data, checks existing workflow state,
     * and executes the transactional database update.
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> acceptFamily(GetFamilyInput data, String clientIp) {
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
            return ResponseEntity.badRequest()
                    .body(error("Remarks cannot exceed 1000 characters", HttpStatus.BAD_REQUEST));
        }

        ZoneDatabaseManager dbManager = getZoneDatabaseManager();
        Integer distCode = null;
        if (data.getLgd_district_code() > 0) {
            distCode = data.getLgd_district_code();
        } else if (dbManager != null) {
            distCode = dbManager.getDistrictId();
        }
        if (distCode == null || distCode <= 0) {
            return ResponseEntity.badRequest()
                    .body(error("Invalid or missing lgd_district_code", HttpStatus.BAD_REQUEST));
        }

        try {
            // 1. Snapshot family data
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
                ipAddress = clientIp;
            }

            // 2. Business check: existing workflow state must be role 13 or 15
            boolean isStateValid = GlobalUpdateCheckHelper.checkingExistingStateFamily(
                    null,
                    familyId,
                    null,
                    List.of(13, 15),
                    distCode,
                    localBodyCode);

            if (!isStateValid) {
                return ResponseEntity.badRequest()
                        .body(error("Application is not available for verification", HttpStatus.BAD_REQUEST));
            }

            // 3. Execute DB operations in Repository with transaction
            Map<String, Object> dbResult = getProcessApplicationRepo().acceptFamily(
                    distCode,
                    familyId,
                    localBodyCode,
                    userId,
                    remarks,
                    ipAddress,
                    family);

            boolean isSuccess = Boolean.TRUE.equals(dbResult.get("success"));
            if (isSuccess) {
                Object appSerial = family.get("app_serial");
                String serialOrId = (appSerial != null) ? appSerial.toString() : familyId;

                int affectedMembers = 0;
                if (dbResult.get("affected_members") instanceof Number num) {
                    affectedMembers = num.intValue();
                }

                // Map to resDTO
                FamilyAcceptResponse responseDto = mapToFamilyAcceptResponse(familyId, appSerial, affectedMembers);

                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("family_id", responseDto.getFamilyId());
                dataMap.put("app_serial", responseDto.getAppSerial());
                dataMap.put("affected_members", responseDto.getAffectedMembers());

                return ResponseEntity.ok(success("Family Application Accepted. Family ID: " + serialOrId, dataMap));
            } else {
                return ResponseEntity.badRequest()
                        .body(error("Failed to accept family application.", HttpStatus.BAD_REQUEST));
            }
        } catch (SQLException e) {
            log.error("Database error during acceptFamily for familyId {}: {}", familyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(error("Failed to accept family application: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            log.error("Unexpected error during acceptFamily for familyId {}: {}", familyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(error("Failed to accept family application: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    public ResponseEntity<?> acceptFamily(GetFamilyInput data) {
        return acceptFamily(data, null);
    }

    /**
     * Business Logic for retrieving member status and role details.
     */
    public ResponseEntity<?> getStatus(int districtCode, String familyId, String memberId) {
        if (statusFLDC == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("Status service unavailable", HttpStatus.INTERNAL_SERVER_ERROR));
        }

        List<Map<String, Object>> data = statusFLDC.getStatusByDistrict(districtCode, familyId, memberId);

        if (data == null || data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(error("Family member details not found", HttpStatus.NOT_FOUND));
        }

        Object roleObj = data.get(0).get("next_level_role_id");
        Integer nextLevelRoleId = parseInteger(roleObj);

        String roleStatusName = RoleId.getRoleStatusName(nextLevelRoleId);
        if (roleStatusName == null) {
            roleStatusName = "Status unknown (Role ID: " + nextLevelRoleId + ")";
        }

        return ResponseEntity.ok(success(roleStatusName, data));
    }

    /**
     * Business Logic for retrieving family status by FamilyRequest DTO.
     */
    public ResponseEntity<?> getStatusByFamily(FamilyRequest request) {
        if (request == null || request.getDistrictCode() == null || request.getFamilyId() == null) {
            return ResponseEntity.badRequest()
                    .body(error("districtCode and familyId are required in request body", HttpStatus.BAD_REQUEST));
        }

        if (statusFLDC == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("Status service unavailable", HttpStatus.INTERNAL_SERVER_ERROR));
        }

        List<Map<String, Object>> data;
        if (request.getMemberId() != null && !request.getMemberId().isBlank()) {
            data = statusFLDC.getStatusByDistrict(request.getDistrictCode(), request.getFamilyId(),
                    request.getMemberId());
        } else {
            data = statusFLDC.getStatusByFamily(request.getDistrictCode(), request.getFamilyId());
        }

        if (data == null || data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(error("Family members not found for the given family", HttpStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(success("Family members retrieved successfully", data));
    }

    /**
     * Business Logic for retrieving member status by MemberRequest DTO.
     */
    public ResponseEntity<?> getStatusByMember(MemberRequest request) {
        if (request == null || request.getDistrictCode() == null || request.getFamilyId() == null || request.getMemberId() == null) {
            return ResponseEntity.badRequest()
                    .body(error("districtCode, familyId, and memberId are required", HttpStatus.BAD_REQUEST));
        }
        return getStatus(request.getDistrictCode(), request.getFamilyId(), request.getMemberId());
    }

    // =========================================================================
    // DTO Mappers
    // =========================================================================

    /**
     * Maps database results to FamilyAcceptResponse DTO.
     */
    public FamilyAcceptResponse mapToFamilyAcceptResponse(String familyId, Object appSerial, int affectedMembers) {
        return FamilyAcceptResponse.builder()
                .familyId(familyId)
                .appSerial(appSerial)
                .affectedMembers(affectedMembers)
                .build();
    }

    /**
     * Maps raw database map row to GetStatus DTO.
     */
    public GetStatus mapToGetStatus(Map<String, Object> row) {
        if (row == null) {
            return null;
        }
        return GetStatus.builder()
                .next_level_role_id(row.get("next_level_role_id"))
                .approval_datetime(row.get("approval_datetime"))
                .verification_datetime(row.get("verification_datetime"))
                .build();
    }

    /**
     * Maps a list of raw database map rows to List of GetStatus DTOs.
     */
    public List<GetStatus> mapToGetStatusList(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }
        List<GetStatus> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            list.add(mapToGetStatus(row));
        }
        return list;
    }

    /**
     * Maps status query results into a FamilyStatusResponse DTO.
     */
    public FamilyStatusResponse mapToFamilyStatusResponse(
            String roleStatusName,
            Integer nextLevelRoleId,
            List<Map<String, Object>> rawRows) {
        return FamilyStatusResponse.builder()
                .roleStatusName(roleStatusName)
                .nextLevelRoleId(nextLevelRoleId)
                .members(mapToGetStatusList(rawRows))
                .build();
    }

    /**
     * Maps a FamilyRequest to a MemberRequest DTO.
     */
    public MemberRequest mapToMemberRequest(FamilyRequest req) {
        if (req == null) {
            return null;
        }
        return MemberRequest.builder()
                .districtCode(req.getDistrictCode())
                .familyId(req.getFamilyId())
                .memberId(req.getMemberId())
                .localBodyCode(req.getLocalBodyCode())
                .appserial(req.getAppserial())
                .familyserial(req.getFamilyserial())
                .build();
    }

    private static Integer parseInteger(Object val) {
        if (val instanceof Number num) {
            return num.intValue();
        }
        if (val != null) {
            try {
                return Integer.valueOf(val.toString().trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}
