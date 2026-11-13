package resDTO;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datatable queue row summary")
public class QueueItemResponse {

    @Schema(description = "Family UUID")
    private UUID familyId;

    @Schema(description = "Member UUID (for member queues)")
    private UUID memberId;

    @Schema(description = "Application Serial Number")
    private Long appSerial;

    @Schema(description = "Temporary Application ID")
    private Long tmpApplicationId;

    @Schema(description = "LGD District Code")
    private Integer lgdDistrictCode;

    @Schema(description = "LGD Block / Municipality Code")
    private Integer lgdBlockMcCode;

    @Schema(description = "LGD Gram Panchayat / Ward Code")
    private Integer lgdGpWardCode;

    @Schema(description = "Create Mode: 'C' or 'O'")
    private String createMode;

    @Schema(description = "Role ID")
    private Integer nextLevelRoleId;

    @Schema(description = "Role status name")
    private String roleStatusName;

    @Schema(description = "Application status string")
    private String applicationStatus;

    @Schema(description = "Under Enquiry flag")
    private Boolean underEnquiry;

    @Schema(description = "JKS ID")
    private String jksId;

    @Schema(description = "Member gender (if member record)")
    private String gender;

    @Schema(description = "Member age (if member record)")
    private Integer age;

    @Schema(description = "Applying for Annapurna Bhandar")
    private Boolean applyingForAnnapurnaBhandar;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;

    public QueueItemResponse() {
    }

    public QueueItemResponse(UUID familyId, UUID memberId, Long appSerial, Long tmpApplicationId,
                             Integer lgdDistrictCode, Integer lgdBlockMcCode, Integer lgdGpWardCode,
                             String createMode, Integer nextLevelRoleId, String roleStatusName,
                             String applicationStatus, Boolean underEnquiry, String jksId,
                             String gender, Integer age, Boolean applyingForAnnapurnaBhandar,
                             LocalDateTime createdAt) {
        this.familyId = familyId;
        this.memberId = memberId;
        this.appSerial = appSerial;
        this.tmpApplicationId = tmpApplicationId;
        this.lgdDistrictCode = lgdDistrictCode;
        this.lgdBlockMcCode = lgdBlockMcCode;
        this.lgdGpWardCode = lgdGpWardCode;
        this.createMode = createMode;
        this.nextLevelRoleId = nextLevelRoleId;
        this.roleStatusName = roleStatusName;
        this.applicationStatus = applicationStatus;
        this.underEnquiry = underEnquiry;
        this.jksId = jksId;
        this.gender = gender;
        this.age = age;
        this.applyingForAnnapurnaBhandar = applyingForAnnapurnaBhandar;
        this.createdAt = createdAt;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static QueueItemResponseBuilder builder() {
        return new QueueItemResponseBuilder();
    }

    public static class QueueItemResponseBuilder {
        private UUID familyId;
        private UUID memberId;
        private Long appSerial;
        private Long tmpApplicationId;
        private Integer lgdDistrictCode;
        private Integer lgdBlockMcCode;
        private Integer lgdGpWardCode;
        private String createMode;
        private Integer nextLevelRoleId;
        private String roleStatusName;
        private String applicationStatus;
        private Boolean underEnquiry;
        private String jksId;
        private String gender;
        private Integer age;
        private Boolean applyingForAnnapurnaBhandar;
        private LocalDateTime createdAt;

        public QueueItemResponseBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public QueueItemResponseBuilder memberId(UUID memberId) {
            this.memberId = memberId;
            return this;
        }

        public QueueItemResponseBuilder appSerial(Long appSerial) {
            this.appSerial = appSerial;
            return this;
        }

        public QueueItemResponseBuilder tmpApplicationId(Long tmpApplicationId) {
            this.tmpApplicationId = tmpApplicationId;
            return this;
        }

        public QueueItemResponseBuilder lgdDistrictCode(Integer lgdDistrictCode) {
            this.lgdDistrictCode = lgdDistrictCode;
            return this;
        }

        public QueueItemResponseBuilder lgdBlockMcCode(Integer lgdBlockMcCode) {
            this.lgdBlockMcCode = lgdBlockMcCode;
            return this;
        }

        public QueueItemResponseBuilder lgdGpWardCode(Integer lgdGpWardCode) {
            this.lgdGpWardCode = lgdGpWardCode;
            return this;
        }

        public QueueItemResponseBuilder createMode(String createMode) {
            this.createMode = createMode;
            return this;
        }

        public QueueItemResponseBuilder nextLevelRoleId(Integer nextLevelRoleId) {
            this.nextLevelRoleId = nextLevelRoleId;
            return this;
        }

        public QueueItemResponseBuilder roleStatusName(String roleStatusName) {
            this.roleStatusName = roleStatusName;
            return this;
        }

        public QueueItemResponseBuilder applicationStatus(String applicationStatus) {
            this.applicationStatus = applicationStatus;
            return this;
        }

        public QueueItemResponseBuilder underEnquiry(Boolean underEnquiry) {
            this.underEnquiry = underEnquiry;
            return this;
        }

        public QueueItemResponseBuilder jksId(String jksId) {
            this.jksId = jksId;
            return this;
        }

        public QueueItemResponseBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public QueueItemResponseBuilder age(Integer age) {
            this.age = age;
            return this;
        }

        public QueueItemResponseBuilder applyingForAnnapurnaBhandar(Boolean applyingForAnnapurnaBhandar) {
            this.applyingForAnnapurnaBhandar = applyingForAnnapurnaBhandar;
            return this;
        }

        public QueueItemResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public QueueItemResponse build() {
            return new QueueItemResponse(familyId, memberId, appSerial, tmpApplicationId,
                    lgdDistrictCode, lgdBlockMcCode, lgdGpWardCode, createMode, nextLevelRoleId,
                    roleStatusName, applicationStatus, underEnquiry, jksId, gender, age,
                    applyingForAnnapurnaBhandar, createdAt);
        }
    }
}
