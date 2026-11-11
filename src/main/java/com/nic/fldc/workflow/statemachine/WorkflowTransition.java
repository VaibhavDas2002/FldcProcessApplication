package com.nic.fldc.workflow.statemachine;

import java.util.Set;

import com.nic.fldc.common.security.WorkflowRole;

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

    public WorkflowTransition(String transitionId, WorkflowRole requiredRole, WorkflowAction action,
                              Set<Integer> sourceRoleIds, NextLevelRoleId targetFamilyRoleId,
                              NextLevelRoleId targetMemberRoleId, String targetFamilyStatus,
                              String targetMemberStatus, Integer targetDetailStatus, String opType,
                              int minRemarksLength, boolean requireRejectRevertCause,
                              boolean preservesApprovedMembers) {
        this.transitionId = transitionId;
        this.requiredRole = requiredRole;
        this.action = action;
        this.sourceRoleIds = sourceRoleIds;
        this.targetFamilyRoleId = targetFamilyRoleId;
        this.targetMemberRoleId = targetMemberRoleId;
        this.targetFamilyStatus = targetFamilyStatus;
        this.targetMemberStatus = targetMemberStatus;
        this.targetDetailStatus = targetDetailStatus;
        this.opType = opType;
        this.minRemarksLength = minRemarksLength;
        this.requireRejectRevertCause = requireRejectRevertCause;
        this.preservesApprovedMembers = preservesApprovedMembers;
    }

    public boolean matches(WorkflowRole role, WorkflowAction action, int currentRoleId) {
        return this.requiredRole == role
                && this.action == action
                && this.sourceRoleIds != null
                && this.sourceRoleIds.contains(currentRoleId);
    }

    public String getTransitionId() {
        return transitionId;
    }

    public WorkflowRole getRequiredRole() {
        return requiredRole;
    }

    public WorkflowAction getAction() {
        return action;
    }

    public Set<Integer> getSourceRoleIds() {
        return sourceRoleIds;
    }

    public NextLevelRoleId getTargetFamilyRoleId() {
        return targetFamilyRoleId;
    }

    public NextLevelRoleId getTargetMemberRoleId() {
        return targetMemberRoleId;
    }

    public String getTargetFamilyStatus() {
        return targetFamilyStatus;
    }

    public String getTargetMemberStatus() {
        return targetMemberStatus;
    }

    public Integer getTargetDetailStatus() {
        return targetDetailStatus;
    }

    public String getOpType() {
        return opType;
    }

    public int getMinRemarksLength() {
        return minRemarksLength;
    }

    public boolean isRequireRejectRevertCause() {
        return requireRejectRevertCause;
    }

    public boolean isPreservesApprovedMembers() {
        return preservesApprovedMembers;
    }

    public static WorkflowTransitionBuilder builder() {
        return new WorkflowTransitionBuilder();
    }

    public static class WorkflowTransitionBuilder {
        private String transitionId;
        private WorkflowRole requiredRole;
        private WorkflowAction action;
        private Set<Integer> sourceRoleIds;
        private NextLevelRoleId targetFamilyRoleId;
        private NextLevelRoleId targetMemberRoleId;
        private String targetFamilyStatus;
        private String targetMemberStatus;
        private Integer targetDetailStatus;
        private String opType;
        private int minRemarksLength;
        private boolean requireRejectRevertCause;
        private boolean preservesApprovedMembers;

        public WorkflowTransitionBuilder transitionId(String transitionId) {
            this.transitionId = transitionId;
            return this;
        }

        public WorkflowTransitionBuilder requiredRole(WorkflowRole requiredRole) {
            this.requiredRole = requiredRole;
            return this;
        }

        public WorkflowTransitionBuilder action(WorkflowAction action) {
            this.action = action;
            return this;
        }

        public WorkflowTransitionBuilder sourceRoleIds(Set<Integer> sourceRoleIds) {
            this.sourceRoleIds = sourceRoleIds;
            return this;
        }

        public WorkflowTransitionBuilder targetFamilyRoleId(NextLevelRoleId targetFamilyRoleId) {
            this.targetFamilyRoleId = targetFamilyRoleId;
            return this;
        }

        public WorkflowTransitionBuilder targetMemberRoleId(NextLevelRoleId targetMemberRoleId) {
            this.targetMemberRoleId = targetMemberRoleId;
            return this;
        }

        public WorkflowTransitionBuilder targetFamilyStatus(String targetFamilyStatus) {
            this.targetFamilyStatus = targetFamilyStatus;
            return this;
        }

        public WorkflowTransitionBuilder targetMemberStatus(String targetMemberStatus) {
            this.targetMemberStatus = targetMemberStatus;
            return this;
        }

        public WorkflowTransitionBuilder targetDetailStatus(Integer targetDetailStatus) {
            this.targetDetailStatus = targetDetailStatus;
            return this;
        }

        public WorkflowTransitionBuilder opType(String opType) {
            this.opType = opType;
            return this;
        }

        public WorkflowTransitionBuilder minRemarksLength(int minRemarksLength) {
            this.minRemarksLength = minRemarksLength;
            return this;
        }

        public WorkflowTransitionBuilder requireRejectRevertCause(boolean requireRejectRevertCause) {
            this.requireRejectRevertCause = requireRejectRevertCause;
            return this;
        }

        public WorkflowTransitionBuilder preservesApprovedMembers(boolean preservesApprovedMembers) {
            this.preservesApprovedMembers = preservesApprovedMembers;
            return this;
        }

        public WorkflowTransition build() {
            return new WorkflowTransition(transitionId, requiredRole, action, sourceRoleIds,
                    targetFamilyRoleId, targetMemberRoleId, targetFamilyStatus, targetMemberStatus,
                    targetDetailStatus, opType, minRemarksLength, requireRejectRevertCause,
                    preservesApprovedMembers);
        }
    }
}
