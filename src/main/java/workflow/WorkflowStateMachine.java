package workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import exception.InvalidWorkflowTransitionException;
import security.WorkflowRole;

@Component
public class WorkflowStateMachine {

    private final List<WorkflowTransition> transitions;

    public WorkflowStateMachine() {
        List<WorkflowTransition> list = new ArrayList<>();

        // ── 1. Citizen Transitions ──────────────────────────────────────────
        // Fresh submit
        list.add(WorkflowTransition.builder()
                .transitionId("TR-CIT-SUBMIT")
                .requiredRole(WorkflowRole.ROLE_CITIZEN)
                .action(WorkflowAction.SUBMIT)
                .sourceRoleIds(Set.of(0))
                .targetFamilyRoleId(NextLevelRoleId.FINAL_SUBMISSION)
                .targetMemberRoleId(NextLevelRoleId.FINAL_SUBMISSION)
                .targetFamilyStatus(ApplicationStatus.SUBMITTED)
                .targetMemberStatus(ApplicationStatus.SUBMITTED)
                .opType(OpType.SUBMITTED)
                .minRemarksLength(0)
                .preservesApprovedMembers(true)
                .build());

        // Resubmit reverted application (roles 4, 5, 6)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-CIT-RESUBMIT")
                .requiredRole(WorkflowRole.ROLE_CITIZEN)
                .action(WorkflowAction.RESUBMIT)
                .sourceRoleIds(Set.of(4, 5, 6))
                .targetFamilyRoleId(NextLevelRoleId.FINAL_SUBMISSION)
                .targetMemberRoleId(NextLevelRoleId.FINAL_SUBMISSION)
                .targetFamilyStatus(ApplicationStatus.SUBMITTED)
                .targetMemberStatus(ApplicationStatus.SUBMITTED)
                .opType(OpType.SUBMITTED)
                .minRemarksLength(0)
                .preservesApprovedMembers(true)
                .build());

        // ── 2. Operator Transitions ─────────────────────────────────────────
        // Fresh submit
        list.add(WorkflowTransition.builder()
                .transitionId("TR-OP-SUBMIT")
                .requiredRole(WorkflowRole.ROLE_OPERATOR)
                .action(WorkflowAction.SUBMIT)
                .sourceRoleIds(Set.of(0))
                .targetFamilyRoleId(NextLevelRoleId.FINAL_SUBMISSION)
                .targetMemberRoleId(NextLevelRoleId.FINAL_SUBMISSION)
                .targetFamilyStatus(ApplicationStatus.SUBMITTED)
                .targetMemberStatus(ApplicationStatus.SUBMITTED)
                .opType(OpType.SUBMITTED)
                .minRemarksLength(0)
                .preservesApprovedMembers(true)
                .build());

