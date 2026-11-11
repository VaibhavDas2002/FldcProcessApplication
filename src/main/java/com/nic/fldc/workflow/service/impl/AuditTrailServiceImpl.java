package com.nic.fldc.workflow.service.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nic.fldc.workflow.entity.AcceptRejectInfo;
import com.nic.fldc.workflow.repository.AcceptRejectInfoRepository;
import com.nic.fldc.workflow.service.AuditTrailService;

@Service
public class AuditTrailServiceImpl implements AuditTrailService {

    private static final Logger log = LoggerFactory.getLogger(AuditTrailServiceImpl.class);

    private final AcceptRejectInfoRepository auditRepo;

    public AuditTrailServiceImpl(AcceptRejectInfoRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void logFamilyAction(UUID familyId, Integer districtCode, String opType, Integer rejectRevertCauseId,
                                String remarks, Long userId, Integer roleId, String ipAddress) {
        AcceptRejectInfo record = AcceptRejectInfo.builder()
                .familyId(familyId)
                .familyMemberId(null)
                .lgdDistrictCode(districtCode)
                .opType(opType)
                .rejectRevertCauseId(rejectRevertCauseId)
                .remarks(remarks)
                .userId(userId != null ? userId : 0L)
                .roleId(roleId)
                .ipAddress(ipAddress)
                .build();

        auditRepo.save(record);
        log.info("Audit log written for family: {} op_type: {} by user: {}", familyId, opType, userId);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void logMemberAction(UUID familyId, UUID memberId, Integer districtCode, String opType,
                                Integer rejectRevertCauseId, String remarks, Long userId, Integer roleId, String ipAddress) {
        AcceptRejectInfo record = AcceptRejectInfo.builder()
                .familyId(familyId)
                .familyMemberId(memberId)
                .lgdDistrictCode(districtCode)
                .opType(opType)
                .rejectRevertCauseId(rejectRevertCauseId)
                .remarks(remarks)
                .userId(userId != null ? userId : 0L)
                .roleId(roleId)
                .ipAddress(ipAddress)
                .build();

        auditRepo.save(record);
        log.info("Audit log written for member: {} (family: {}) op_type: {} by user: {}", memberId, familyId, opType, userId);
    }
}

