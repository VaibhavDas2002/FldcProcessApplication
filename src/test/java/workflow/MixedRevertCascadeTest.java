package workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import security.UserPrincipal;
import reqDTO.BatchMemberVerificationRequest;
import reqDTO.MemberVerificationItem;
import resDTO.WorkflowActionResponse;
import entity.Family;
import entity.FamilyMember;
import repository.FamilyMemberRepository;
import repository.FamilyRepository;
import service.AuditTrailService;
import service.impl.WorkflowExecutionServiceImpl;
import workflow.ApplicationStatus;
import workflow.OpType;
import workflow.WorkflowStateMachine;

public class MixedRevertCascadeTest {

    private FamilyRepository mockFamilyRepo;
    private FamilyMemberRepository mockMemberRepo;
    private AuditTrailService mockAuditService;
    private WorkflowStateMachine stateMachine;
    private WorkflowExecutionServiceImpl workflowService;

    private UserPrincipal testUser;
    private final UUID familyId = UUID.randomUUID();
    private final UUID member1Id = UUID.randomUUID();
    private final UUID member2Id = UUID.randomUUID();
    private final UUID approvedMemberId = UUID.randomUUID();
    private final int districtCode = 304;

    @BeforeEach
    void setUp() {
        mockFamilyRepo = mock(FamilyRepository.class);
        mockMemberRepo = mock(FamilyMemberRepository.class);
        mockAuditService = mock(AuditTrailService.class);
        stateMachine = new WorkflowStateMachine();

        workflowService = new WorkflowExecutionServiceImpl(
                mockFamilyRepo, mockMemberRepo, mockAuditService, stateMachine);

        testUser = UserPrincipal.builder()
                .userId(101L)
                .username("verifier_user")
                .districtCode(districtCode)
                .build();
    }

    @Test
    @DisplayName("Office Entry: Mixed Revert triggers Cascade to Role 8 and detail_status = 99")
    void testOfficeMixedRevertCascade() {
        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .createMode("O")
                .nextLevelRoleId(18) // Accepted
                .applicationStatus(ApplicationStatus.ACCEPTED)
                .build();

        FamilyMember m1 = FamilyMember.builder()
                .id(member1Id)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(18)
                .applicationStatus(ApplicationStatus.ACCEPTED)
                .gender("F")
                .age(35)
                .applyingForAnnapurnaBhandar(true)
                .build();

        FamilyMember m2 = FamilyMember.builder()
                .id(member2Id)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(18)
                .applicationStatus(ApplicationStatus.ACCEPTED)
                .gender("F")
                .age(40)
                .applyingForAnnapurnaBhandar(true)
                .build();

        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        when(mockMemberRepo.findNonApprovedMembers(familyId, 30))
                .thenReturn(List.of(m1, m2));

        // Member 1 is VERIFY, Member 2 is REVERT (Mixed selection)
        BatchMemberVerificationRequest request = BatchMemberVerificationRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .members(List.of(
                        MemberVerificationItem.builder().memberId(member1Id).action("VERIFY").build(),
                        MemberVerificationItem.builder().memberId(member2Id).action("REVERT").build()
                ))
                .build();

        WorkflowActionResponse response = workflowService.verifierConfirmVerification(request, testUser, "127.0.0.1");

        // Verify cascade occurred
        assertTrue(response.isMixedRevertCascaded());
        assertEquals(8, response.getNextLevelRoleId()); // Role 8 for Office
        assertEquals(ApplicationStatus.REVERTED, response.getApplicationStatus());

        // Verify family updated to 8 and detail_status 99
        assertEquals(8, family.getNextLevelRoleId());
        assertEquals(ApplicationStatus.REVERTED, family.getApplicationStatus());
        assertEquals(99, family.getDetailStatus());

        // Verify members updated to 8 and detail_status 99
        assertEquals(8, m1.getNextLevelRoleId());
        assertEquals(99, m1.getDetailStatus());
        assertEquals(8, m2.getNextLevelRoleId());
        assertEquals(99, m2.getDetailStatus());

        // Verify audit trail logged
        verify(mockAuditService, times(1)).logFamilyAction(
                eq(familyId), eq(districtCode), eq(OpType.REVERT_FAMILY), any(), anyString(), anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Citizen Entry: Mixed Revert triggers Cascade to Role 6 and detail_status = 99")
    void testCitizenMixedRevertCascade() {
        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .createMode("C")
                .nextLevelRoleId(18)
                .applicationStatus(ApplicationStatus.ACCEPTED)
                .build();

        FamilyMember m1 = FamilyMember.builder()
                .id(member1Id)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(18)
                .gender("F")
                .age(30)
                .applyingForAnnapurnaBhandar(true)
                .build();

        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        when(mockMemberRepo.findNonApprovedMembers(familyId, 30))
                .thenReturn(List.of(m1));

        BatchMemberVerificationRequest request = BatchMemberVerificationRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .members(List.of(
                        MemberVerificationItem.builder().memberId(member1Id).action("REVERT").build()
                ))
                .build();

        WorkflowActionResponse response = workflowService.verifierConfirmVerification(request, testUser, "127.0.0.1");

        assertTrue(response.isMixedRevertCascaded());
        assertEquals(6, response.getNextLevelRoleId()); // Role 6 for Citizen
        assertEquals(ApplicationStatus.REVERTED, response.getApplicationStatus());
        assertEquals(6, family.getNextLevelRoleId());
        assertEquals(99, family.getDetailStatus());
        assertEquals(6, m1.getNextLevelRoleId());
        assertEquals(99, m1.getDetailStatus());
    }

    @Test
    @DisplayName("Approved members (role 30) are NEVER reverted by cascade")
    void testApprovedMembersNeverReverted() {
        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .createMode("O")
                .nextLevelRoleId(18)
                .build();

        FamilyMember nonApproved = FamilyMember.builder()
                .id(member1Id)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(18)
                .build();

        FamilyMember alreadyApproved = FamilyMember.builder()
                .id(approvedMemberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(30) // APPROVED
                .applicationStatus(ApplicationStatus.APPROVED)
                .build();

        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
        // Repository excludes role 30
        when(mockMemberRepo.findNonApprovedMembers(familyId, 30))
                .thenReturn(List.of(nonApproved));

        BatchMemberVerificationRequest request = BatchMemberVerificationRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .members(List.of(
                        MemberVerificationItem.builder().memberId(member1Id).action("REVERT").build()
                ))
                .build();

        workflowService.verifierConfirmVerification(request, testUser, "127.0.0.1");

        assertEquals(8, nonApproved.getNextLevelRoleId());
        assertEquals(30, alreadyApproved.getNextLevelRoleId()); // Role 30 preserved!
        assertEquals(ApplicationStatus.APPROVED, alreadyApproved.getApplicationStatus());
    }
}

