package statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import exception.InvalidWorkflowTransitionException;
import security.WorkflowRole;
import workflow.ApplicationStatus;
import workflow.NextLevelRoleId;
import workflow.OpType;
import workflow.WorkflowAction;
import workflow.WorkflowStateMachine;
import workflow.WorkflowTransition;

public class WorkflowStateMachineTest {

    private WorkflowStateMachine stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine = new WorkflowStateMachine();
    }

    @Test
    @DisplayName("Citizen Submit: Draft (0) -> Submitted (10)")
    void testCitizenSubmitTransition() {
        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_CITIZEN, WorkflowAction.SUBMIT, 0);
        assertNotNull(transition);
        assertEquals(NextLevelRoleId.FINAL_SUBMISSION, transition.getTargetFamilyRoleId());
        assertEquals(ApplicationStatus.SUBMITTED, transition.getTargetFamilyStatus());
        assertEquals(OpType.SUBMITTED, transition.getOpType());
    }

    @Test
    @DisplayName("Operator Submit: Draft (0) -> Submitted (10)")
    void testOperatorSubmitTransition() {
        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_OPERATOR, WorkflowAction.SUBMIT, 0);
        assertNotNull(transition);
        assertEquals(NextLevelRoleId.FINAL_SUBMISSION, transition.getTargetFamilyRoleId());
        assertEquals(ApplicationStatus.SUBMITTED, transition.getTargetFamilyStatus());
    }

    @Test
    @DisplayName("Citizen Resubmit from Revert (4, 5, 6)")
    void testCitizenResubmitTransitions() {
        for (int roleId : new int[]{4, 5, 6}) {
            WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_CITIZEN, WorkflowAction.RESUBMIT, roleId);
            assertNotNull(transition);
            assertEquals(NextLevelRoleId.FINAL_SUBMISSION, transition.getTargetFamilyRoleId());
        }
    }

    @Test
    @DisplayName("Operator Resubmit from Revert (7, 8, 9)")
    void testOperatorResubmitTransitions() {
        for (int roleId : new int[]{7, 8, 9}) {
            WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_OPERATOR, WorkflowAction.RESUBMIT, roleId);
            assertNotNull(transition);
            assertEquals(NextLevelRoleId.FINAL_SUBMISSION, transition.getTargetFamilyRoleId());
        }
    }

    @Test
    @DisplayName("Enquiry Officer: Recommend Accept (10, 11 -> 15)")
    void testEnquiryRecommendAccept() {
        for (int sourceRole : new int[]{10, 11}) {
            WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_ENQUIRY_OFFICER,
                    WorkflowAction.ENQUIRY_RECOMMEND_ACCEPT, sourceRole);
            assertNotNull(transition);
            assertEquals(NextLevelRoleId.RECOMMEND_FOR_ACCEPT_ENQUIRY, transition.getTargetFamilyRoleId());
            assertEquals(ApplicationStatus.ENQUIRY_PROCESSED, transition.getTargetFamilyStatus());
            assertEquals(OpType.ENQUIRY_PROCESSED, transition.getOpType());
        }
    }

    @Test
    @DisplayName("Enquiry Officer: Recommend Reject (10, 11 -> 13)")
    void testEnquiryRecommendReject() {
        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_ENQUIRY_OFFICER,
                WorkflowAction.ENQUIRY_RECOMMEND_REJECT, 10);
        assertNotNull(transition);
        assertEquals(NextLevelRoleId.RECOMMEND_FOR_REJECTION_ENQUIRY, transition.getTargetFamilyRoleId());
        assertEquals(ApplicationStatus.ENQUIRY_REJECTED, transition.getTargetFamilyStatus());
    }

    @Test
    @DisplayName("Verifier: Accept Family (13, 15 -> 18)")
    void testVerifierAcceptFamily() {
        for (int source : new int[]{13, 15}) {
            WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER,
                    WorkflowAction.VERIFIER_ACCEPT_FAMILY, source);
            assertNotNull(transition);
            assertEquals(NextLevelRoleId.ACCEPT_APPLICATION_VERIFIER, transition.getTargetFamilyRoleId());
            assertEquals(ApplicationStatus.ACCEPTED, transition.getTargetFamilyStatus());
            assertEquals(OpType.ACCEPT_FAMILY, transition.getOpType());
        }
    }

    @Test
    @DisplayName("Verifier: Reject Family (13, 15 -> -100)")
    void testVerifierRejectFamily() {
        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER,
                WorkflowAction.VERIFIER_REJECT_FAMILY, 15);
        assertNotNull(transition);
        assertEquals(NextLevelRoleId.REJECT_APPLICATION_VERIFIER, transition.getTargetFamilyRoleId());
        assertEquals(ApplicationStatus.REJECTED, transition.getTargetFamilyStatus());
        assertEquals(OpType.REJECT_FAMILY, transition.getOpType());
    }

    @Test
    @DisplayName("Verifier: Recommend for Enquiry (13, 15 -> 11 with min 5 chars)")
    void testVerifierRecommendEnquiry() {
        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER,
                WorkflowAction.VERIFIER_RECOMMEND_ENQUIRY, 15);
        assertNotNull(transition);
        assertEquals(NextLevelRoleId.RECOMMEND_FOR_ENQUIRY_VERIFIER, transition.getTargetFamilyRoleId());
        assertEquals(5, transition.getMinRemarksLength());
        assertEquals(OpType.RECOMMEND_ENQUIRY, transition.getOpType());
    }

    @Test
    @DisplayName("Verifier: Hold Family (13, 15 -> 17 with min 15 chars)")
    void testVerifierHoldFamily() {
        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER,
                WorkflowAction.VERIFIER_HOLD_FAMILY, 15);
        assertNotNull(transition);
        assertEquals(NextLevelRoleId.VERIFIER_HOLD, transition.getTargetFamilyRoleId());
        assertEquals(15, transition.getMinRemarksLength());
        assertEquals(OpType.HELDUP_V, transition.getOpType());
    }

    @Test
    @DisplayName("Verifier: Verify Member (18 -> 20)")
    void testVerifierVerifyMember() {
        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER,
                WorkflowAction.VERIFIER_VERIFY_MEMBER, 18);
        assertNotNull(transition);
        assertEquals(NextLevelRoleId.VERIFY_MEMBER_VERIFIER, transition.getTargetMemberRoleId());
        assertEquals(ApplicationStatus.VERIFIED, transition.getTargetMemberStatus());
        assertEquals(OpType.VERIFY, transition.getOpType());
    }

    @Test
    @DisplayName("Approver: Approve Member (20 -> 30)")
    void testApproverApproveMember() {
        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_APPROVER,
                WorkflowAction.APPROVER_APPROVE_MEMBER, 20);
        assertNotNull(transition);
        assertEquals(NextLevelRoleId.APPROVE_MEMBER_APPROVER, transition.getTargetMemberRoleId());
        assertEquals(ApplicationStatus.APPROVED, transition.getTargetMemberStatus());
        assertEquals(OpType.APPROVE, transition.getOpType());
    }

    @Test
    @DisplayName("Approver: Reject Member (20 -> -102)")
    void testApproverRejectMember() {
        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_APPROVER,
                WorkflowAction.APPROVER_REJECT_MEMBER, 20);
        assertNotNull(transition);
        assertEquals(NextLevelRoleId.REJECT_MEMBER_APPROVER, transition.getTargetMemberRoleId());
        assertEquals(ApplicationStatus.REJECTED, transition.getTargetMemberStatus());
        assertEquals(OpType.REJECT, transition.getOpType());
    }

    @Test
    @DisplayName("Approver: Hold (20 -> 26 with min 15 chars)")
    void testApproverHoldTransitions() {
        WorkflowTransition familyHold = stateMachine.getTransition(WorkflowRole.ROLE_APPROVER,
                WorkflowAction.APPROVER_HOLD_FAMILY, 20);
        assertNotNull(familyHold);
        assertEquals(15, familyHold.getMinRemarksLength());
        assertEquals(OpType.HELDUP_A, familyHold.getOpType());

        WorkflowTransition memberHold = stateMachine.getTransition(WorkflowRole.ROLE_APPROVER,
                WorkflowAction.APPROVER_HOLD_SINGLE_MEMBER, 20);
        assertNotNull(memberHold);
        assertEquals(15, memberHold.getMinRemarksLength());
        assertEquals(OpType.HELDUP_M_A, memberHold.getOpType());
    }

    @Test
    @DisplayName("Admin: Re-Approve from Rejected (-100, -101, -102, -103 -> 30)")
    void testAdminReApproveFromReject() {
        for (int rejectedRole : new int[]{-100, -101, -102, -103}) {
            WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_ADMIN,
                    WorkflowAction.ADMIN_APPROVE_FROM_REJECT, rejectedRole);
            assertNotNull(transition);
            assertEquals(NextLevelRoleId.APPROVE_MEMBER_APPROVER, transition.getTargetMemberRoleId());
            assertEquals(ApplicationStatus.APPROVE, transition.getTargetMemberStatus());
        }
    }

    @Test
    @DisplayName("Admin: Send to Enquiry from Rejected (-100, -101, -102, -103 -> 10)")
    void testAdminSendToEnquiry() {
        WorkflowTransition transition = stateMachine.getTransition(WorkflowRole.ROLE_ADMIN,
                WorkflowAction.ADMIN_SEND_TO_ENQUIRY, -101);
        assertNotNull(transition);
        assertEquals(NextLevelRoleId.FINAL_SUBMISSION, transition.getTargetFamilyRoleId());
        assertEquals(ApplicationStatus.REJECT_TO_ENQUIRY, transition.getTargetFamilyStatus());
    }

    @Test
    @DisplayName("Invalid Transitions: Reject unauthorized role and invalid states")
    void testInvalidTransitionsThrowException() {
        // Approver cannot execute citizen submit
        assertThrows(InvalidWorkflowTransitionException.class, () ->
                stateMachine.getTransition(WorkflowRole.ROLE_APPROVER, WorkflowAction.SUBMIT, 0));

        // Verifier cannot verify a draft application (0)
        assertThrows(InvalidWorkflowTransitionException.class, () ->
                stateMachine.getTransition(WorkflowRole.ROLE_VERIFIER, WorkflowAction.VERIFIER_VERIFY_MEMBER, 0));

        // Approver cannot approve a member who is not verified (role 10)
        assertThrows(InvalidWorkflowTransitionException.class, () ->
                stateMachine.getTransition(WorkflowRole.ROLE_APPROVER, WorkflowAction.APPROVER_APPROVE_MEMBER, 10));
    }

    @Test
    @DisplayName("Master Role ID Enum Validation")
    void testNextLevelRoleIdCompleteness() {
        // Verify key roles from reference table
        assertEquals(0, NextLevelRoleId.DRAFT_ENTRY.getCode());
        assertEquals(10, NextLevelRoleId.FINAL_SUBMISSION.getCode());
        assertEquals(18, NextLevelRoleId.ACCEPT_APPLICATION_VERIFIER.getCode());
        assertEquals(20, NextLevelRoleId.VERIFY_MEMBER_VERIFIER.getCode());
        assertEquals(30, NextLevelRoleId.APPROVE_MEMBER_APPROVER.getCode());
        assertEquals(-100, NextLevelRoleId.REJECT_APPLICATION_VERIFIER.getCode());
        assertEquals(-101, NextLevelRoleId.REJECT_ACCEPTED_MEMBER_VERIFIER.getCode());
        assertEquals(-102, NextLevelRoleId.REJECT_MEMBER_APPROVER.getCode());
        assertEquals(-103, NextLevelRoleId.REJECT_APPROVED_RECOMMENDED_APPROVER.getCode());
        assertEquals(-104, NextLevelRoleId.REJECT_HOLD_MEMBER_APPROVER.getCode());
        assertEquals(-106, NextLevelRoleId.REJECT_PRIORITY_REVERIFICATION.getCode());

        assertTrue(NextLevelRoleId.fromCode(8).isRevert());
        assertTrue(NextLevelRoleId.fromCode(-101).isNegativeReject());
        assertTrue(NextLevelRoleId.fromCode(30).isApproved());
        assertFalse(NextLevelRoleId.fromCode(18).isApproved());
    }
}

