package com.nic.fldc.workflow.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Family application summary response with member list")
public class FamilySummaryResponse {

    @Schema(description = "Family UUID")
    private UUID id;

    @Schema(description = "Application Serial")
    private Long appSerial;

    @Schema(description = "Temporary Application ID")
    private Long tmpApplicationId;

    @Schema(description = "LGD District Code")
    private Integer lgdDistrictCode;

    @Schema(description = "Created by Local Body Code")
    private Long createdByLocalBodyCode;

    @Schema(description = "LGD Block / Municipality Code")
    private Integer lgdBlockMcCode;

    @Schema(description = "LGD Gram Panchayat / Ward Code")
    private Integer lgdGpWardCode;

    @Schema(description = "Create Mode: 'C' (Citizen) or 'O' (Office)")
    private String createMode;

    @Schema(description = "Current next_level_role_id")
    private Integer nextLevelRoleId;

    @Schema(description = "Role status description")
    private String roleStatusName;

    @Schema(description = "Application status")
    private String applicationStatus;

    @Schema(description = "Detail status")
    private Integer detailStatus;

    @Schema(description = "Under enquiry flag")
    private Boolean underEnquiry;

    @Schema(description = "JKS ID")
    private String jksId;

    @Schema(description = "Verification datetime")
    private LocalDateTime verificationDatetime;

    @Schema(description = "Approval datetime")
    private LocalDateTime approvalDatetime;

    @Schema(description = "Members of the family")
    private List<MemberDetailResponse> members;

    public FamilySummaryResponse() {
    }

