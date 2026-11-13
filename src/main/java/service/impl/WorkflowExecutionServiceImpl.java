package service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import exception.EligibilityException;
import exception.InvalidWorkflowTransitionException;
import exception.ResourceNotFoundException;
import exception.UnauthorizedJurisdictionException;
import security.UserPrincipal;
import security.WorkflowRole;
import reqDTO.BatchMemberVerificationRequest;
import reqDTO.BulkApproveRequest;
import reqDTO.EnquiryActionRequest;
import reqDTO.FamilyActionRequest;
import reqDTO.HoldActionRequest;
import reqDTO.MemberVerificationItem;
import reqDTO.SubmitApplicationRequest;
import resDTO.WorkflowActionResponse;
import entity.Family;
import entity.FamilyMember;
import repository.FamilyMemberRepository;
import repository.FamilyRepository;
import service.AuditTrailService;
import service.WorkflowExecutionService;
import workflow.ApplicationStatus;
import workflow.NextLevelRoleId;
import workflow.OpType;
import workflow.WorkflowAction;
import workflow.WorkflowStateMachine;
import workflow.WorkflowTransition;

@Service
@Transactional(rollbackFor = Exception.class)
public class WorkflowExecutionServiceImpl implements WorkflowExecutionService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionServiceImpl.class);

    private final FamilyRepository familyRepo;
    private final FamilyMemberRepository memberRepo;
    private final AuditTrailService auditService;
    private final WorkflowStateMachine stateMachine;

    public WorkflowExecutionServiceImpl(FamilyRepository familyRepo,
                                       FamilyMemberRepository memberRepo,
                                       AuditTrailService auditService,
                                       WorkflowStateMachine stateMachine) {
        this.familyRepo = familyRepo;
        this.memberRepo = memberRepo;
        this.auditService = auditService;
        this.stateMachine = stateMachine;
    }

    private void validateJurisdiction(Integer requestedDistrict, UserPrincipal user) {
        if (user == null || user.hasRole(WorkflowRole.ROLE_ADMIN)) {
            return;
        }
        if (user.getDistrictCode() != null && requestedDistrict != null && !user.getDistrictCode().equals(requestedDistrict)) {
            throw new UnauthorizedJurisdictionException(requestedDistrict, user.getDistrictCode());
        }
    }

    private void validateRemarks(String remarks, int minLength, String action) {
        if (minLength > 0) {
            if (remarks == null || remarks.trim().length() < minLength) {
                throw new InvalidWorkflowTransitionException(
                        String.format("Action '%s' requires remarks of at least %d characters", action, minLength));
            }
        }
    }

    private boolean isOfficeEntry(String createMode) {
        return createMode != null && ("O".equalsIgnoreCase(createMode) || "OFFICE".equalsIgnoreCase(createMode));
    }

    // =========================================================================
    // 1. Citizen & Operator Actions
    // =========================================================================

    @Override
    public WorkflowActionResponse submitApplication(SubmitApplicationRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        WorkflowRole role = isOfficeEntry(family.getCreateMode()) ? WorkflowRole.ROLE_OPERATOR : WorkflowRole.ROLE_CITIZEN;
        WorkflowTransition transition = stateMachine.getTransition(role, WorkflowAction.SUBMIT, family.getNextLevelRoleId());

        LocalDateTime now = LocalDateTime.now();
        family.setNextLevelRoleId(transition.getTargetFamilyRoleId().getCode());
        family.setApplicationStatus(transition.getTargetFamilyStatus());
        family.setIsAgreed(Boolean.TRUE.equals(request.getIsAgreed()));
        family.setUpdatedAt(now);
        family.setUpdatedBy(user != null ? user.getUserId() : null);
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findByFamilyIdAndIsDeletedFalse(family.getId());
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
            m.setApplicationStatus(transition.getTargetMemberStatus());
            m.setUpdatedAt(now);
            m.setUpdatedBy(user != null ? user.getUserId() : null);
            memberRepo.save(m);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), transition.getOpType(), null,
                "Final Submission", user != null ? user.getUserId() : null, role == WorkflowRole.ROLE_CITIZEN ? 1 : 2, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.SUBMIT.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(transition.getTargetFamilyRoleId().getCode())
                .applicationStatus(transition.getTargetFamilyStatus())
                .opType(transition.getOpType())
                .message("Application submitted successfully.")
                .build();
    }

    @Override
    public WorkflowActionResponse resubmitApplication(SubmitApplicationRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        WorkflowRole role = isOfficeEntry(family.getCreateMode()) ? WorkflowRole.ROLE_OPERATOR : WorkflowRole.ROLE_CITIZEN;
        WorkflowTransition transition = stateMachine.getTransition(role, WorkflowAction.RESUBMIT, family.getNextLevelRoleId());

        LocalDateTime now = LocalDateTime.now();
        family.setNextLevelRoleId(transition.getTargetFamilyRoleId().getCode());
        family.setApplicationStatus(transition.getTargetFamilyStatus());
        family.setIsAgreed(Boolean.TRUE.equals(request.getIsAgreed()));
        family.setUpdatedAt(now);
        family.setUpdatedBy(user != null ? user.getUserId() : null);
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findByFamilyIdAndIsDeletedFalse(family.getId());
        for (FamilyMember m : members) {
            if (m.getNextLevelRoleId() != 30) {
                m.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
                m.setApplicationStatus(transition.getTargetMemberStatus());
                m.setUpdatedAt(now);
                m.setUpdatedBy(user != null ? user.getUserId() : null);
                memberRepo.save(m);
            }
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), transition.getOpType(), null,
                "Application Resubmitted after Correction", user != null ? user.getUserId() : null, role == WorkflowRole.ROLE_CITIZEN ? 1 : 2, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.RESUBMIT.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(transition.getTargetFamilyRoleId().getCode())
                .applicationStatus(transition.getTargetFamilyStatus())
                .opType(transition.getOpType())
                .message("Application resubmitted successfully.")
                .build();
    }

    // =========================================================================
    // 2. Enquiry Officer Actions
    // =========================================================================

    @Override
    public WorkflowActionResponse enquiryRecommendAccept(EnquiryActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_ENQUIRY_OFFICER,
                WorkflowAction.ENQUIRY_RECOMMEND_ACCEPT, family.getNextLevelRoleId());

        LocalDateTime now = LocalDateTime.now();
        family.setNextLevelRoleId(transition.getTargetFamilyRoleId().getCode());
        family.setApplicationStatus(transition.getTargetFamilyStatus());
        family.setEnquiryRemarks(request.getRemarks());
        family.setEnquiryDatetime(now);
        family.setEnquiryConductedBy(user != null ? user.getUserId() : null);
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findNonApprovedMembers(family.getId(), 30);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
            m.setApplicationStatus(transition.getTargetMemberStatus());
            m.setEnquiryRemarks(request.getRemarks());
            m.setEnquiryDatetime(now);
            m.setEnquiryConductedBy(user != null ? user.getUserId() : null);
            memberRepo.save(m);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), transition.getOpType(), null,
                request.getRemarks(), user != null ? user.getUserId() : null, 3, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.ENQUIRY_RECOMMEND_ACCEPT.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(transition.getTargetFamilyRoleId().getCode())
                .applicationStatus(transition.getTargetFamilyStatus())
                .opType(transition.getOpType())
                .message("Enquiry verified and recommended for acceptance to Verifier.")
                .build();
    }

    @Override
    public WorkflowActionResponse enquiryRecommendReject(EnquiryActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_ENQUIRY_OFFICER,
                WorkflowAction.ENQUIRY_RECOMMEND_REJECT, family.getNextLevelRoleId());

        LocalDateTime now = LocalDateTime.now();
        family.setNextLevelRoleId(transition.getTargetFamilyRoleId().getCode());
        family.setApplicationStatus(transition.getTargetFamilyStatus());
        family.setEnquiryRemarks(request.getRemarks());
        family.setEnquiryDatetime(now);
        family.setEnquiryConductedBy(user != null ? user.getUserId() : null);
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findNonApprovedMembers(family.getId(), 30);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
            m.setApplicationStatus(transition.getTargetMemberStatus());
            m.setEnquiryRemarks(request.getRemarks());
            m.setEnquiryDatetime(now);
            m.setEnquiryConductedBy(user != null ? user.getUserId() : null);
            memberRepo.save(m);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), transition.getOpType(), null,
                request.getRemarks(), user != null ? user.getUserId() : null, 3, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.ENQUIRY_RECOMMEND_REJECT.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(transition.getTargetFamilyRoleId().getCode())
                .applicationStatus(transition.getTargetFamilyStatus())
                .opType(transition.getOpType())
                .message("Enquiry recommended for rejection to Verifier.")
                .build();
    }

    @Override
    public WorkflowActionResponse enquiryRevert(EnquiryActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        stateMachine.getTransition(WorkflowRole.ROLE_ENQUIRY_OFFICER, WorkflowAction.ENQUIRY_REVERT, family.getNextLevelRoleId());

        // Role 7 if Office, Role 5 if Citizen
        int targetRoleId = isOfficeEntry(family.getCreateMode()) ? 7 : 5;
        LocalDateTime now = LocalDateTime.now();

        family.setNextLevelRoleId(targetRoleId);
        family.setApplicationStatus(ApplicationStatus.ENQUIRY_REVERTED);
        family.setEnquiryRemarks(request.getRemarks());
        family.setEnquiryDatetime(now);
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findNonApprovedMembers(family.getId(), 30);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(targetRoleId);
            m.setApplicationStatus(ApplicationStatus.ENQUIRY_REVERTED);
            m.setEnquiryRemarks(request.getRemarks());
            m.setEnquiryDatetime(now);
            memberRepo.save(m);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), OpType.ENQUIRY_REVERTED,
                request.getRejectRevertCauseId(), request.getRemarks(), user != null ? user.getUserId() : null, 3, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.ENQUIRY_REVERT.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(targetRoleId)
                .applicationStatus(ApplicationStatus.ENQUIRY_REVERTED)
                .opType(OpType.ENQUIRY_REVERTED)
                .message("Application reverted by Enquiry Officer.")
                .build();
    }

    @Override
    public WorkflowActionResponse enquiryEditSave(EnquiryActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_ENQUIRY_OFFICER,
                WorkflowAction.ENQUIRY_EDIT_SAVE, family.getNextLevelRoleId());

        LocalDateTime now = LocalDateTime.now();
        family.setNextLevelRoleId(transition.getTargetFamilyRoleId().getCode());
        family.setApplicationStatus(transition.getTargetFamilyStatus());
        family.setUpdatedAt(now);
        family.setUpdatedBy(user != null ? user.getUserId() : null);
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findNonApprovedMembers(family.getId(), 30);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
            m.setApplicationStatus(transition.getTargetMemberStatus());
            m.setUpdatedAt(now);
            m.setUpdatedBy(user != null ? user.getUserId() : null);
            memberRepo.save(m);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), transition.getOpType(), null,
                "Enquiry edited and saved", user != null ? user.getUserId() : null, 3, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.ENQUIRY_EDIT_SAVE.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(transition.getTargetFamilyRoleId().getCode())
                .applicationStatus(transition.getTargetFamilyStatus())
                .opType(transition.getOpType())
                .message("Enquiry details updated and forwarded to Verifier.")
                .build();
    }

    // =========================================================================
    // 3. Verifier Family-Level Actions
    // =========================================================================

    @Override
    public WorkflowActionResponse verifierAcceptFamily(FamilyActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER,
                WorkflowAction.VERIFIER_ACCEPT_FAMILY, family.getNextLevelRoleId());

        family.setNextLevelRoleId(transition.getTargetFamilyRoleId().getCode());
        family.setApplicationStatus(transition.getTargetFamilyStatus());
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findNonApprovedMembers(family.getId(), 30);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
            m.setApplicationStatus(transition.getTargetMemberStatus());
            memberRepo.save(m);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), transition.getOpType(), null,
                request.getRemarks(), user != null ? user.getUserId() : null, 4, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.VERIFIER_ACCEPT_FAMILY.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(transition.getTargetFamilyRoleId().getCode())
                .applicationStatus(transition.getTargetFamilyStatus())
                .opType(transition.getOpType())
                .message("Family Application Accepted. Member verification enabled.")
                .build();
    }

    @Override
    public WorkflowActionResponse verifierRejectFamily(FamilyActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER,
                WorkflowAction.VERIFIER_REJECT_FAMILY, family.getNextLevelRoleId());

        LocalDateTime now = LocalDateTime.now();
        family.setNextLevelRoleId(transition.getTargetFamilyRoleId().getCode());
        family.setApplicationStatus(transition.getTargetFamilyStatus());
        family.setVerificationDatetime(now);
        family.setVerifiedBy(user != null ? user.getUserId() : null);
        family.setVerificationRemarks(request.getRemarks());
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findNonApprovedMembers(family.getId(), 30);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
            m.setApplicationStatus(transition.getTargetMemberStatus());
            m.setVerificationDatetime(now);
            m.setVerifiedBy(user != null ? user.getUserId() : null);
            memberRepo.save(m);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), transition.getOpType(),
                request.getRejectRevertCauseId(), request.getRemarks(), user != null ? user.getUserId() : null, 4, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.VERIFIER_REJECT_FAMILY.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(transition.getTargetFamilyRoleId().getCode())
                .applicationStatus(transition.getTargetFamilyStatus())
                .opType(transition.getOpType())
                .message("Family Application Rejected.")
                .build();
    }

    @Override
    public WorkflowActionResponse verifierRevertFamily(FamilyActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER, WorkflowAction.VERIFIER_REVERT_FAMILY, family.getNextLevelRoleId());

        // Role 8 for Office, Role 6 for Citizen
        int targetRoleId = isOfficeEntry(family.getCreateMode()) ? 8 : 6;
        LocalDateTime now = LocalDateTime.now();

        family.setNextLevelRoleId(targetRoleId);
        family.setApplicationStatus(ApplicationStatus.REVERTED);
        family.setDetailStatus(99);
        family.setVerificationDatetime(now);
        family.setVerifiedBy(user != null ? user.getUserId() : null);
        family.setVerificationRemarks(request.getRemarks());
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findNonApprovedMembers(family.getId(), 30);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(targetRoleId);
            m.setApplicationStatus(ApplicationStatus.REVERTED);
            m.setDetailStatus(99);
            m.setVerificationDatetime(now);
            m.setVerifiedBy(user != null ? user.getUserId() : null);
            memberRepo.save(m);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), OpType.REVERT_FAMILY,
                request.getRejectRevertCauseId(), request.getRemarks(), user != null ? user.getUserId() : null, 4, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.VERIFIER_REVERT_FAMILY.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(targetRoleId)
                .applicationStatus(ApplicationStatus.REVERTED)
                .opType(OpType.REVERT_FAMILY)
                .message("Family Application Reverted with detail_status = 99.")
                .build();
    }

    @Override
    public WorkflowActionResponse verifierRecommendEnquiry(FamilyActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        validateRemarks(request.getRemarks(), 5, "Recommend for Enquiry");

        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER,
                WorkflowAction.VERIFIER_RECOMMEND_ENQUIRY, family.getNextLevelRoleId());

        LocalDateTime now = LocalDateTime.now();
        family.setNextLevelRoleId(transition.getTargetFamilyRoleId().getCode());
        family.setApplicationStatus(transition.getTargetFamilyStatus());
        family.setVerificationDatetime(now);
        family.setVerifiedBy(user != null ? user.getUserId() : null);
        family.setVerificationRemarks(request.getRemarks());
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findNonApprovedMembers(family.getId(), 30);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
            m.setApplicationStatus(transition.getTargetMemberStatus());
            m.setVerificationDatetime(now);
            m.setVerifiedBy(user != null ? user.getUserId() : null);
            memberRepo.save(m);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), transition.getOpType(), null,
                request.getRemarks(), user != null ? user.getUserId() : null, 4, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.VERIFIER_RECOMMEND_ENQUIRY.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(transition.getTargetFamilyRoleId().getCode())
                .applicationStatus(transition.getTargetFamilyStatus())
                .opType(transition.getOpType())
                .message("Application Recommended for Enquiry back to Enquiry Officer.")
                .build();
    }

    @Override
    public WorkflowActionResponse verifierHoldFamily(HoldActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        validateRemarks(request.getRemarks(), 15, "Verifier Hold");

        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER,
                WorkflowAction.VERIFIER_HOLD_FAMILY, family.getNextLevelRoleId());

        LocalDateTime now = LocalDateTime.now();
        family.setNextLevelRoleId(transition.getTargetFamilyRoleId().getCode());
        family.setApplicationStatus(transition.getTargetFamilyStatus());
        family.setVerificationRemarks(request.getRemarks());
        family.setVerificationDatetime(now);
        family.setVerifiedBy(user != null ? user.getUserId() : null);
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findNonApprovedMembers(family.getId(), 30);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
            m.setApplicationStatus(transition.getTargetMemberStatus());
            m.setVerificationRemarks(request.getRemarks());
            m.setVerificationDatetime(now);
            m.setVerifiedBy(user != null ? user.getUserId() : null);
            memberRepo.save(m);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), transition.getOpType(), null,
                request.getRemarks(), user != null ? user.getUserId() : null, 4, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.VERIFIER_HOLD_FAMILY.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(transition.getTargetFamilyRoleId().getCode())
                .applicationStatus(transition.getTargetFamilyStatus())
                .opType(transition.getOpType())
                .message("Family application placed on Verifier Hold.")
                .build();
    }

    // =========================================================================
    // 4. Verifier Member Actions & Mixed-Revert Cascade
    // =========================================================================

    @Override
    public WorkflowActionResponse verifierConfirmVerification(BatchMemberVerificationRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        if (family.getNextLevelRoleId() != 18) {
            throw new InvalidWorkflowTransitionException(
                    "Member verification can only occur after the family has been accepted (role 18). Current family role: " + family.getNextLevelRoleId());
        }

        LocalDateTime now = LocalDateTime.now();
        Long userId = user != null ? user.getUserId() : null;

        // CRITICAL BUSINESS RULE 8: Mixed-Revert Cascade Check
        if (request.hasRevertAction()) {
            log.info("Mixed-revert detected for family {}. Triggering application-wide revert cascade.", family.getId());
            int targetRoleId = isOfficeEntry(family.getCreateMode()) ? 8 : 6;

            family.setNextLevelRoleId(targetRoleId);
            family.setApplicationStatus(ApplicationStatus.REVERTED);
            family.setDetailStatus(99);
            family.setVerificationDatetime(now);
            family.setVerifiedBy(userId);
            familyRepo.save(family);

            List<FamilyMember> allMembers = memberRepo.findNonApprovedMembers(family.getId(), 30);
            for (FamilyMember m : allMembers) {
                m.setNextLevelRoleId(targetRoleId);
                m.setApplicationStatus(ApplicationStatus.REVERTED);
                m.setDetailStatus(99);
                m.setVerificationDatetime(now);
                m.setVerifiedBy(userId);
                memberRepo.save(m);
                auditService.logMemberAction(family.getId(), m.getId(), request.getDistrictCode(), OpType.REVERT,
                        null, "Reverted via mixed revert cascade", userId, 4, ipAddress);
            }

            auditService.logFamilyAction(family.getId(), request.getDistrictCode(), OpType.REVERT_FAMILY,
                    null, "Whole application reverted due to member revert selection", userId, 4, ipAddress);

            return WorkflowActionResponse.builder()
                .action(WorkflowAction.VERIFIER_REVERT_MEMBER_CASCADE.name())
                .familyId(family.getId())
                .affectedMembers(allMembers.size())
                .nextLevelRoleId(targetRoleId)
                .applicationStatus(ApplicationStatus.REVERTED)
                .mixedRevertCascaded(true)
                .opType(OpType.REVERT_FAMILY)
                .message("You Cannot Revert a single application, on Confirmation whole application has been reverted.")
                .build();
        }

        // Standard Non-Revert Verification / Rejection
        int processedCount = 0;
        for (MemberVerificationItem item : request.getMembers()) {
            FamilyMember member = memberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(item.getMemberId(), request.getDistrictCode())
                    .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", item.getMemberId()));

            // Member Eligibility Check: Female, Age 25-60, AY=true, Role 18
            if (!Boolean.TRUE.equals(member.getApplyingForAnnapurnaBhandar())) {
                throw new EligibilityException(member.getId(), "applying_for_annapurna_bhandar must be true");
            }
            if (!"F".equalsIgnoreCase(member.getGender())) {
                throw new EligibilityException(member.getId(), "Member must be Female");
            }
            int age = member.getAge() != null ? member.getAge() : 0;
            if (age < 25 || age > 60) {
                throw new EligibilityException(member.getId(), "Age must be between 25 and 60 years (Current: " + age + ")");
            }
            if (member.getNextLevelRoleId() != 18) {
                throw new InvalidWorkflowTransitionException(
                        String.format("Member %s is not in role 18 (current: %s)", member.getId(), member.getNextLevelRoleId()));
            }

            if ("VERIFY".equalsIgnoreCase(item.getAction())) {
                member.setNextLevelRoleId(20);
                member.setApplicationStatus(ApplicationStatus.VERIFIED);
                member.setVerifiedBy(userId);
                member.setVerificationDatetime(now);
                member.setNotInAsdd(Boolean.TRUE.equals(item.getNotInAsdd()));
                member.setAppealTribunal(item.getAppealTribunal());
                memberRepo.save(member);

                auditService.logMemberAction(family.getId(), member.getId(), request.getDistrictCode(), OpType.VERIFY,
                        null, item.getRemarks(), userId, 4, ipAddress);
                processedCount++;
            } else if ("REJECT".equalsIgnoreCase(item.getAction())) {
                member.setNextLevelRoleId(-101);
                member.setApplicationStatus(ApplicationStatus.REJECTED);
                member.setVerifiedBy(userId);
                member.setVerificationDatetime(now);
                memberRepo.save(member);

                auditService.logMemberAction(family.getId(), member.getId(), request.getDistrictCode(), OpType.REJECT,
                        item.getRejectRevertCauseId(), item.getRemarks(), userId, 4, ipAddress);
                processedCount++;
            } else {
                throw new InvalidWorkflowTransitionException("Unrecognized member action: " + item.getAction());
            }
        }

        return WorkflowActionResponse.builder()
                .action("BATCH_MEMBER_VERIFICATION")
                .familyId(family.getId())
                .affectedMembers(processedCount)
                .nextLevelRoleId(18) // Family remains at 18
                .applicationStatus(ApplicationStatus.ACCEPTED)
                .mixedRevertCascaded(false)
                .opType(OpType.VERIFY)
                .message("Selected eligible members verified successfully.")
                .build();
    }

    @Override
    public WorkflowActionResponse verifierRecommendRejectApproved(UUID memberId, Integer districtCode, String remarks, UserPrincipal user, String ipAddress) {
        validateJurisdiction(districtCode, user);
        FamilyMember member = memberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode)
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", memberId));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER,
                WorkflowAction.VERIFIER_RECOMMEND_REJECT_APPROVED, member.getNextLevelRoleId());

        member.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
        member.setApplicationStatus(transition.getTargetMemberStatus());
        memberRepo.save(member);

        auditService.logMemberAction(member.getFamilyId(), member.getId(), districtCode, transition.getOpType(),
                null, remarks, user != null ? user.getUserId() : null, 4, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.VERIFIER_RECOMMEND_REJECT_APPROVED.name())
                .familyId(member.getFamilyId())
                .memberId(member.getId())
                .affectedMembers(1)
                .nextLevelRoleId(transition.getTargetMemberRoleId().getCode())
                .applicationStatus(transition.getTargetMemberStatus())
                .opType(transition.getOpType())
                .message("Approved member recommended for rejection to Approver.")
                .build();
    }

    // =========================================================================
    // 5. Approver Actions
    // =========================================================================

    @Override
    public WorkflowActionResponse approverApproveMember(UUID memberId, Integer districtCode, UserPrincipal user, String ipAddress) {
        validateJurisdiction(districtCode, user);
        FamilyMember member = memberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode)
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", memberId));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_APPROVER,
                WorkflowAction.APPROVER_APPROVE_MEMBER, member.getNextLevelRoleId());

        LocalDateTime now = LocalDateTime.now();
        member.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
        member.setApplicationStatus(transition.getTargetMemberStatus());
        member.setApprovedBy(user != null ? user.getUserId() : null);
        member.setApprovalDatetime(now);
        memberRepo.save(member);

        auditService.logMemberAction(member.getFamilyId(), member.getId(), districtCode, transition.getOpType(),
                null, "Member Approved by Approver", user != null ? user.getUserId() : null, 5, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.APPROVER_APPROVE_MEMBER.name())
                .familyId(member.getFamilyId())
                .memberId(member.getId())
                .affectedMembers(1)
                .nextLevelRoleId(transition.getTargetMemberRoleId().getCode())
                .applicationStatus(transition.getTargetMemberStatus())
                .opType(transition.getOpType())
                .message("Member Approved successfully.")
                .build();
    }

    @Override
    public WorkflowActionResponse approverBulkApprove(BulkApproveRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        LocalDateTime now = LocalDateTime.now();
        Long userId = user != null ? user.getUserId() : null;
        int count = 0;

        for (UUID memberId : request.getMemberIds()) {
            FamilyMember member = memberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, request.getDistrictCode())
                    .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", memberId));

            if (member.getNextLevelRoleId() != 20) {
                throw new InvalidWorkflowTransitionException(
                        String.format("Member %s must be in role 20 (Verified) for approval. Current: %s", memberId, member.getNextLevelRoleId()));
            }

            member.setNextLevelRoleId(30);
            member.setApplicationStatus(ApplicationStatus.APPROVED);
            member.setApprovedBy(userId);
            member.setApprovalDatetime(now);
            memberRepo.save(member);

            auditService.logMemberAction(member.getFamilyId(), member.getId(), request.getDistrictCode(), OpType.APPROVE,
                    null, "Bulk Approved by Approver", userId, 5, ipAddress);
            count++;
        }

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.APPROVER_BULK_APPROVE.name())
                .affectedMembers(count)
                .nextLevelRoleId(30)
                .applicationStatus(ApplicationStatus.APPROVED)
                .opType(OpType.APPROVE)
                .message(count + " members approved successfully.")
                .build();
    }

    @Override
    public WorkflowActionResponse approverRejectMember(FamilyActionRequest request, UUID memberId, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        FamilyMember member = memberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", memberId));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_APPROVER,
                WorkflowAction.APPROVER_REJECT_MEMBER, member.getNextLevelRoleId());

        LocalDateTime now = LocalDateTime.now();
        member.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
        member.setApplicationStatus(transition.getTargetMemberStatus());
        member.setApprovedBy(user != null ? user.getUserId() : null);
        member.setApprovalDatetime(now);
        member.setApprovalRemarks(request.getRemarks());
        memberRepo.save(member);

        auditService.logMemberAction(member.getFamilyId(), member.getId(), request.getDistrictCode(), transition.getOpType(),
                request.getRejectRevertCauseId(), request.getRemarks(), user != null ? user.getUserId() : null, 5, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.APPROVER_REJECT_MEMBER.name())
                .familyId(member.getFamilyId())
                .memberId(member.getId())
                .affectedMembers(1)
                .nextLevelRoleId(transition.getTargetMemberRoleId().getCode())
                .applicationStatus(transition.getTargetMemberStatus())
                .opType(transition.getOpType())
                .message("Member Rejected by Approver.")
                .build();
    }

    @Override
    public WorkflowActionResponse approverRevertCascade(FamilyActionRequest request, UUID memberId, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        FamilyMember member = memberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", memberId));

        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(member.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", member.getFamilyId()));

        // CRITICAL BUSINESS RULE: Approver revert cascades to whole family & non-approved members
        int targetRoleId = isOfficeEntry(family.getCreateMode()) ? 9 : 4;
        LocalDateTime now = LocalDateTime.now();
        Long userId = user != null ? user.getUserId() : null;

        family.setNextLevelRoleId(targetRoleId);
        family.setApplicationStatus(ApplicationStatus.REVERTED);
        family.setDetailStatus(99);
        family.setApprovalDatetime(now);
        family.setApprovedBy(userId);
        family.setApprovalRemarks(request.getRemarks());
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findNonApprovedMembers(family.getId(), 30);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(targetRoleId);
            m.setApplicationStatus(ApplicationStatus.REVERTED);
            m.setDetailStatus(99);
            m.setApprovalDatetime(now);
            m.setApprovedBy(userId);
            m.setApprovalRemarks(request.getRemarks());
            memberRepo.save(m);
            auditService.logMemberAction(family.getId(), m.getId(), request.getDistrictCode(), OpType.REVERT,
                    request.getRejectRevertCauseId(), request.getRemarks(), userId, 5, ipAddress);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), OpType.REVERT_FAMILY,
                request.getRejectRevertCauseId(), request.getRemarks(), userId, 5, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.APPROVER_REVERT_MEMBER_CASCADE.name())
                .familyId(family.getId())
                .memberId(memberId)
                .affectedMembers(members.size())
                .nextLevelRoleId(targetRoleId)
                .applicationStatus(ApplicationStatus.REVERTED)
                .mixedRevertCascaded(true)
                .opType(OpType.REVERT_FAMILY)
                .message("Application reverted by Approver with detail_status = 99.")
                .build();
    }

    @Override
    public WorkflowActionResponse approverHoldFamily(HoldActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        validateRemarks(request.getRemarks(), 15, "Approver Family Hold");

        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getFamilyId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("Family", request.getFamilyId()));

        LocalDateTime now = LocalDateTime.now();
        Long userId = user != null ? user.getUserId() : null;

        family.setNextLevelRoleId(26);
        family.setApplicationStatus(ApplicationStatus.HELDUP_A);
        family.setApprovalRemarks(request.getRemarks());
        family.setApprovalDatetime(now);
        family.setApprovedBy(userId);
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findNonApprovedMembers(family.getId(), 30);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(26);
            m.setApplicationStatus(ApplicationStatus.HELDUP_A);
            m.setApprovalRemarks(request.getRemarks());
            m.setApprovalDatetime(now);
            m.setApprovedBy(userId);
            memberRepo.save(m);
        }

        auditService.logFamilyAction(family.getId(), request.getDistrictCode(), OpType.HELDUP_A, null,
                request.getRemarks(), userId, 5, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.APPROVER_HOLD_FAMILY.name())
                .familyId(family.getId())
                .affectedMembers(members.size())
                .nextLevelRoleId(26)
                .applicationStatus(ApplicationStatus.HELDUP_A)
                .opType(OpType.HELDUP_A)
                .message("Family application marked on Approver Hold.")
                .build();
    }

    @Override
    public WorkflowActionResponse approverHoldSingleMember(HoldActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        validateRemarks(request.getRemarks(), 15, "Approver Member Hold");

        if (request.getMemberId() == null) {
            throw new IllegalArgumentException("memberId is required for single member hold");
        }

        FamilyMember member = memberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getMemberId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", request.getMemberId()));

        LocalDateTime now = LocalDateTime.now();
        Long userId = user != null ? user.getUserId() : null;

        member.setNextLevelRoleId(26);
        member.setApplicationStatus(ApplicationStatus.HELDUP_M_A);
        member.setApprovalRemarks(request.getRemarks());
        member.setApprovalDatetime(now);
        member.setApprovedBy(userId);
        memberRepo.save(member);

        auditService.logMemberAction(member.getFamilyId(), member.getId(), request.getDistrictCode(), OpType.HELDUP_M_A,
                null, request.getRemarks(), userId, 5, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.APPROVER_HOLD_SINGLE_MEMBER.name())
                .familyId(member.getFamilyId())
                .memberId(member.getId())
                .affectedMembers(1)
                .nextLevelRoleId(26)
                .applicationStatus(ApplicationStatus.HELDUP_M_A)
                .opType(OpType.HELDUP_M_A)
                .message("Member marked on Approver Hold without changing family state.")
                .build();
    }

    @Override
    public WorkflowActionResponse approverTempHold(HoldActionRequest request, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        FamilyMember member = memberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(request.getMemberId(), request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", request.getMemberId()));

        stateMachine.getTransition(WorkflowRole.ROLE_APPROVER, WorkflowAction.APPROVER_TEMP_HOLD_MEMBER, member.getNextLevelRoleId());

        String statusStr = ApplicationStatus.holdType(request.getHoldType());
        String auditOp = OpType.holdAuditType(request.getHoldType());

        member.setNextLevelRoleId(25);
        member.setApplicationStatus(statusStr);
        memberRepo.save(member);

        auditService.logMemberAction(member.getFamilyId(), member.getId(), request.getDistrictCode(), auditOp,
                null, request.getRemarks(), user != null ? user.getUserId() : null, 5, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.APPROVER_TEMP_HOLD_MEMBER.name())
                .familyId(member.getFamilyId())
                .memberId(member.getId())
                .affectedMembers(1)
                .nextLevelRoleId(25)
                .applicationStatus(statusStr)
                .opType(auditOp)
                .message("Temporary hold applied on approved member.")
                .build();
    }

    @Override
    public WorkflowActionResponse approverRejectHoldMember(FamilyActionRequest request, UUID memberId, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        FamilyMember member = memberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", memberId));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_APPROVER,
                WorkflowAction.APPROVER_REJECT_HOLD_MEMBER, member.getNextLevelRoleId());

        member.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
        member.setApplicationStatus(transition.getTargetMemberStatus());
        memberRepo.save(member);

        auditService.logMemberAction(member.getFamilyId(), member.getId(), request.getDistrictCode(), transition.getOpType(),
                request.getRejectRevertCauseId(), request.getRemarks(), user != null ? user.getUserId() : null, 5, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.APPROVER_REJECT_HOLD_MEMBER.name())
                .familyId(member.getFamilyId())
                .memberId(member.getId())
                .affectedMembers(1)
                .nextLevelRoleId(transition.getTargetMemberRoleId().getCode())
                .applicationStatus(transition.getTargetMemberStatus())
                .opType(transition.getOpType())
                .message("Held member rejected by Approver.")
                .build();
    }

    @Override
    public WorkflowActionResponse approverRejectRecommended(FamilyActionRequest request, UUID memberId, UserPrincipal user, String ipAddress) {
        validateJurisdiction(request.getDistrictCode(), user);
        FamilyMember member = memberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, request.getDistrictCode())
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", memberId));

        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_APPROVER,
                WorkflowAction.APPROVER_REJECT_RECOMMENDED, member.getNextLevelRoleId());

        member.setNextLevelRoleId(transition.getTargetMemberRoleId().getCode());
        member.setApplicationStatus(transition.getTargetMemberStatus());
        memberRepo.save(member);

        auditService.logMemberAction(member.getFamilyId(), member.getId(), request.getDistrictCode(), transition.getOpType(),
                request.getRejectRevertCauseId(), request.getRemarks(), user != null ? user.getUserId() : null, 5, ipAddress);

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.APPROVER_REJECT_RECOMMENDED.name())
                .familyId(member.getFamilyId())
                .memberId(member.getId())
                .affectedMembers(1)
                .nextLevelRoleId(transition.getTargetMemberRoleId().getCode())
                .applicationStatus(transition.getTargetMemberStatus())
                .opType(transition.getOpType())
                .message("Recommended member rejected by Approver.")
                .build();
    }

    // =========================================================================
    // 6. Administrative Re-Processing (RevertRejectToApprove)
    // =========================================================================

    @Override
    public WorkflowActionResponse adminApproveFromReject(UUID memberId, Integer districtCode, UserPrincipal user, String ipAddress) {
        validateJurisdiction(districtCode, user);
        FamilyMember member = memberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode)
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", memberId));

        stateMachine.getTransition(WorkflowRole.ROLE_ADMIN, WorkflowAction.ADMIN_APPROVE_FROM_REJECT, member.getNextLevelRoleId());

        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(member.getFamilyId(), districtCode)
                .orElseThrow(() -> new ResourceNotFoundException("Family", member.getFamilyId()));

        long nonRejectedCount = memberRepo.countByFamilyIdAndNextLevelRoleIdInAndIsDeletedFalse(
                family.getId(), List.of(0, 10, 11, 13, 15, 17, 18, 20, 25, 26, 27, 30));

        boolean isWholeFamilyRejected = (nonRejectedCount == 0);
        int affected = 0;

        if (isWholeFamilyRejected) {
            family.setNextLevelRoleId(18);
            family.setApplicationStatus(ApplicationStatus.ACCEPTED);
            familyRepo.save(family);

            List<FamilyMember> ayMembers = memberRepo.findByFamilyIdAndApplyingForAnnapurnaBhandarTrueAndIsDeletedFalse(family.getId());
            for (FamilyMember m : ayMembers) {
                m.setNextLevelRoleId(30);
                m.setApplicationStatus(ApplicationStatus.APPROVE);
                memberRepo.save(m);
                auditService.logMemberAction(family.getId(), m.getId(), districtCode, OpType.APPROVE_FROM_REJECT,
                        null, "Re-Approved from Reject stage", user != null ? user.getUserId() : null, 99, ipAddress);
                affected++;
            }
        } else {
            member.setNextLevelRoleId(30);
            member.setApplicationStatus(ApplicationStatus.APPROVE);
            memberRepo.save(member);
            auditService.logMemberAction(family.getId(), member.getId(), districtCode, OpType.APPROVE_FROM_REJECT,
                    null, "Re-Approved single member from Reject stage", user != null ? user.getUserId() : null, 99, ipAddress);
            affected = 1;
        }

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.ADMIN_APPROVE_FROM_REJECT.name())
                .familyId(family.getId())
                .memberId(memberId)
                .affectedMembers(affected)
                .nextLevelRoleId(30)
                .applicationStatus(ApplicationStatus.APPROVE)
                .opType(OpType.APPROVE_FROM_REJECT)
                .message("Previously rejected member re-approved successfully.")
                .build();
    }

    @Override
    public WorkflowActionResponse adminSendToEnquiry(UUID familyId, Integer districtCode, String remarks, UserPrincipal user, String ipAddress) {
        validateJurisdiction(districtCode, user);
        validateRemarks(remarks, 5, "Send Back to Enquiry");

        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode)
                .orElseThrow(() -> new ResourceNotFoundException("Family", familyId));

        family.setNextLevelRoleId(10);
        family.setApplicationStatus(ApplicationStatus.REJECT_TO_ENQUIRY);
        familyRepo.save(family);

        List<FamilyMember> members = memberRepo.findByFamilyIdAndIsDeletedFalse(familyId);
        for (FamilyMember m : members) {
            m.setNextLevelRoleId(10);
            m.setApplicationStatus(ApplicationStatus.REJECT_TO_ENQUIRY);
            memberRepo.save(m);
            auditService.logMemberAction(familyId, m.getId(), districtCode, OpType.REJECT_TO_ENQUIRY,
                    null, remarks, user != null ? user.getUserId() : null, 99, ipAddress);
        }

        return WorkflowActionResponse.builder()
                .action(WorkflowAction.ADMIN_SEND_TO_ENQUIRY.name())
                .familyId(familyId)
                .affectedMembers(members.size())
                .nextLevelRoleId(10)
                .applicationStatus(ApplicationStatus.REJECT_TO_ENQUIRY)
                .opType(OpType.REJECT_TO_ENQUIRY)
                .message("Previously rejected application routed back to Enquiry Officer queue.")
                .build();
    }
}

