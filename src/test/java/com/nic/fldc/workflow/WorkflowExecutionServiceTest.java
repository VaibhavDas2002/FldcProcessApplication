package com.nic.fldc.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nic.fldc.common.exception.InvalidWorkflowTransitionException;
import com.nic.fldc.common.security.UserPrincipal;
import com.nic.fldc.workflow.dto.request.BulkApproveRequest;
import com.nic.fldc.workflow.dto.request.EnquiryActionRequest;
import com.nic.fldc.workflow.dto.request.FamilyActionRequest;
import com.nic.fldc.workflow.dto.request.HoldActionRequest;
import com.nic.fldc.workflow.dto.request.SubmitApplicationRequest;
import com.nic.fldc.workflow.dto.response.WorkflowActionResponse;
import com.nic.fldc.workflow.entity.Family;
import com.nic.fldc.workflow.entity.FamilyMember;
import com.nic.fldc.workflow.repository.FamilyMemberRepository;
import com.nic.fldc.workflow.repository.FamilyRepository;
import com.nic.fldc.workflow.service.AuditTrailService;
import com.nic.fldc.workflow.service.impl.WorkflowExecutionServiceImpl;
import com.nic.fldc.workflow.statemachine.ApplicationStatus;
import com.nic.fldc.workflow.statemachine.OpType;
import com.nic.fldc.workflow.statemachine.WorkflowStateMachine;

public class WorkflowExecutionServiceTest {

    private FamilyRepository mockFamilyRepo;
    private FamilyMemberRepository mockMemberRepo;
    private AuditTrailService mockAuditService;
    private WorkflowExecutionServiceImpl executionService;

    private UserPrincipal testUser;
    private final UUID familyId = UUID.randomUUID();
    private final UUID memberId = UUID.randomUUID();
    private final int districtCode = 304;

    @BeforeEach
    void setUp() {
        mockFamilyRepo = mock(FamilyRepository.class);
        mockMemberRepo = mock(FamilyMemberRepository.class);
        mockAuditService = mock(AuditTrailService.class);

        executionService = new WorkflowExecutionServiceImpl(
                mockFamilyRepo, mockMemberRepo, mockAuditService, new WorkflowStateMachine());

        testUser = UserPrincipal.builder()
                .userId(501L)
                .username("officer_user")
                .districtCode(districtCode)
                .build();
    }

