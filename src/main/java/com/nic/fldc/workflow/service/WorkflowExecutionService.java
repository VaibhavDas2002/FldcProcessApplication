package com.nic.fldc.workflow.service;

import java.util.UUID;

import com.nic.fldc.common.security.UserPrincipal;
import com.nic.fldc.workflow.dto.request.BatchMemberVerificationRequest;
import com.nic.fldc.workflow.dto.request.BulkApproveRequest;
import com.nic.fldc.workflow.dto.request.EnquiryActionRequest;
import com.nic.fldc.workflow.dto.request.FamilyActionRequest;
import com.nic.fldc.workflow.dto.request.HoldActionRequest;
import com.nic.fldc.workflow.dto.request.SubmitApplicationRequest;
import com.nic.fldc.workflow.dto.response.WorkflowActionResponse;

public interface WorkflowExecutionService {

    // ── Citizen & Operator ──────────────────────────────────────────────────
    WorkflowActionResponse submitApplication(SubmitApplicationRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse resubmitApplication(SubmitApplicationRequest request, UserPrincipal user, String ipAddress);

    // ── Enquiry Officer ─────────────────────────────────────────────────────
    WorkflowActionResponse enquiryRecommendAccept(EnquiryActionRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse enquiryRecommendReject(EnquiryActionRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse enquiryRevert(EnquiryActionRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse enquiryEditSave(EnquiryActionRequest request, UserPrincipal user, String ipAddress);

    // ── Verifier Family Actions ─────────────────────────────────────────────
    WorkflowActionResponse verifierAcceptFamily(FamilyActionRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse verifierRejectFamily(FamilyActionRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse verifierRevertFamily(FamilyActionRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse verifierRecommendEnquiry(FamilyActionRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse verifierHoldFamily(HoldActionRequest request, UserPrincipal user, String ipAddress);

    // ── Verifier Member Actions ─────────────────────────────────────────────
    WorkflowActionResponse verifierConfirmVerification(BatchMemberVerificationRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse verifierRecommendRejectApproved(UUID memberId, Integer districtCode, String remarks, UserPrincipal user, String ipAddress);

    // ── Approver Actions ────────────────────────────────────────────────────
    WorkflowActionResponse approverApproveMember(UUID memberId, Integer districtCode, UserPrincipal user, String ipAddress);
    WorkflowActionResponse approverBulkApprove(BulkApproveRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse approverRejectMember(FamilyActionRequest request, UUID memberId, UserPrincipal user, String ipAddress);
    WorkflowActionResponse approverRevertCascade(FamilyActionRequest request, UUID memberId, UserPrincipal user, String ipAddress);
    WorkflowActionResponse approverHoldFamily(HoldActionRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse approverHoldSingleMember(HoldActionRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse approverTempHold(HoldActionRequest request, UserPrincipal user, String ipAddress);
    WorkflowActionResponse approverRejectHoldMember(FamilyActionRequest request, UUID memberId, UserPrincipal user, String ipAddress);
    WorkflowActionResponse approverRejectRecommended(FamilyActionRequest request, UUID memberId, UserPrincipal user, String ipAddress);

    // ── Administrative Re-Processing ────────────────────────────────────────
    WorkflowActionResponse adminApproveFromReject(UUID memberId, Integer districtCode, UserPrincipal user, String ipAddress);
    WorkflowActionResponse adminSendToEnquiry(UUID familyId, Integer districtCode, String remarks, UserPrincipal user, String ipAddress);
}

