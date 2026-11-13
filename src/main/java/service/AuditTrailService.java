package service;

import java.util.UUID;

public interface AuditTrailService {

    void logFamilyAction(UUID familyId, Integer districtCode, String opType, Integer rejectRevertCauseId,
                        String remarks, Long userId, Integer roleId, String ipAddress);

    void logMemberAction(UUID familyId, UUID memberId, Integer districtCode, String opType,
                         Integer rejectRevertCauseId, String remarks, Long userId, Integer roleId, String ipAddress);
}

