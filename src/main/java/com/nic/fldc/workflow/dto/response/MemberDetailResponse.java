package com.nic.fldc.workflow.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Family member detail response")
public class MemberDetailResponse {

    @Schema(description = "Member UUID")
    private UUID id;

    @Schema(description = "Family UUID")
    private UUID familyId;

    @Schema(description = "Family serial")
    private Long familySerial;

    @Schema(description = "Gender", example = "F")
    private String gender;

    @Schema(description = "Date of Birth")
    private LocalDate dob;

    @Schema(description = "Calculated age", example = "34")
    private Integer age;

    @Schema(description = "Applying for Annapurna Bhandar flag", example = "true")
    private Boolean applyingForAnnapurnaBhandar;

    @Schema(description = "Current next_level_role_id", example = "18")
    private Integer nextLevelRoleId;

    @Schema(description = "Role status description", example = "Accept Application / Family Details - Verifier")
    private String roleStatusName;

    @Schema(description = "Current application_status", example = "ACCEPTED")
    private String applicationStatus;

    @Schema(description = "Detail status code (e.g. 99 for reverted)")
    private Integer detailStatus;

    @Schema(description = "Is eligible for Annapurna Bhandar action (Female, Age 25-60, AY=true, Role=18)", example = "true")
    private Boolean eligibleForAction;

    @Schema(description = "Not in ASDD")
    private Boolean notInAsdd;

    @Schema(description = "Appeal tribunal text")
    private String appealTribunal;

    @Schema(description = "Verification datetime")
    private LocalDateTime verificationDatetime;

    @Schema(description = "Verified by User ID")
    private Long verifiedBy;

    @Schema(description = "Approval datetime")
    private LocalDateTime approvalDatetime;

    @Schema(description = "Approved by User ID")
    private Long approvedBy;

    @Schema(description = "Verification remarks")
    private String verificationRemarks;

    @Schema(description = "Approval remarks")
    private String approvalRemarks;

    public MemberDetailResponse() {
    }

