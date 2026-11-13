package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "family_members", schema = "dbt_apy")
public class FamilyMember {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "family_serial")
    private Long familySerial;

    @Column(name = "gender", length = 1)
    private String gender;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "age")
    private Integer age;

    @Column(name = "applying_for_annapurna_bhandar")
    private Boolean applyingForAnnapurnaBhandar = false;

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

    @Column(name = "lb_data_paid_apy")
    private Boolean lbDataPaidApy = false;

    @Column(name = "july_payment")
    private Boolean julyPayment = false;

    @Column(name = "is_claim")
    private Boolean isClaim = false;

    @Column(name = "is_asap")
    private Boolean isAsap = false;

    @Column(name = "not_in_asdd")
    private Boolean notInAsdd = false;

    @Column(name = "appeal_tribunal", columnDefinition = "TEXT")
    private String appealTribunal;

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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", insertable = false, updatable = false)
    private Family family;

    public FamilyMember() {
    }

    public FamilyMember(UUID id, UUID familyId, Long familySerial, String gender, LocalDate dob,
                        Integer age, Boolean applyingForAnnapurnaBhandar, Integer lgdDistrictCode,
                        Integer createdByDistCode, Long createdByLocalBodyCode, Integer lgdBlockMcCode,
                        Integer lgdGpWardCode, String createMode, Integer nextLevelRoleId,
                        String applicationStatus, Integer detailStatus, Boolean underEnquiry,
                        Boolean lbDataPaidApy, Boolean julyPayment, Boolean isClaim, Boolean isAsap,
                        Boolean notInAsdd, String appealTribunal, String verificationRemarks,
                        LocalDateTime verificationDatetime, Long verifiedBy, String approvalRemarks,
                        LocalDateTime approvalDatetime, Long approvedBy, String enquiryRemarks,
                        LocalDateTime enquiryDatetime, Long enquiryConductedBy, Boolean isDeleted,
                        LocalDateTime createdAt, LocalDateTime updatedAt, Long updatedBy,
                        Family family) {
        this.id = id;
        this.familyId = familyId;
        this.familySerial = familySerial;
        this.gender = gender;
        this.dob = dob;
        this.age = age;
        this.applyingForAnnapurnaBhandar = applyingForAnnapurnaBhandar != null ? applyingForAnnapurnaBhandar : false;
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
        this.lbDataPaidApy = lbDataPaidApy != null ? lbDataPaidApy : false;
        this.julyPayment = julyPayment != null ? julyPayment : false;
        this.isClaim = isClaim != null ? isClaim : false;
        this.isAsap = isAsap != null ? isAsap : false;
        this.notInAsdd = notInAsdd != null ? notInAsdd : false;
        this.appealTribunal = appealTribunal;
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
        this.family = family;
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

    public Boolean getLbDataPaidApy() {
        return lbDataPaidApy;
    }

    public void setLbDataPaidApy(Boolean lbDataPaidApy) {
        this.lbDataPaidApy = lbDataPaidApy;
    }

    public Boolean getJulyPayment() {
        return julyPayment;
    }

    public void setJulyPayment(Boolean julyPayment) {
        this.julyPayment = julyPayment;
    }

    public Boolean getIsClaim() {
        return isClaim;
    }

    public void setIsClaim(Boolean isClaim) {
        this.isClaim = isClaim;
    }

    public Boolean getIsAsap() {
        return isAsap;
    }

    public void setIsAsap(Boolean isAsap) {
        this.isAsap = isAsap;
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

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public static FamilyMemberBuilder builder() {
        return new FamilyMemberBuilder();
    }

    public static class FamilyMemberBuilder {
        private UUID id;
        private UUID familyId;
        private Long familySerial;
        private String gender;
        private LocalDate dob;
        private Integer age;
        private Boolean applyingForAnnapurnaBhandar = false;
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
        private Boolean lbDataPaidApy = false;
        private Boolean julyPayment = false;
        private Boolean isClaim = false;
        private Boolean isAsap = false;
        private Boolean notInAsdd = false;
        private String appealTribunal;
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
        private Family family;

        public FamilyMemberBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public FamilyMemberBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public FamilyMemberBuilder familySerial(Long familySerial) {
            this.familySerial = familySerial;
            return this;
        }

        public FamilyMemberBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public FamilyMemberBuilder dob(LocalDate dob) {
            this.dob = dob;
            return this;
        }

        public FamilyMemberBuilder age(Integer age) {
            this.age = age;
            return this;
        }

        public FamilyMemberBuilder applyingForAnnapurnaBhandar(Boolean applyingForAnnapurnaBhandar) {
            this.applyingForAnnapurnaBhandar = applyingForAnnapurnaBhandar;
            return this;
        }

        public FamilyMemberBuilder lgdDistrictCode(Integer lgdDistrictCode) {
            this.lgdDistrictCode = lgdDistrictCode;
            return this;
        }

        public FamilyMemberBuilder createdByDistCode(Integer createdByDistCode) {
            this.createdByDistCode = createdByDistCode;
            return this;
        }

        public FamilyMemberBuilder createdByLocalBodyCode(Long createdByLocalBodyCode) {
            this.createdByLocalBodyCode = createdByLocalBodyCode;
            return this;
        }

        public FamilyMemberBuilder lgdBlockMcCode(Integer lgdBlockMcCode) {
            this.lgdBlockMcCode = lgdBlockMcCode;
            return this;
        }

        public FamilyMemberBuilder lgdGpWardCode(Integer lgdGpWardCode) {
            this.lgdGpWardCode = lgdGpWardCode;
            return this;
        }

        public FamilyMemberBuilder createMode(String createMode) {
            this.createMode = createMode;
            return this;
        }

        public FamilyMemberBuilder nextLevelRoleId(Integer nextLevelRoleId) {
            this.nextLevelRoleId = nextLevelRoleId;
            return this;
        }

        public FamilyMemberBuilder applicationStatus(String applicationStatus) {
            this.applicationStatus = applicationStatus;
            return this;
        }

        public FamilyMemberBuilder detailStatus(Integer detailStatus) {
            this.detailStatus = detailStatus;
            return this;
        }

        public FamilyMemberBuilder underEnquiry(Boolean underEnquiry) {
            this.underEnquiry = underEnquiry;
            return this;
        }

        public FamilyMemberBuilder lbDataPaidApy(Boolean lbDataPaidApy) {
            this.lbDataPaidApy = lbDataPaidApy;
            return this;
        }

        public FamilyMemberBuilder julyPayment(Boolean julyPayment) {
            this.julyPayment = julyPayment;
            return this;
        }

        public FamilyMemberBuilder isClaim(Boolean isClaim) {
            this.isClaim = isClaim;
            return this;
        }

        public FamilyMemberBuilder isAsap(Boolean isAsap) {
            this.isAsap = isAsap;
            return this;
        }

        public FamilyMemberBuilder notInAsdd(Boolean notInAsdd) {
            this.notInAsdd = notInAsdd;
            return this;
        }

        public FamilyMemberBuilder appealTribunal(String appealTribunal) {
            this.appealTribunal = appealTribunal;
            return this;
        }

        public FamilyMemberBuilder verificationRemarks(String verificationRemarks) {
            this.verificationRemarks = verificationRemarks;
            return this;
        }

        public FamilyMemberBuilder verificationDatetime(LocalDateTime verificationDatetime) {
            this.verificationDatetime = verificationDatetime;
            return this;
        }

        public FamilyMemberBuilder verifiedBy(Long verifiedBy) {
            this.verifiedBy = verifiedBy;
            return this;
        }

        public FamilyMemberBuilder approvalRemarks(String approvalRemarks) {
            this.approvalRemarks = approvalRemarks;
            return this;
        }

        public FamilyMemberBuilder approvalDatetime(LocalDateTime approvalDatetime) {
            this.approvalDatetime = approvalDatetime;
            return this;
        }

        public FamilyMemberBuilder approvedBy(Long approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public FamilyMemberBuilder enquiryRemarks(String enquiryRemarks) {
            this.enquiryRemarks = enquiryRemarks;
            return this;
        }

        public FamilyMemberBuilder enquiryDatetime(LocalDateTime enquiryDatetime) {
            this.enquiryDatetime = enquiryDatetime;
            return this;
        }

        public FamilyMemberBuilder enquiryConductedBy(Long enquiryConductedBy) {
            this.enquiryConductedBy = enquiryConductedBy;
            return this;
        }

        public FamilyMemberBuilder isDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public FamilyMemberBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FamilyMemberBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public FamilyMemberBuilder updatedBy(Long updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public FamilyMemberBuilder family(Family family) {
            this.family = family;
            return this;
        }

        public FamilyMember build() {
            return new FamilyMember(id, familyId, familySerial, gender, dob, age, applyingForAnnapurnaBhandar,
                    lgdDistrictCode, createdByDistCode, createdByLocalBodyCode, lgdBlockMcCode, lgdGpWardCode,
                    createMode, nextLevelRoleId, applicationStatus, detailStatus, underEnquiry, lbDataPaidApy,
                    julyPayment, isClaim, isAsap, notInAsdd, appealTribunal, verificationRemarks,
                    verificationDatetime, verifiedBy, approvalRemarks, approvalDatetime, approvedBy,
                    enquiryRemarks, enquiryDatetime, enquiryConductedBy, isDeleted, createdAt,
                    updatedAt, updatedBy, family);
        }
    }
}