    public FamilySummaryResponse(UUID id, Long appSerial, Long tmpApplicationId, Integer lgdDistrictCode,
                                 Long createdByLocalBodyCode, Integer lgdBlockMcCode, Integer lgdGpWardCode,
                                 String createMode, Integer nextLevelRoleId, String roleStatusName,
                                 String applicationStatus, Integer detailStatus, Boolean underEnquiry,
                                 String jksId, LocalDateTime verificationDatetime,
                                 LocalDateTime approvalDatetime, List<MemberDetailResponse> members) {
        this.id = id;
        this.appSerial = appSerial;
        this.tmpApplicationId = tmpApplicationId;
        this.lgdDistrictCode = lgdDistrictCode;
        this.createdByLocalBodyCode = createdByLocalBodyCode;
        this.lgdBlockMcCode = lgdBlockMcCode;
        this.lgdGpWardCode = lgdGpWardCode;
        this.createMode = createMode;
        this.nextLevelRoleId = nextLevelRoleId;
        this.roleStatusName = roleStatusName;
        this.applicationStatus = applicationStatus;
        this.detailStatus = detailStatus;
        this.underEnquiry = underEnquiry;
        this.jksId = jksId;
        this.verificationDatetime = verificationDatetime;
        this.approvalDatetime = approvalDatetime;
        this.members = members;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getAppSerial() {
        return appSerial;
    }

    public void setAppSerial(Long appSerial) {
        this.appSerial = appSerial;
    }

    public Long getTmpApplicationId() {
        return tmpApplicationId;
    }

    public void setTmpApplicationId(Long tmpApplicationId) {
        this.tmpApplicationId = tmpApplicationId;
    }

    public Integer getLgdDistrictCode() {
        return lgdDistrictCode;
    }

    public void setLgdDistrictCode(Integer lgdDistrictCode) {
        this.lgdDistrictCode = lgdDistrictCode;
    }

    public Long getCreatedByLocalBodyCode() {
        return createdByLocalBodyCode;
    }

    public void setCreatedByLocalBodyCode(Long createdByLocalBodyCode) {
        this.createdByLocalBodyCode = createdByLocalBodyCode;
    }

    public Integer getLgdBlockMcCode() {
        return lgdBlockMcCode;
    }

    public void setLgdBlockMcCode(Integer lgdBlockMcCode) {
        this.lgdBlockMcCode = lgdBlockMcCode;
    }

    public Integer getLgdGpWardCode() {
        return lgdGpWardCode;
    }

    public void setLgdGpWardCode(Integer lgdGpWardCode) {
        this.lgdGpWardCode = lgdGpWardCode;
    }

    public String getCreateMode() {
        return createMode;
    }

    public void setCreateMode(String createMode) {
        this.createMode = createMode;
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

    public Boolean getUnderEnquiry() {
        return underEnquiry;
    }

    public void setUnderEnquiry(Boolean underEnquiry) {
        this.underEnquiry = underEnquiry;
    }

    public String getJksId() {
        return jksId;
    }

    public void setJksId(String jksId) {
        this.jksId = jksId;
    }

    public LocalDateTime getVerificationDatetime() {
        return verificationDatetime;
    }

    public void setVerificationDatetime(LocalDateTime verificationDatetime) {
        this.verificationDatetime = verificationDatetime;
    }

    public LocalDateTime getApprovalDatetime() {
        return approvalDatetime;
    }

    public void setApprovalDatetime(LocalDateTime approvalDatetime) {
        this.approvalDatetime = approvalDatetime;
    }

    public List<MemberDetailResponse> getMembers() {
        return members;
    }

    public void setMembers(List<MemberDetailResponse> members) {
        this.members = members;
    }

    public static FamilySummaryResponseBuilder builder() {
        return new FamilySummaryResponseBuilder();
    }

    public static class FamilySummaryResponseBuilder {
        private UUID id;
        private Long appSerial;
        private Long tmpApplicationId;
        private Integer lgdDistrictCode;
        private Long createdByLocalBodyCode;
        private Integer lgdBlockMcCode;
        private Integer lgdGpWardCode;
        private String createMode;
        private Integer nextLevelRoleId;
        private String roleStatusName;
        private String applicationStatus;
        private Integer detailStatus;
        private Boolean underEnquiry;
        private String jksId;
        private LocalDateTime verificationDatetime;
        private LocalDateTime approvalDatetime;
        private List<MemberDetailResponse> members;

        public FamilySummaryResponseBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public FamilySummaryResponseBuilder appSerial(Long appSerial) {
            this.appSerial = appSerial;
            return this;
        }

        public FamilySummaryResponseBuilder tmpApplicationId(Long tmpApplicationId) {
            this.tmpApplicationId = tmpApplicationId;
            return this;
        }

        public FamilySummaryResponseBuilder lgdDistrictCode(Integer lgdDistrictCode) {
            this.lgdDistrictCode = lgdDistrictCode;
            return this;
        }

        public FamilySummaryResponseBuilder createdByLocalBodyCode(Long createdByLocalBodyCode) {
            this.createdByLocalBodyCode = createdByLocalBodyCode;
            return this;
        }

        public FamilySummaryResponseBuilder lgdBlockMcCode(Integer lgdBlockMcCode) {
            this.lgdBlockMcCode = lgdBlockMcCode;
            return this;
        }

        public FamilySummaryResponseBuilder lgdGpWardCode(Integer lgdGpWardCode) {
            this.lgdGpWardCode = lgdGpWardCode;
            return this;
        }

        public FamilySummaryResponseBuilder createMode(String createMode) {
            this.createMode = createMode;
            return this;
        }

        public FamilySummaryResponseBuilder nextLevelRoleId(Integer nextLevelRoleId) {
            this.nextLevelRoleId = nextLevelRoleId;
            return this;
        }

        public FamilySummaryResponseBuilder roleStatusName(String roleStatusName) {
            this.roleStatusName = roleStatusName;
            return this;
        }

        public FamilySummaryResponseBuilder applicationStatus(String applicationStatus) {
            this.applicationStatus = applicationStatus;
            return this;
        }

        public FamilySummaryResponseBuilder detailStatus(Integer detailStatus) {
            this.detailStatus = detailStatus;
            return this;
        }

        public FamilySummaryResponseBuilder underEnquiry(Boolean underEnquiry) {
            this.underEnquiry = underEnquiry;
            return this;
        }

        public FamilySummaryResponseBuilder jksId(String jksId) {
            this.jksId = jksId;
            return this;
        }

        public FamilySummaryResponseBuilder verificationDatetime(LocalDateTime verificationDatetime) {
            this.verificationDatetime = verificationDatetime;
            return this;
        }

        public FamilySummaryResponseBuilder approvalDatetime(LocalDateTime approvalDatetime) {
            this.approvalDatetime = approvalDatetime;
            return this;
        }

        public FamilySummaryResponseBuilder members(List<MemberDetailResponse> members) {
            this.members = members;
            return this;
        }

        public FamilySummaryResponse build() {
            return new FamilySummaryResponse(id, appSerial, tmpApplicationId, lgdDistrictCode, createdByLocalBodyCode,
                    lgdBlockMcCode, lgdGpWardCode, createMode, nextLevelRoleId, roleStatusName,
                    applicationStatus, detailStatus, underEnquiry, jksId, verificationDatetime,
                    approvalDatetime, members);
        }
    }
}
