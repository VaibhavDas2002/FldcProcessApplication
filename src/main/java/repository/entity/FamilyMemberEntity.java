package repository.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "family_members", schema = "dbt_apy")
public class FamilyMemberEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "family_id")
    private UUID familyId;

    @Column(name = "family_serial")
    private Long familySerial;

    @Column(name = "lgd_district_code")
    private Integer lgdDistrictCode;

    @Column(name = "created_by_dist_code")
    private Integer createdByDistCode;

    @Column(name = "created_by_local_body_code")
    private Long createdByLocalBodyCode;

    @Column(name = "next_level_role_id")
    private Integer nextLevelRoleId;

    @Column(name = "application_status")
    private String applicationStatus;

    @Column(name = "verification_datetime")
    private LocalDateTime verificationDatetime;

    @Column(name = "approval_datetime")
    private LocalDateTime approvalDatetime;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public FamilyMemberEntity() {
    }

    public FamilyMemberEntity(UUID id, UUID familyId, Long familySerial, Integer lgdDistrictCode,
                              Integer createdByDistCode, Long createdByLocalBodyCode, Integer nextLevelRoleId,
                              String applicationStatus, LocalDateTime verificationDatetime,
                              LocalDateTime approvalDatetime, Boolean isDeleted) {
        this.id = id;
        this.familyId = familyId;
        this.familySerial = familySerial;
        this.lgdDistrictCode = lgdDistrictCode;
        this.createdByDistCode = createdByDistCode;
        this.createdByLocalBodyCode = createdByLocalBodyCode;
        this.nextLevelRoleId = nextLevelRoleId;
        this.applicationStatus = applicationStatus;
        this.verificationDatetime = verificationDatetime;
        this.approvalDatetime = approvalDatetime;
        this.isDeleted = isDeleted;
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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public static FamilyMemberEntityBuilder builder() {
        return new FamilyMemberEntityBuilder();
    }

    public static class FamilyMemberEntityBuilder {
        private UUID id;
        private UUID familyId;
        private Long familySerial;
        private Integer lgdDistrictCode;
        private Integer createdByDistCode;
        private Long createdByLocalBodyCode;
        private Integer nextLevelRoleId;
        private String applicationStatus;
        private LocalDateTime verificationDatetime;
        private LocalDateTime approvalDatetime;
        private Boolean isDeleted;

        public FamilyMemberEntityBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public FamilyMemberEntityBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public FamilyMemberEntityBuilder familySerial(Long familySerial) {
            this.familySerial = familySerial;
            return this;
        }

        public FamilyMemberEntityBuilder lgdDistrictCode(Integer lgdDistrictCode) {
            this.lgdDistrictCode = lgdDistrictCode;
            return this;
        }

        public FamilyMemberEntityBuilder createdByDistCode(Integer createdByDistCode) {
            this.createdByDistCode = createdByDistCode;
            return this;
        }

        public FamilyMemberEntityBuilder createdByLocalBodyCode(Long createdByLocalBodyCode) {
            this.createdByLocalBodyCode = createdByLocalBodyCode;
            return this;
        }

        public FamilyMemberEntityBuilder nextLevelRoleId(Integer nextLevelRoleId) {
            this.nextLevelRoleId = nextLevelRoleId;
            return this;
        }

        public FamilyMemberEntityBuilder applicationStatus(String applicationStatus) {
            this.applicationStatus = applicationStatus;
            return this;
        }

        public FamilyMemberEntityBuilder verificationDatetime(LocalDateTime verificationDatetime) {
            this.verificationDatetime = verificationDatetime;
            return this;
        }

        public FamilyMemberEntityBuilder approvalDatetime(LocalDateTime approvalDatetime) {
            this.approvalDatetime = approvalDatetime;
            return this;
        }

        public FamilyMemberEntityBuilder isDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public FamilyMemberEntity build() {
            return new FamilyMemberEntity(id, familyId, familySerial, lgdDistrictCode, createdByDistCode,
                    createdByLocalBodyCode, nextLevelRoleId, applicationStatus, verificationDatetime,
                    approvalDatetime, isDeleted);
        }
    }
}