    @Test
    @DisplayName("Submit Application: role 0 -> 10, SUBMITTED audit log")
    void testSubmitApplication() {
        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .createMode("O")
                .nextLevelRoleId(0)
                .applicationStatus(ApplicationStatus.DRAFT)
                .build();

        FamilyMember member = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(0)
                .build();

        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        when(mockMemberRepo.findByFamilyIdAndIsDeletedFalse(familyId))
                .thenReturn(List.of(member));

        SubmitApplicationRequest req = SubmitApplicationRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .isAgreed(true)
                .build();

        WorkflowActionResponse response = executionService.submitApplication(req, testUser, "10.0.0.1");

        assertNotNull(response);
        assertEquals(10, family.getNextLevelRoleId());
        assertEquals(10, member.getNextLevelRoleId());
        assertEquals(ApplicationStatus.SUBMITTED, family.getApplicationStatus());
        verify(mockAuditService, times(1)).logFamilyAction(
                eq(familyId), eq(districtCode), eq(OpType.SUBMITTED), any(), anyString(), anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Enquiry Recommend Accept: role 10 -> 15, ENQUIRY_PROCESSED audit log")
    void testEnquiryRecommendAccept() {
        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .createMode("O")
                .nextLevelRoleId(10)
                .build();

        FamilyMember member = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(10)
                .build();

        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        when(mockMemberRepo.findNonApprovedMembers(familyId, 30))
                .thenReturn(List.of(member));

        EnquiryActionRequest req = EnquiryActionRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .remarks("Enquiry verified OK")
                .build();

        WorkflowActionResponse response = executionService.enquiryRecommendAccept(req, testUser, "10.0.0.1");

        assertEquals(15, family.getNextLevelRoleId());
        assertEquals(15, member.getNextLevelRoleId());
        assertEquals(ApplicationStatus.ENQUIRY_PROCESSED, family.getApplicationStatus());
        verify(mockAuditService, times(1)).logFamilyAction(
                eq(familyId), eq(districtCode), eq(OpType.ENQUIRY_PROCESSED), any(), anyString(), anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Enquiry Recommend Reject: role 10 -> 13, ENQUIRY_REJECTED audit log")
    void testEnquiryRecommendReject() {
        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .createMode("O")
                .nextLevelRoleId(10)
                .build();

        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        when(mockMemberRepo.findNonApprovedMembers(familyId, 30))
                .thenReturn(List.of());

        EnquiryActionRequest req = EnquiryActionRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .remarks("Income exceeds threshold")
                .build();

        executionService.enquiryRecommendReject(req, testUser, "10.0.0.1");

        assertEquals(13, family.getNextLevelRoleId());
        assertEquals(ApplicationStatus.ENQUIRY_REJECTED, family.getApplicationStatus());
        verify(mockAuditService, times(1)).logFamilyAction(
                eq(familyId), eq(districtCode), eq(OpType.ENQUIRY_REJECTED), any(), anyString(), anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Verifier Accept Family: role 15 -> 18, ACCEPT_FAMILY audit log")
    void testVerifierAcceptFamily() {
        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .createMode("O")
                .nextLevelRoleId(15)
                .build();

        FamilyMember member = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(15)
                .build();

        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        when(mockMemberRepo.findNonApprovedMembers(familyId, 30))
                .thenReturn(List.of(member));

        FamilyActionRequest req = FamilyActionRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .remarks("Family accepted for verification")
                .build();

        executionService.verifierAcceptFamily(req, testUser, "10.0.0.1");

        assertEquals(18, family.getNextLevelRoleId());
        assertEquals(18, member.getNextLevelRoleId());
        assertEquals(ApplicationStatus.ACCEPTED, family.getApplicationStatus());
        verify(mockAuditService, times(1)).logFamilyAction(
                eq(familyId), eq(districtCode), eq(OpType.ACCEPT_FAMILY), any(), anyString(), anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Verifier Recommend for Enquiry: requires min 5 characters remarks")
    void testVerifierRecommendEnquiryRemarksValidation() {
        FamilyActionRequest shortReq = FamilyActionRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .remarks("bad") // Only 3 chars
                .build();

        assertThrows(InvalidWorkflowTransitionException.class, () ->
                executionService.verifierRecommendEnquiry(shortReq, testUser, "10.0.0.1"));

        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(15)
                .build();

        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        when(mockMemberRepo.findNonApprovedMembers(familyId, 30))
                .thenReturn(List.of());

        FamilyActionRequest validReq = FamilyActionRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .remarks("Ration card mismatch observed") // > 5 chars
                .build();

        executionService.verifierRecommendEnquiry(validReq, testUser, "10.0.0.1");

        assertEquals(11, family.getNextLevelRoleId());
        assertEquals(ApplicationStatus.SENT_FOR_ENQUIRY, family.getApplicationStatus());
        verify(mockAuditService, times(1)).logFamilyAction(
                eq(familyId), eq(districtCode), eq(OpType.RECOMMEND_ENQUIRY), any(), anyString(), anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Verifier Hold: requires min 15 characters remarks")
    void testVerifierHoldRemarksValidation() {
        HoldActionRequest shortReq = HoldActionRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .remarks("Too short") // 9 chars < 15
                .build();

        assertThrows(InvalidWorkflowTransitionException.class, () ->
                executionService.verifierHoldFamily(shortReq, testUser, "10.0.0.1"));

        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(15)
                .build();

        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        when(mockMemberRepo.findNonApprovedMembers(familyId, 30))
                .thenReturn(List.of());

        HoldActionRequest validReq = HoldActionRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .remarks("Original bank passbook pending verification with branch manager")
                .build();

        executionService.verifierHoldFamily(validReq, testUser, "10.0.0.1");

        assertEquals(17, family.getNextLevelRoleId());
        assertEquals(ApplicationStatus.HELDUP_V, family.getApplicationStatus());
        verify(mockAuditService, times(1)).logFamilyAction(
                eq(familyId), eq(districtCode), eq(OpType.HELDUP_V), any(), anyString(), anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Approver: Single & Bulk Member Approval (role 20 -> 30, APPROVED)")
    void testApproverMemberApproval() {
        FamilyMember member = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(20)
                .build();

        when(mockMemberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode))
                .thenReturn(Optional.of(member));

        executionService.approverApproveMember(memberId, districtCode, testUser, "10.0.0.1");

        assertEquals(30, member.getNextLevelRoleId());
        assertEquals(ApplicationStatus.APPROVED, member.getApplicationStatus());
        verify(mockAuditService, times(1)).logMemberAction(
                eq(familyId), eq(memberId), eq(districtCode), eq(OpType.APPROVE), any(), anyString(), anyLong(), anyInt(), anyString());

        // Bulk approval
        UUID m2Id = UUID.randomUUID();
        FamilyMember member2 = FamilyMember.builder()
                .id(m2Id)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(20)
                .build();

        when(mockMemberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(m2Id, districtCode))
                .thenReturn(Optional.of(member2));

        BulkApproveRequest bulkReq = BulkApproveRequest.builder()
                .districtCode(districtCode)
                .memberIds(List.of(m2Id))
                .build();

        executionService.approverBulkApprove(bulkReq, testUser, "10.0.0.1");
        assertEquals(30, member2.getNextLevelRoleId());
    }

    @Test
    @DisplayName("Approver: Member Rejection (role 20 -> -102)")
    void testApproverRejectMember() {
        FamilyMember member = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(20)
                .build();

        when(mockMemberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode))
                .thenReturn(Optional.of(member));

        FamilyActionRequest req = FamilyActionRequest.builder()
                .districtCode(districtCode)
                .rejectRevertCauseId(10)
                .remarks("Exclusion criteria triggered")
                .build();

        executionService.approverRejectMember(req, memberId, testUser, "10.0.0.1");

        assertEquals(-102, member.getNextLevelRoleId());
        assertEquals(ApplicationStatus.REJECTED, member.getApplicationStatus());
        verify(mockAuditService, times(1)).logMemberAction(
                eq(familyId), eq(memberId), eq(districtCode), eq(OpType.REJECT), eq(10), anyString(), anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Approver: Revert cascades to Whole Family and Members (role 9 for Office, detail 99)")
    void testApproverRevertCascade() {
        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .createMode("O")
                .nextLevelRoleId(18)
                .build();

        FamilyMember verifiedMember = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(20)
                .build();

        when(mockMemberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode))
                .thenReturn(Optional.of(verifiedMember));
        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        when(mockMemberRepo.findNonApprovedMembers(familyId, 30))
                .thenReturn(List.of(verifiedMember));

        FamilyActionRequest req = FamilyActionRequest.builder()
                .districtCode(districtCode)
                .rejectRevertCauseId(5)
                .remarks("Approver discrepancies identified")
                .build();

        WorkflowActionResponse response = executionService.approverRevertCascade(req, memberId, testUser, "10.0.0.1");

        assertEquals(9, family.getNextLevelRoleId()); // Role 9 for Office
        assertEquals(99, family.getDetailStatus());
        assertEquals(ApplicationStatus.REVERTED, family.getApplicationStatus());
        assertEquals(9, verifiedMember.getNextLevelRoleId());
        assertEquals(99, verifiedMember.getDetailStatus());
        assertEquals(ApplicationStatus.REVERTED, verifiedMember.getApplicationStatus());

        verify(mockAuditService, times(1)).logFamilyAction(
                eq(familyId), eq(districtCode), eq(OpType.REVERT_FAMILY), eq(5), anyString(), anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Admin: Re-Approve from Rejected (-101 -> 30, APPROVE)")
    void testAdminApproveFromReject() {
        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(18)
                .build();

        FamilyMember rejectedMember = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(-101)
                .build();

        when(mockMemberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode))
                .thenReturn(Optional.of(rejectedMember));
        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        when(mockMemberRepo.countByFamilyIdAndNextLevelRoleIdInAndIsDeletedFalse(eq(familyId), any()))
                .thenReturn(1L); // Non-rejected members exist

        UserPrincipal adminUser = UserPrincipal.builder()
                .userId(999L)
                .username("admin")
                .build();

        executionService.adminApproveFromReject(memberId, districtCode, adminUser, "10.0.0.1");

        assertEquals(30, rejectedMember.getNextLevelRoleId());
        assertEquals(ApplicationStatus.APPROVE, rejectedMember.getApplicationStatus());
        verify(mockAuditService, times(1)).logMemberAction(
                eq(familyId), eq(memberId), eq(districtCode), eq(OpType.APPROVE_FROM_REJECT), any(), anyString(), anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Admin: Send to Enquiry from Rejected (resets to role 10, 'REJECT TO ENQUIRY')")
    void testAdminSendToEnquiry() {
        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(-100)
                .build();

        FamilyMember m1 = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(-100)
                .build();

        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        when(mockMemberRepo.findByFamilyIdAndIsDeletedFalse(familyId))
                .thenReturn(List.of(m1));

        UserPrincipal adminUser = UserPrincipal.builder()
                .userId(999L)
                .username("admin")
                .build();

        executionService.adminSendToEnquiry(familyId, districtCode, "Re-enquiry instructed by District Collector", adminUser, "10.0.0.1");

        assertEquals(10, family.getNextLevelRoleId());
        assertEquals(ApplicationStatus.REJECT_TO_ENQUIRY, family.getApplicationStatus());
        assertEquals(10, m1.getNextLevelRoleId());
        assertEquals(ApplicationStatus.REJECT_TO_ENQUIRY, m1.getApplicationStatus());
        verify(mockAuditService, times(1)).logMemberAction(
                eq(familyId), eq(memberId), eq(districtCode), eq(OpType.REJECT_TO_ENQUIRY), any(), anyString(), anyLong(), anyInt(), anyString());
    }
}

