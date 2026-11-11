package com.nic.fldc.workflow.dto.response;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of workflow mutation")
public class WorkflowActionResponse {

    @Schema(description = "Executed workflow action", example = "VERIFIER_ACCEPT_FAMILY")
    private String action;

    @Schema(description = "Target Family UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID familyId;

    @Schema(description = "Target Member UUID (if member-specific)")
    private UUID memberId;

    @Schema(description = "Number of affected family members in this transaction", example = "4")
    private int affectedMembers;

    @Schema(description = "Resulting next_level_role_id", example = "18")
    private Integer nextLevelRoleId;

    @Schema(description = "Resulting application_status", example = "ACCEPTED")
    private String applicationStatus;

    @Schema(description = "Informative status message", example = "Family Application Accepted successfully.")
    private String message;

    @Schema(description = "Audit trail op_type logged", example = "ACCEPT_FAMILY")
    private String opType;

    @Schema(description = "True if mixed-revert cascade was triggered", example = "false")
    private boolean mixedRevertCascaded;

    public WorkflowActionResponse() {
    }

    public WorkflowActionResponse(String action, UUID familyId, UUID memberId, int affectedMembers,
                                  Integer nextLevelRoleId, String applicationStatus, String message,
                                  String opType, boolean mixedRevertCascaded) {
        this.action = action;
        this.familyId = familyId;
        this.memberId = memberId;
        this.affectedMembers = affectedMembers;
        this.nextLevelRoleId = nextLevelRoleId;
        this.applicationStatus = applicationStatus;
        this.message = message;
        this.opType = opType;
        this.mixedRevertCascaded = mixedRevertCascaded;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public int getAffectedMembers() {
        return affectedMembers;
    }

    public void setAffectedMembers(int affectedMembers) {
        this.affectedMembers = affectedMembers;
    }

    public Integer getNextLevelRoleId() {
        return nextLevelRoleId;
    }

    public void setNextLevelRoleId(Integer nextLevelRoleId) {
        this.nextLevelRoleId = nextLevelRoleId;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public boolean isMixedRevertCascaded() {
        return mixedRevertCascaded;
    }

    public void setMixedRevertCascaded(boolean mixedRevertCascaded) {
        this.mixedRevertCascaded = mixedRevertCascaded;
    }

    public static WorkflowActionResponseBuilder builder() {
        return new WorkflowActionResponseBuilder();
    }

    public static class WorkflowActionResponseBuilder {
        private String action;
        private UUID familyId;
        private UUID memberId;
        private int affectedMembers;
        private Integer nextLevelRoleId;
        private String applicationStatus;
        private String message;
        private String opType;
        private boolean mixedRevertCascaded;

        public WorkflowActionResponseBuilder action(String action) {
            this.action = action;
            return this;
        }

        public WorkflowActionResponseBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public WorkflowActionResponseBuilder memberId(UUID memberId) {
            this.memberId = memberId;
            return this;
        }

        public WorkflowActionResponseBuilder affectedMembers(int affectedMembers) {
            this.affectedMembers = affectedMembers;
            return this;
        }

        public WorkflowActionResponseBuilder nextLevelRoleId(Integer nextLevelRoleId) {
            this.nextLevelRoleId = nextLevelRoleId;
            return this;
        }

        public WorkflowActionResponseBuilder applicationStatus(String applicationStatus) {
            this.applicationStatus = applicationStatus;
            return this;
        }

        public WorkflowActionResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public WorkflowActionResponseBuilder opType(String opType) {
            this.opType = opType;
            return this;
        }

        public WorkflowActionResponseBuilder mixedRevertCascaded(boolean mixedRevertCascaded) {
            this.mixedRevertCascaded = mixedRevertCascaded;
            return this;
        }

        public WorkflowActionResponse build() {
            return new WorkflowActionResponse(action, familyId, memberId, affectedMembers, nextLevelRoleId, applicationStatus, message, opType, mixedRevertCascaded);
        }
    }
}