    public MemberDetailResponse(UUID id, UUID familyId, Long familySerial, String gender, LocalDate dob,
                                Integer age, Boolean applyingForAnnapurnaBhandar, Integer nextLevelRoleId,
                                String roleStatusName, String applicationStatus, Integer detailStatus,
                                Boolean eligibleForAction, Boolean notInAsdd, String appealTribunal,
                                LocalDateTime verificationDatetime, Long verifiedBy,
                                LocalDateTime approvalDatetime, Long approvedBy,
                                String verificationRemarks, String approvalRemarks) {
        this.id = id;
        this.familyId = familyId;
        this.familySerial = familySerial;
        this.gender = gender;
        this.dob = dob;
        this.age = age;
        this.applyingForAnnapurnaBhandar = applyingForAnnapurnaBhandar;
        this.nextLevelRoleId = nextLevelRoleId;
        this.roleStatusName = roleStatusName;
        this.applicationStatus = applicationStatus;
        this.detailStatus = detailStatus;
        this.eligibleForAction = eligibleForAction;
        this.notInAsdd = notInAsdd;
        this.appealTribunal = appealTribunal;
        this.verificationDatetime = verificationDatetime;
        this.verifiedBy = verifiedBy;
        this.approvalDatetime = approvalDatetime;
        this.approvedBy = approvedBy;
        this.verificationRemarks = verificationRemarks;
        this.approvalRemarks = approvalRemarks;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public Long getFamilySerial() {
        return familySerial;
    }

    public void setFamilySerial(Long familySerial) {
        this.familySerial = familySerial;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getApplyingForAnnapurnaBhandar() {
        return applyingForAnnapurnaBhandar;
    }

    public void setApplyingForAnnapurnaBhandar(Boolean applyingForAnnapurnaBhandar) {
        this.applyingForAnnapurnaBhandar = applyingForAnnapurnaBhandar;
    }

    public Integer getNextLevelRoleId() {
        return nextLevelRoleId;
    }

    public void setNextLevelRoleId(Integer nextLevelRoleId) {
        this.nextLevelRoleId = nextLevelRoleId;
    }

    public String getRoleStatusName() {
        return roleStatusName;
    }

    public void setRoleStatusName(String roleStatusName) {
        this.roleStatusName = roleStatusName;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Integer getDetailStatus() {
        return detailStatus;
    }

    public void setDetailStatus(Integer detailStatus) {
        this.detailStatus = detailStatus;
    }

    public Boolean getEligibleForAction() {
        return eligibleForAction;
    }

    public void setEligibleForAction(Boolean eligibleForAction) {
        this.eligibleForAction = eligibleForAction;
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

    public LocalDateTime getVerificationDatetime() {
        return verificationDatetime;
    }

    public void setVerificationDatetime(LocalDateTime verificationDatetime) {
        this.verificationDatetime = verificationDatetime;
    }

    public Long getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(Long verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDateTime getApprovalDatetime() {
        return approvalDatetime;
    }

    public void setApprovalDatetime(LocalDateTime approvalDatetime) {
        this.approvalDatetime = approvalDatetime;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getVerificationRemarks() {
        return verificationRemarks;
    }

    public void setVerificationRemarks(String verificationRemarks) {
        this.verificationRemarks = verificationRemarks;
    }

    public String getApprovalRemarks() {
        return approvalRemarks;
    }

    public void setApprovalRemarks(String approvalRemarks) {
        this.approvalRemarks = approvalRemarks;
    }

    public static MemberDetailResponseBuilder builder() {
        return new MemberDetailResponseBuilder();
    }

    public static class MemberDetailResponseBuilder {
        private UUID id;
        private UUID familyId;
        private Long familySerial;
        private String gender;
        private LocalDate dob;
        private Integer age;
        private Boolean applyingForAnnapurnaBhandar;
        private Integer nextLevelRoleId;
        private String roleStatusName;
        private String applicationStatus;
        private Integer detailStatus;
        private Boolean eligibleForAction;
        private Boolean notInAsdd;
        private String appealTribunal;
        private LocalDateTime verificationDatetime;
        private Long verifiedBy;
        private LocalDateTime approvalDatetime;
        private Long approvedBy;
        private String verificationRemarks;
        private String approvalRemarks;

        public MemberDetailResponseBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public MemberDetailResponseBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public MemberDetailResponseBuilder familySerial(Long familySerial) {
            this.familySerial = familySerial;
            return this;
        }

        public MemberDetailResponseBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public MemberDetailResponseBuilder dob(LocalDate dob) {
            this.dob = dob;
            return this;
        }

        public MemberDetailResponseBuilder age(Integer age) {
            this.age = age;
            return this;
        }

        public MemberDetailResponseBuilder applyingForAnnapurnaBhandar(Boolean applyingForAnnapurnaBhandar) {
            this.applyingForAnnapurnaBhandar = applyingForAnnapurnaBhandar;
            return this;
        }

        public MemberDetailResponseBuilder nextLevelRoleId(Integer nextLevelRoleId) {
            this.nextLevelRoleId = nextLevelRoleId;
            return this;
        }

        public MemberDetailResponseBuilder roleStatusName(String roleStatusName) {
            this.roleStatusName = roleStatusName;
            return this;
        }

        public MemberDetailResponseBuilder applicationStatus(String applicationStatus) {
            this.applicationStatus = applicationStatus;
            return this;
        }

        public MemberDetailResponseBuilder detailStatus(Integer detailStatus) {
            this.detailStatus = detailStatus;
            return this;
        }

        public MemberDetailResponseBuilder eligibleForAction(Boolean eligibleForAction) {
            this.eligibleForAction = eligibleForAction;
            return this;
        }

        public MemberDetailResponseBuilder notInAsdd(Boolean notInAsdd) {
            this.notInAsdd = notInAsdd;
            return this;
        }

        public MemberDetailResponseBuilder appealTribunal(String appealTribunal) {
            this.appealTribunal = appealTribunal;
            return this;
        }

        public MemberDetailResponseBuilder verificationDatetime(LocalDateTime verificationDatetime) {
            this.verificationDatetime = verificationDatetime;
            return this;
        }

        public MemberDetailResponseBuilder verifiedBy(Long verifiedBy) {
            this.verifiedBy = verifiedBy;
            return this;
        }

        public MemberDetailResponseBuilder approvalDatetime(LocalDateTime approvalDatetime) {
            this.approvalDatetime = approvalDatetime;
            return this;
        }

        public MemberDetailResponseBuilder approvedBy(Long approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public MemberDetailResponseBuilder verificationRemarks(String verificationRemarks) {
            this.verificationRemarks = verificationRemarks;
            return this;
        }

        public MemberDetailResponseBuilder approvalRemarks(String approvalRemarks) {
            this.approvalRemarks = approvalRemarks;
            return this;
        }

        public MemberDetailResponse build() {
            return new MemberDetailResponse(id, familyId, familySerial, gender, dob, age,
                    applyingForAnnapurnaBhandar, nextLevelRoleId, roleStatusName, applicationStatus,
                    detailStatus, eligibleForAction, notInAsdd, appealTribunal,
                    verificationDatetime, verifiedBy, approvalDatetime, approvedBy,
                    verificationRemarks, approvalRemarks);
        }
    }
}
