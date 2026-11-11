package com.nic.fldc.workflow.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "families", schema = "dbt_apy")
public class Family {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "app_serial")
    private Long appSerial;

    @Column(name = "tmp_application_id")
    private Long tmpApplicationId;

    @Column(name = "lgd_district_code", nullable = false)
    private Integer lgdDistrictCode;

    @Column(name = "created_by_dist_code")
    private Integer createdByDistCode;

    @Column(name = "created_by_local_body_code")
    private Long createdByLocalBodyCode;

    @Column(name = "lgd_block_mc_code")
    private Integer lgdBlockMcCode;

    @Column(name = "lgd_gp_ward_code")
    private Integer lgdGpWardCode;

    @Column(name = "create_mode", length = 1)
    private String createMode;

    @Column(name = "next_level_role_id", nullable = false)
    private Integer nextLevelRoleId;

    @Column(name = "application_status", length = 50)
    private String applicationStatus;

    @Column(name = "detail_status")
    private Integer detailStatus;

    @Column(name = "under_enquiry")
    private Boolean underEnquiry = false;

    @Column(name = "is_agreed")
    private Boolean isAgreed = false;

    @Column(name = "is_claim")
    private Boolean isClaim = false;

    @Column(name = "jks_id", length = 50)
    private String jksId;

    @Column(name = "verification_remarks", columnDefinition = "TEXT")
    private String verificationRemarks;

    @Column(name = "verification_datetime")
    private LocalDateTime verificationDatetime;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "approval_remarks", columnDefinition = "TEXT")
    private String approvalRemarks;

    @Column(name = "approval_datetime")
    private LocalDateTime approvalDatetime;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "enquiry_remarks", columnDefinition = "TEXT")
    private String enquiryRemarks;

    @Column(name = "enquiry_datetime")
    private LocalDateTime enquiryDatetime;

    @Column(name = "enquiry_conducted_by")
    private Long enquiryConductedBy;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FamilyMember> members = new ArrayList<>();

    public Family() {
    }

    public Family(UUID id, Long appSerial, Long tmpApplicationId, Integer lgdDistrictCode,
                  Integer createdByDistCode, Long createdByLocalBodyCode, Integer lgdBlockMcCode,
                  Integer lgdGpWardCode, String createMode, Integer nextLevelRoleId,
                  String applicationStatus, Integer detailStatus, Boolean underEnquiry,
                  Boolean isAgreed, Boolean isClaim, String jksId, String verificationRemarks,
                  LocalDateTime verificationDatetime, Long verifiedBy, String approvalRemarks,
                  LocalDateTime approvalDatetime, Long approvedBy, String enquiryRemarks,
                  LocalDateTime enquiryDatetime, Long enquiryConductedBy, Boolean isDeleted,
                  LocalDateTime createdAt, LocalDateTime updatedAt, Long updatedBy,
                  List<FamilyMember> members) {
        this.id = id;
        this.appSerial = appSerial;
        this.tmpApplicationId = tmpApplicationId;
        this.lgdDistrictCode = lgdDistrictCode;
        this.createdByDistCode = createdByDistCode;
        this.createdByLocalBodyCode = createdByLocalBodyCode;
        this.lgdBlockMcCode = lgdBlockMcCode;
        this.lgdGpWardCode = lgdGpWardCode;
        this.createMode = createMode;
        this.nextLevelRoleId = nextLevelRoleId;
        this.applicationStatus = applicationStatus;
        this.detailStatus = detailStatus;
        this.underEnquiry = underEnquiry != null ? underEnquiry : false;
        this.isAgreed = isAgreed != null ? isAgreed : false;
        this.isClaim = isClaim != null ? isClaim : false;
        this.jksId = jksId;
        this.verificationRemarks = verificationRemarks;
        this.verificationDatetime = verificationDatetime;
        this.verifiedBy = verifiedBy;
        this.approvalRemarks = approvalRemarks;
        this.approvalDatetime = approvalDatetime;
        this.approvedBy = approvedBy;
        this.enquiryRemarks = enquiryRemarks;
        this.enquiryDatetime = enquiryDatetime;
        this.enquiryConductedBy = enquiryConductedBy;
        this.isDeleted = isDeleted != null ? isDeleted : false;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.members = members != null ? members : new ArrayList<>();
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

    public Integer getCreatedByDistCode() {
        return createdByDistCode;
    }

    public void setCreatedByDistCode(Integer createdByDistCode) {
        this.createdByDistCode = createdByDistCode;
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

    public Boolean getIsAgreed() {
        return isAgreed;
    }

    public void setIsAgreed(Boolean isAgreed) {
        this.isAgreed = isAgreed;
    }

    public Boolean getIsClaim() {
        return isClaim;
    }

    public void setIsClaim(Boolean isClaim) {
        this.isClaim = isClaim;
    }

    public String getJksId() {
        return jksId;
    }

    public void setJksId(String jksId) {
        this.jksId = jksId;
    }

    public String getVerificationRemarks() {
        return verificationRemarks;
    }

    public void setVerificationRemarks(String verificationRemarks) {
        this.verificationRemarks = verificationRemarks;
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

    public String getApprovalRemarks() {
        return approvalRemarks;
    }

    public void setApprovalRemarks(String approvalRemarks) {
        this.approvalRemarks = approvalRemarks;
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

    public String getEnquiryRemarks() {
        return enquiryRemarks;
    }

    public void setEnquiryRemarks(String enquiryRemarks) {
        this.enquiryRemarks = enquiryRemarks;
    }

    public LocalDateTime getEnquiryDatetime() {
        return enquiryDatetime;
    }

    public void setEnquiryDatetime(LocalDateTime enquiryDatetime) {
        this.enquiryDatetime = enquiryDatetime;
    }

    public Long getEnquiryConductedBy() {
        return enquiryConductedBy;
    }

    public void setEnquiryConductedBy(Long enquiryConductedBy) {
        this.enquiryConductedBy = enquiryConductedBy;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public List<FamilyMember> getMembers() {
        return members;
    }

    public void setMembers(List<FamilyMember> members) {
        this.members = members;
    }

    public static FamilyBuilder builder() {
        return new FamilyBuilder();
    }

    public static class FamilyBuilder {
        private UUID id;
        private Long appSerial;
        private Long tmpApplicationId;
        private Integer lgdDistrictCode;
        private Integer createdByDistCode;
        private Long createdByLocalBodyCode;
        private Integer lgdBlockMcCode;
        private Integer lgdGpWardCode;
        private String createMode;
        private Integer nextLevelRoleId;
        private String applicationStatus;
        private Integer detailStatus;
        private Boolean underEnquiry = false;
        private Boolean isAgreed = false;
        private Boolean isClaim = false;
        private String jksId;
        private String verificationRemarks;
        private LocalDateTime verificationDatetime;
        private Long verifiedBy;
        private String approvalRemarks;
        private LocalDateTime approvalDatetime;
        private Long approvedBy;
        private String enquiryRemarks;
        private LocalDateTime enquiryDatetime;
        private Long enquiryConductedBy;
        private Boolean isDeleted = false;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long updatedBy;
        private List<FamilyMember> members = new ArrayList<>();

        public FamilyBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public FamilyBuilder appSerial(Long appSerial) {
            this.appSerial = appSerial;
            return this;
        }

        public FamilyBuilder tmpApplicationId(Long tmpApplicationId) {
            this.tmpApplicationId = tmpApplicationId;
            return this;
        }

        public FamilyBuilder lgdDistrictCode(Integer lgdDistrictCode) {
            this.lgdDistrictCode = lgdDistrictCode;
            return this;
        }

        public FamilyBuilder createdByDistCode(Integer createdByDistCode) {
            this.createdByDistCode = createdByDistCode;
            return this;
        }

        public FamilyBuilder createdByLocalBodyCode(Long createdByLocalBodyCode) {
            this.createdByLocalBodyCode = createdByLocalBodyCode;
            return this;
        }

        public FamilyBuilder lgdBlockMcCode(Integer lgdBlockMcCode) {
            this.lgdBlockMcCode = lgdBlockMcCode;
            return this;
        }

        public FamilyBuilder lgdGpWardCode(Integer lgdGpWardCode) {
            this.lgdGpWardCode = lgdGpWardCode;
            return this;
        }

        public FamilyBuilder createMode(String createMode) {
            this.createMode = createMode;
            return this;
        }

        public FamilyBuilder nextLevelRoleId(Integer nextLevelRoleId) {
            this.nextLevelRoleId = nextLevelRoleId;
            return this;
        }

        public FamilyBuilder applicationStatus(String applicationStatus) {
            this.applicationStatus = applicationStatus;
            return this;
        }

        public FamilyBuilder detailStatus(Integer detailStatus) {
            this.detailStatus = detailStatus;
            return this;
        }

        public FamilyBuilder underEnquiry(Boolean underEnquiry) {
            this.underEnquiry = underEnquiry;
            return this;
        }

        public FamilyBuilder isAgreed(Boolean isAgreed) {
            this.isAgreed = isAgreed;
            return this;
        }

        public FamilyBuilder isClaim(Boolean isClaim) {
            this.isClaim = isClaim;
            return this;
        }

        public FamilyBuilder jksId(String jksId) {
            this.jksId = jksId;
            return this;
        }

        public FamilyBuilder verificationRemarks(String verificationRemarks) {
            this.verificationRemarks = verificationRemarks;
            return this;
        }

        public FamilyBuilder verificationDatetime(LocalDateTime verificationDatetime) {
            this.verificationDatetime = verificationDatetime;
            return this;
        }

        public FamilyBuilder verifiedBy(Long verifiedBy) {
            this.verifiedBy = verifiedBy;
            return this;
        }

        public FamilyBuilder approvalRemarks(String approvalRemarks) {
            this.approvalRemarks = approvalRemarks;
            return this;
        }

        public FamilyBuilder approvalDatetime(LocalDateTime approvalDatetime) {
            this.approvalDatetime = approvalDatetime;
            return this;
        }

        public FamilyBuilder approvedBy(Long approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public FamilyBuilder enquiryRemarks(String enquiryRemarks) {
            this.enquiryRemarks = enquiryRemarks;
            return this;
        }

        public FamilyBuilder enquiryDatetime(LocalDateTime enquiryDatetime) {
            this.enquiryDatetime = enquiryDatetime;
            return this;
        }

        public FamilyBuilder enquiryConductedBy(Long enquiryConductedBy) {
            this.enquiryConductedBy = enquiryConductedBy;
            return this;
        }

        public FamilyBuilder isDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public FamilyBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FamilyBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public FamilyBuilder updatedBy(Long updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public FamilyBuilder members(List<FamilyMember> members) {
            this.members = members;
            return this;
        }

        public Family build() {
            return new Family(id, appSerial, tmpApplicationId, lgdDistrictCode, createdByDistCode,
                    createdByLocalBodyCode, lgdBlockMcCode, lgdGpWardCode, createMode, nextLevelRoleId,
                    applicationStatus, detailStatus, underEnquiry, isAgreed, isClaim, jksId,
                    verificationRemarks, verificationDatetime, verifiedBy, approvalRemarks,
                    approvalDatetime, approvedBy, enquiryRemarks, enquiryDatetime, enquiryConductedBy,
                    isDeleted, createdAt, updatedAt, updatedBy, members);
        }
    }
}
