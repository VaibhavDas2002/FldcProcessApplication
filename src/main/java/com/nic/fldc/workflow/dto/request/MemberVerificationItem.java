package com.nic.fldc.workflow.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Action selection for an individual eligible member during verification")
public class MemberVerificationItem {

    @NotNull(message = "memberId is required")
    @Schema(description = "Member UUID", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID memberId;

    @NotBlank(message = "action is required: VERIFY, REJECT, or REVERT")
    @Schema(description = "Target action: VERIFY, REJECT, or REVERT", example = "VERIFY")
    private String action;

    @Schema(description = "Not in ASDD flag", example = "false")
    private Boolean notInAsdd = false;

    @Schema(description = "Appeal tribunal reference text", example = "Tribunal Order #459/2024")
    private String appealTribunal;

    @Schema(description = "Remarks if rejected or reverted", example = "Income certificate criteria satisfied.")
    private String remarks;

    @Schema(description = "Reject or Revert cause ID", example = "8")
    private Integer rejectRevertCauseId;

    public MemberVerificationItem() {
    }

    public MemberVerificationItem(UUID memberId, String action, Boolean notInAsdd, String appealTribunal, String remarks, Integer rejectRevertCauseId) {
        this.memberId = memberId;
        this.action = action;
        this.notInAsdd = notInAsdd != null ? notInAsdd : false;
        this.appealTribunal = appealTribunal;
        this.remarks = remarks;
        this.rejectRevertCauseId = rejectRevertCauseId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getNotInAsdd() {
        return notInAsdd;
    }

    public void setNotInAsdd(Boolean notInAsdd) {
        this.notInAsdd = notInAsdd;
    }

    public String getAppealTribunal() {
        return appealTribunal;
    }

    public void setAppealTribunal(String appealTribunal) {
        this.appealTribunal = appealTribunal;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getRejectRevertCauseId() {
        return rejectRevertCauseId;
    }

    public void setRejectRevertCauseId(Integer rejectRevertCauseId) {
        this.rejectRevertCauseId = rejectRevertCauseId;
    }

    public static MemberVerificationItemBuilder builder() {
        return new MemberVerificationItemBuilder();
    }

    public static class MemberVerificationItemBuilder {
        private UUID memberId;
        private String action;
        private Boolean notInAsdd = false;
        private String appealTribunal;
        private String remarks;
        private Integer rejectRevertCauseId;

        public MemberVerificationItemBuilder memberId(UUID memberId) {
            this.memberId = memberId;
            return this;
        }

        public MemberVerificationItemBuilder action(String action) {
            this.action = action;
            return this;
        }

        public MemberVerificationItemBuilder notInAsdd(Boolean notInAsdd) {
            this.notInAsdd = notInAsdd;
            return this;
        }

        public MemberVerificationItemBuilder appealTribunal(String appealTribunal) {
            this.appealTribunal = appealTribunal;
            return this;
        }

        public MemberVerificationItemBuilder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public MemberVerificationItemBuilder rejectRevertCauseId(Integer rejectRevertCauseId) {
            this.rejectRevertCauseId = rejectRevertCauseId;
            return this;
        }

        public MemberVerificationItem build() {
            return new MemberVerificationItem(memberId, action, notInAsdd, appealTribunal, remarks, rejectRevertCauseId);
        }
    }
}
