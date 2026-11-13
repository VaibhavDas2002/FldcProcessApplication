package workflow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import exception.EligibilityException;
import security.UserPrincipal;
import reqDTO.BatchMemberVerificationRequest;
import reqDTO.MemberVerificationItem;
import entity.Family;
import entity.FamilyMember;
import repository.FamilyMemberRepository;
import repository.FamilyRepository;
import service.AuditTrailService;
import service.impl.WorkflowExecutionServiceImpl;
import workflow.WorkflowStateMachine;

public class EligibilityValidationTest {

    private FamilyRepository mockFamilyRepo;
    private FamilyMemberRepository mockMemberRepo;
    private AuditTrailService mockAuditService;
    private WorkflowExecutionServiceImpl workflowService;

    private UserPrincipal testUser;
    private final UUID familyId = UUID.randomUUID();
    private final UUID memberId = UUID.randomUUID();
    private final int districtCode = 304;

    @BeforeEach
    void setUp() {
        mockFamilyRepo = mock(FamilyRepository.class);
        mockMemberRepo = mock(FamilyMemberRepository.class);
        mockAuditService = mock(AuditTrailService.class);

        workflowService = new WorkflowExecutionServiceImpl(
                mockFamilyRepo, mockMemberRepo, mockAuditService, new WorkflowStateMachine());

        testUser = UserPrincipal.builder()
                .userId(1L)
                .districtCode(districtCode)
                .build();

        Family family = Family.builder()
                .id(familyId)
                .lgdDistrictCode(districtCode)
                .createMode("O")
                .nextLevelRoleId(18)
                .build();

        when(mockFamilyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode))
                .thenReturn(Optional.of(family));
    }

    @Test
    @DisplayName("Fails eligibility when gender is Male ('M')")
    void testFailEligibilityMale() {
        FamilyMember male = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(18)
                .gender("M") // MALE
                .age(35)
                .applyingForAnnapurnaBhandar(true)
                .build();

        when(mockMemberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode))
                .thenReturn(Optional.of(male));

        BatchMemberVerificationRequest request = BatchMemberVerificationRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .members(List.of(MemberVerificationItem.builder().memberId(memberId).action("VERIFY").build()))
                .build();

        EligibilityException ex = assertThrows(EligibilityException.class, () ->
                workflowService.verifierConfirmVerification(request, testUser, "127.0.0.1"));
        assertTrue(ex.getMessage().contains("Female"));
    }

    @Test
    @DisplayName("Fails eligibility when applying_for_annapurna_bhandar is false")
    void testFailEligibilityNotApplying() {
        FamilyMember notApplying = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(18)
                .gender("F")
                .age(30)
                .applyingForAnnapurnaBhandar(false) // FALSE
                .build();

        when(mockMemberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode))
                .thenReturn(Optional.of(notApplying));

        BatchMemberVerificationRequest request = BatchMemberVerificationRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .members(List.of(MemberVerificationItem.builder().memberId(memberId).action("VERIFY").build()))
                .build();

        EligibilityException ex = assertThrows(EligibilityException.class, () ->
                workflowService.verifierConfirmVerification(request, testUser, "127.0.0.1"));
        assertTrue(ex.getMessage().contains("applying_for_annapurna_bhandar"));
    }

    @Test
    @DisplayName("Fails eligibility when age is under 25 or over 60")
    void testFailEligibilityAgeBounds() {
        // Age 24 (too young)
        FamilyMember young = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(18)
                .gender("F")
                .age(24) // UNDER 25
                .applyingForAnnapurnaBhandar(true)
                .build();

        when(mockMemberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode))
                .thenReturn(Optional.of(young));

        BatchMemberVerificationRequest req1 = BatchMemberVerificationRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .members(List.of(MemberVerificationItem.builder().memberId(memberId).action("VERIFY").build()))
                .build();

        EligibilityException ex1 = assertThrows(EligibilityException.class, () ->
                workflowService.verifierConfirmVerification(req1, testUser, "127.0.0.1"));
        assertTrue(ex1.getMessage().contains("between 25 and 60"));

        // Age 61 (too old)
        FamilyMember old = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(18)
                .gender("F")
                .age(61) // OVER 60
                .applyingForAnnapurnaBhandar(true)
                .build();

        when(mockMemberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode))
                .thenReturn(Optional.of(old));

        EligibilityException ex2 = assertThrows(EligibilityException.class, () ->
                workflowService.verifierConfirmVerification(req1, testUser, "127.0.0.1"));
        assertTrue(ex2.getMessage().contains("between 25 and 60"));
    }

    @Test
    @DisplayName("Passes eligibility when Female, Age 25-60, AY=true, Role=18")
    void testPassEligibility() {
        FamilyMember eligible = FamilyMember.builder()
                .id(memberId)
                .familyId(familyId)
                .lgdDistrictCode(districtCode)
                .nextLevelRoleId(18)
                .gender("F")
                .age(42) // OK
                .applyingForAnnapurnaBhandar(true) // OK
                .build();

        when(mockMemberRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(memberId, districtCode))
                .thenReturn(Optional.of(eligible));

        BatchMemberVerificationRequest req = BatchMemberVerificationRequest.builder()
                .familyId(familyId)
                .districtCode(districtCode)
                .members(List.of(MemberVerificationItem.builder().memberId(memberId).action("VERIFY").build()))
                .build();

        assertDoesNotThrow(() -> workflowService.verifierConfirmVerification(req, testUser, "127.0.0.1"));
    }
}

