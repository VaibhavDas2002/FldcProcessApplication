package com.nic.fldc.workflow.statemachine;

import java.util.Set;

import com.nic.fldc.common.security.WorkflowRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkflowTransition {

    private final String transitionId;
    private final WorkflowRole requiredRole;
    private final WorkflowAction action;
    private final Set<Integer> sourceRoleIds;
    private final NextLevelRoleId targetFamilyRoleId;
    private final NextLevelRoleId targetMemberRoleId;
    private final String targetFamilyStatus;
    private final String targetMemberStatus;
    private final Integer targetDetailStatus;
    private final String opType;
    private final int minRemarksLength;
    private final boolean requireRejectRevertCause;
    private final boolean preservesApprovedMembers;

    public boolean matches(WorkflowRole role, WorkflowAction action, int currentRoleId) {
        return this.requiredRole == role
                && this.action == action
                && this.sourceRoleIds.contains(currentRoleId);
    }
}