        // Resubmit reverted application (roles 7, 8, 9)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-OP-RESUBMIT")
                .requiredRole(WorkflowRole.ROLE_OPERATOR)
                .action(WorkflowAction.RESUBMIT)
                .sourceRoleIds(Set.of(7, 8, 9))
                .targetFamilyRoleId(NextLevelRoleId.FINAL_SUBMISSION)
                .targetMemberRoleId(NextLevelRoleId.FINAL_SUBMISSION)
                .targetFamilyStatus(ApplicationStatus.SUBMITTED)
                .targetMemberStatus(ApplicationStatus.SUBMITTED)
                .opType(OpType.SUBMITTED)
                .minRemarksLength(0)
                .preservesApprovedMembers(true)
                .build());

        // ── 3. Enquiry Officer Transitions ──────────────────────────────────
        // Recommend Accept (role 10 or 11 -> 15)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-ENQ-ACCEPT")
                .requiredRole(WorkflowRole.ROLE_ENQUIRY_OFFICER)
                .action(WorkflowAction.ENQUIRY_RECOMMEND_ACCEPT)
                .sourceRoleIds(Set.of(10, 11))
                .targetFamilyRoleId(NextLevelRoleId.RECOMMEND_FOR_ACCEPT_ENQUIRY)
                .targetMemberRoleId(NextLevelRoleId.RECOMMEND_FOR_ACCEPT_ENQUIRY)
                .targetFamilyStatus(ApplicationStatus.ENQUIRY_PROCESSED)
                .targetMemberStatus(ApplicationStatus.ENQUIRY_PROCESSED)
                .opType(OpType.ENQUIRY_PROCESSED)
                .minRemarksLength(0)
                .preservesApprovedMembers(true)
                .build());

        // Recommend Reject (role 10 or 11 -> 13)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-ENQ-REJECT")
                .requiredRole(WorkflowRole.ROLE_ENQUIRY_OFFICER)
                .action(WorkflowAction.ENQUIRY_RECOMMEND_REJECT)
                .sourceRoleIds(Set.of(10, 11))
                .targetFamilyRoleId(NextLevelRoleId.RECOMMEND_FOR_REJECTION_ENQUIRY)
                .targetMemberRoleId(NextLevelRoleId.RECOMMEND_FOR_REJECTION_ENQUIRY)
                .targetFamilyStatus(ApplicationStatus.ENQUIRY_REJECTED)
                .targetMemberStatus(ApplicationStatus.ENQUIRY_REJECTED)
                .opType(OpType.ENQUIRY_REJECTED)
                .minRemarksLength(0)
                .preservesApprovedMembers(true)
                .build());

        // Enquiry Revert (handled with create_mode check in service: role 7 for Office, 5 for Citizen)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-ENQ-REVERT")
                .requiredRole(WorkflowRole.ROLE_ENQUIRY_OFFICER)
                .action(WorkflowAction.ENQUIRY_REVERT)
                .sourceRoleIds(Set.of(10, 11))
                .targetFamilyRoleId(NextLevelRoleId.REVERT_OFFICE_ENQUIRY) // or REVERT_CITIZEN_ENQUIRY based on create_mode
                .targetMemberRoleId(NextLevelRoleId.REVERT_OFFICE_ENQUIRY)
                .targetFamilyStatus(ApplicationStatus.ENQUIRY_REVERTED)
                .targetMemberStatus(ApplicationStatus.ENQUIRY_REVERTED)
                .opType(OpType.ENQUIRY_REVERTED)
                .minRemarksLength(0)
                .requireRejectRevertCause(true)
                .preservesApprovedMembers(true)
                .build());

        // Enquiry Edit & Save (role 10 or 11 -> 15)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-ENQ-EDIT-SAVE")
                .requiredRole(WorkflowRole.ROLE_ENQUIRY_OFFICER)
                .action(WorkflowAction.ENQUIRY_EDIT_SAVE)
                .sourceRoleIds(Set.of(10, 11))
                .targetFamilyRoleId(NextLevelRoleId.RECOMMEND_FOR_ACCEPT_ENQUIRY)
                .targetMemberRoleId(NextLevelRoleId.RECOMMEND_FOR_ACCEPT_ENQUIRY)
                .targetFamilyStatus(ApplicationStatus.ENQUIRY_PROCESSED)
                .targetMemberStatus(ApplicationStatus.ENQUIRY_PROCESSED)
                .opType(OpType.ENQUIRY_PROCESSED)
                .minRemarksLength(0)
                .preservesApprovedMembers(true)
                .build());

        // ── 4. Verifier Family-Level Transitions ────────────────────────────
        // Accept Application / Family (role 13 or 15 -> 18)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-VER-ACCEPT-FAMILY")
                .requiredRole(WorkflowRole.ROLE_VERIFIER)
                .action(WorkflowAction.VERIFIER_ACCEPT_FAMILY)
                .sourceRoleIds(Set.of(13, 15))
                .targetFamilyRoleId(NextLevelRoleId.ACCEPT_APPLICATION_VERIFIER)
                .targetMemberRoleId(NextLevelRoleId.ACCEPT_APPLICATION_VERIFIER)
                .targetFamilyStatus(ApplicationStatus.ACCEPTED)
                .targetMemberStatus(ApplicationStatus.ACCEPTED)
                .opType(OpType.ACCEPT_FAMILY)
                .minRemarksLength(0)
                .preservesApprovedMembers(true)
                .build());

        // Reject Family (role 13 or 15 -> -100)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-VER-REJECT-FAMILY")
                .requiredRole(WorkflowRole.ROLE_VERIFIER)
                .action(WorkflowAction.VERIFIER_REJECT_FAMILY)
                .sourceRoleIds(Set.of(13, 15))
                .targetFamilyRoleId(NextLevelRoleId.REJECT_APPLICATION_VERIFIER)
                .targetMemberRoleId(NextLevelRoleId.REJECT_APPLICATION_VERIFIER)
                .targetFamilyStatus(ApplicationStatus.REJECTED)
                .targetMemberStatus(ApplicationStatus.REJECTED)
                .opType(OpType.REJECT_FAMILY)
                .requireRejectRevertCause(true)
                .preservesApprovedMembers(true)
                .build());

        // Revert Family (role 13 or 15 -> 8 for Office, 6 for Citizen; detail_status = 99)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-VER-REVERT-FAMILY")
                .requiredRole(WorkflowRole.ROLE_VERIFIER)
                .action(WorkflowAction.VERIFIER_REVERT_FAMILY)
                .sourceRoleIds(Set.of(13, 15))
                .targetFamilyRoleId(NextLevelRoleId.REVERT_OFFICE_VERIFIER)
                .targetMemberRoleId(NextLevelRoleId.REVERT_OFFICE_VERIFIER)
                .targetFamilyStatus(ApplicationStatus.REVERTED)
                .targetMemberStatus(ApplicationStatus.REVERTED)
                .targetDetailStatus(99)
                .opType(OpType.REVERT_FAMILY)
                .requireRejectRevertCause(true)
                .preservesApprovedMembers(true)
                .build());

        // Recommend for Enquiry (role 13 or 15 -> 11, min remarks 5)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-VER-REC-ENQUIRY")
                .requiredRole(WorkflowRole.ROLE_VERIFIER)
                .action(WorkflowAction.VERIFIER_RECOMMEND_ENQUIRY)
                .sourceRoleIds(Set.of(13, 15))
                .targetFamilyRoleId(NextLevelRoleId.RECOMMEND_FOR_ENQUIRY_VERIFIER)
                .targetMemberRoleId(NextLevelRoleId.RECOMMEND_FOR_ENQUIRY_VERIFIER)
                .targetFamilyStatus(ApplicationStatus.SENT_FOR_ENQUIRY)
                .targetMemberStatus(ApplicationStatus.SENT_FOR_ENQUIRY)
                .opType(OpType.RECOMMEND_ENQUIRY)
                .minRemarksLength(5)
                .preservesApprovedMembers(true)
                .build());

        // Held-Up Family (role 13 or 15 -> 17, min remarks 15)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-VER-HOLD-FAMILY")
                .requiredRole(WorkflowRole.ROLE_VERIFIER)
                .action(WorkflowAction.VERIFIER_HOLD_FAMILY)
                .sourceRoleIds(Set.of(13, 15))
                .targetFamilyRoleId(NextLevelRoleId.VERIFIER_HOLD)
                .targetMemberRoleId(NextLevelRoleId.VERIFIER_HOLD)
                .targetFamilyStatus(ApplicationStatus.HELDUP_V)
                .targetMemberStatus(ApplicationStatus.HELDUP_V)
                .opType(OpType.HELDUP_V)
                .minRemarksLength(15)
                .preservesApprovedMembers(true)
                .build());

        // ── 5. Verifier Member-Level Transitions ────────────────────────────
        // Verify Eligible Member (fm: 18 -> 20; family remains 18)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-VER-VERIFY-MEMBER")
                .requiredRole(WorkflowRole.ROLE_VERIFIER)
                .action(WorkflowAction.VERIFIER_VERIFY_MEMBER)
                .sourceRoleIds(Set.of(18))
                .targetFamilyRoleId(NextLevelRoleId.ACCEPT_APPLICATION_VERIFIER)
                .targetMemberRoleId(NextLevelRoleId.VERIFY_MEMBER_VERIFIER)
                .targetFamilyStatus(ApplicationStatus.ACCEPTED)
                .targetMemberStatus(ApplicationStatus.VERIFIED)
                .opType(OpType.VERIFY)
                .preservesApprovedMembers(true)
                .build());

        // Reject Eligible Member (fm: 18 -> -101; family remains 18)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-VER-REJECT-MEMBER")
                .requiredRole(WorkflowRole.ROLE_VERIFIER)
                .action(WorkflowAction.VERIFIER_REJECT_MEMBER)
                .sourceRoleIds(Set.of(18))
                .targetFamilyRoleId(NextLevelRoleId.ACCEPT_APPLICATION_VERIFIER)
                .targetMemberRoleId(NextLevelRoleId.REJECT_ACCEPTED_MEMBER_VERIFIER)
                .targetFamilyStatus(ApplicationStatus.ACCEPTED)
                .targetMemberStatus(ApplicationStatus.REJECTED)
                .opType(OpType.REJECT)
                .requireRejectRevertCause(true)
                .preservesApprovedMembers(true)
                .build());

        // Revert Cascade (fm: 18 -> whole family & members cascade to 8/6, detail 99)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-VER-REVERT-CASCADE")
                .requiredRole(WorkflowRole.ROLE_VERIFIER)
                .action(WorkflowAction.VERIFIER_REVERT_MEMBER_CASCADE)
                .sourceRoleIds(Set.of(18))
                .targetFamilyRoleId(NextLevelRoleId.REVERT_OFFICE_VERIFIER)
                .targetMemberRoleId(NextLevelRoleId.REVERT_OFFICE_VERIFIER)
                .targetFamilyStatus(ApplicationStatus.REVERTED)
                .targetMemberStatus(ApplicationStatus.REVERTED)
                .targetDetailStatus(99)
                .opType(OpType.REVERT)
                .requireRejectRevertCause(true)
                .preservesApprovedMembers(true)
                .build());

        // Post-approval recommendation for rejection (fm: 30 -> 27)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-VER-REC-REJECT-APPROVED")
                .requiredRole(WorkflowRole.ROLE_VERIFIER)
                .action(WorkflowAction.VERIFIER_RECOMMEND_REJECT_APPROVED)
                .sourceRoleIds(Set.of(30))
                .targetMemberRoleId(NextLevelRoleId.RECOMMEND_APPROVED_REJECTION_VERIFIER)
                .targetMemberStatus(ApplicationStatus.RECOMMENDED_FOR_REJECTION)
                .opType(OpType.RECOMMENDED_FOR_REJECTION)
                .build());

        // ── 6. Approver Transitions ─────────────────────────────────────────
        // Approve Member (fm: 20 -> 30; family remains 18)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-APP-APPROVE-MEMBER")
                .requiredRole(WorkflowRole.ROLE_APPROVER)
                .action(WorkflowAction.APPROVER_APPROVE_MEMBER)
                .sourceRoleIds(Set.of(20))
                .targetFamilyRoleId(NextLevelRoleId.ACCEPT_APPLICATION_VERIFIER)
                .targetMemberRoleId(NextLevelRoleId.APPROVE_MEMBER_APPROVER)
                .targetFamilyStatus(ApplicationStatus.ACCEPTED)
                .targetMemberStatus(ApplicationStatus.APPROVED)
                .opType(OpType.APPROVE)
                .build());

        // Bulk Approve Members (fm: 20 -> 30)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-APP-BULK-APPROVE")
                .requiredRole(WorkflowRole.ROLE_APPROVER)
                .action(WorkflowAction.APPROVER_BULK_APPROVE)
                .sourceRoleIds(Set.of(20))
                .targetFamilyRoleId(NextLevelRoleId.ACCEPT_APPLICATION_VERIFIER)
                .targetMemberRoleId(NextLevelRoleId.APPROVE_MEMBER_APPROVER)
                .targetFamilyStatus(ApplicationStatus.ACCEPTED)
                .targetMemberStatus(ApplicationStatus.APPROVED)
                .opType(OpType.APPROVE)
                .build());

        // Reject Member (fm: 20 -> -102; family remains 18)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-APP-REJECT-MEMBER")
                .requiredRole(WorkflowRole.ROLE_APPROVER)
                .action(WorkflowAction.APPROVER_REJECT_MEMBER)
                .sourceRoleIds(Set.of(20))
                .targetFamilyRoleId(NextLevelRoleId.ACCEPT_APPLICATION_VERIFIER)
                .targetMemberRoleId(NextLevelRoleId.REJECT_MEMBER_APPROVER)
                .targetFamilyStatus(ApplicationStatus.ACCEPTED)
                .targetMemberStatus(ApplicationStatus.REJECTED)
                .opType(OpType.REJECT)
                .requireRejectRevertCause(true)
                .build());

        // Approver Revert Cascade (fm: 20 -> cascades whole family to 9/4, detail 99, excluding role 30)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-APP-REVERT-CASCADE")
                .requiredRole(WorkflowRole.ROLE_APPROVER)
                .action(WorkflowAction.APPROVER_REVERT_MEMBER_CASCADE)
                .sourceRoleIds(Set.of(20))
                .targetFamilyRoleId(NextLevelRoleId.REVERT_OFFICE_APPROVER)
                .targetMemberRoleId(NextLevelRoleId.REVERT_OFFICE_APPROVER)
                .targetFamilyStatus(ApplicationStatus.REVERTED)
                .targetMemberStatus(ApplicationStatus.REVERTED)
                .targetDetailStatus(99)
                .opType(OpType.REVERT)
                .requireRejectRevertCause(true)
                .preservesApprovedMembers(true)
                .build());

        // Approver Hold Family (f & fm role 20 -> 26, 'HELDUP_A', min remarks 15)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-APP-HOLD-FAMILY")
                .requiredRole(WorkflowRole.ROLE_APPROVER)
                .action(WorkflowAction.APPROVER_HOLD_FAMILY)
                .sourceRoleIds(Set.of(20))
                .targetFamilyRoleId(NextLevelRoleId.APPROVER_HOLD)
                .targetMemberRoleId(NextLevelRoleId.APPROVER_HOLD)
                .targetFamilyStatus(ApplicationStatus.HELDUP_A)
                .targetMemberStatus(ApplicationStatus.HELDUP_A)
                .opType(OpType.HELDUP_A)
                .minRemarksLength(15)
                .preservesApprovedMembers(true)
                .build());

        // Approver Hold Single Member (fm role 20 -> 26, 'HELDUP_M_A', min remarks 15, family unchanged)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-APP-HOLD-SINGLE-MEMBER")
                .requiredRole(WorkflowRole.ROLE_APPROVER)
                .action(WorkflowAction.APPROVER_HOLD_SINGLE_MEMBER)
                .sourceRoleIds(Set.of(20))
                .targetMemberRoleId(NextLevelRoleId.APPROVER_HOLD)
                .targetMemberStatus(ApplicationStatus.HELDUP_M_A)
                .opType(OpType.HELDUP_M_A)
                .minRemarksLength(15)
                .build());

        // Temp Hold Approved Member (fm: 30 -> 25)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-APP-TEMP-HOLD")
                .requiredRole(WorkflowRole.ROLE_APPROVER)
                .action(WorkflowAction.APPROVER_TEMP_HOLD_MEMBER)
                .sourceRoleIds(Set.of(30))
                .targetMemberRoleId(NextLevelRoleId.HOLD_MEMBER_APPROVER)
                .targetMemberStatus("HOLD_TEMP")
                .opType("HOLD_TEMP")
                .build());

        // Reject Hold Member (fm: 25 -> -104)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-APP-REJECT-HOLD")
                .requiredRole(WorkflowRole.ROLE_APPROVER)
                .action(WorkflowAction.APPROVER_REJECT_HOLD_MEMBER)
                .sourceRoleIds(Set.of(25))
                .targetMemberRoleId(NextLevelRoleId.REJECT_HOLD_MEMBER_APPROVER)
                .targetMemberStatus(ApplicationStatus.REJECTED_HOLD_A)
                .opType(OpType.REJECTED_HOLD_A)
                .build());

        // Reject Recommended for Rejection Member (fm: 27 -> -103)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-APP-REJECT-REC")
                .requiredRole(WorkflowRole.ROLE_APPROVER)
                .action(WorkflowAction.APPROVER_REJECT_RECOMMENDED)
                .sourceRoleIds(Set.of(27))
                .targetMemberRoleId(NextLevelRoleId.REJECT_APPROVED_RECOMMENDED_APPROVER)
                .targetMemberStatus(ApplicationStatus.REJECTED)
                .opType(OpType.REJECT)
                .build());

        // ── 7. Administrative Re-Processing (RevertRejectToApprove) ─────────
        // Re-Approve from Reject (-100, -101, -102, -103 -> 30)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-ADM-APPROVE-FROM-REJECT")
                .requiredRole(WorkflowRole.ROLE_ADMIN)
                .action(WorkflowAction.ADMIN_APPROVE_FROM_REJECT)
                .sourceRoleIds(Set.of(-100, -101, -102, -103))
                .targetMemberRoleId(NextLevelRoleId.APPROVE_MEMBER_APPROVER)
                .targetMemberStatus(ApplicationStatus.APPROVE)
                .opType(OpType.APPROVE_FROM_REJECT)
                .build());

        // Send back to Enquiry (-100, -101, -102, -103 -> 10, min remarks 5)
        list.add(WorkflowTransition.builder()
                .transitionId("TR-ADM-REJECT-TO-ENQUIRY")
                .requiredRole(WorkflowRole.ROLE_ADMIN)
                .action(WorkflowAction.ADMIN_SEND_TO_ENQUIRY)
                .sourceRoleIds(Set.of(-100, -101, -102, -103))
                .targetFamilyRoleId(NextLevelRoleId.FINAL_SUBMISSION)
                .targetMemberRoleId(NextLevelRoleId.FINAL_SUBMISSION)
                .targetFamilyStatus(ApplicationStatus.REJECT_TO_ENQUIRY)
                .targetMemberStatus(ApplicationStatus.REJECT_TO_ENQUIRY)
                .opType(OpType.REJECT_TO_ENQUIRY)
                .minRemarksLength(5)
                .build());

        this.transitions = Collections.unmodifiableList(list);
    }

    public WorkflowTransition getTransition(WorkflowRole role, WorkflowAction action, int currentRoleId) {
        return transitions.stream()
                .filter(t -> t.matches(role, action, currentRoleId))
                .findFirst()
                .orElseThrow(() -> new InvalidWorkflowTransitionException(
                        String.format("Invalid workflow transition: Action '%s' is not allowed for actor '%s' from state (role_id=%d)",
                                action, role, currentRoleId),
                        currentRoleId,
                        action.name(),
                        role.name()
                ));
    }

    public boolean canTransition(WorkflowRole role, WorkflowAction action, int currentRoleId) {
        return transitions.stream().anyMatch(t -> t.matches(role, action, currentRoleId));
    }

    public List<WorkflowTransition> getAllTransitions() {
        return transitions;
    }
}

