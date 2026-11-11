package com.nic.fldc.workflow.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accept_reject_info", schema = "dbt_apy")
public class AcceptRejectInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "family_id", nullable = false, updatable = false)
    private UUID familyId;

    @Column(name = "family_member_id", updatable = false)
    private UUID familyMemberId;

    @Column(name = "lgd_district_code", nullable = false, updatable = false)
    private Integer lgdDistrictCode;

    @Column(name = "op_type", nullable = false, length = 50, updatable = false)
    private String opType;

    @Column(name = "reject_revert_cause_id", updatable = false)
    private Integer rejectRevertCauseId;

    @Column(name = "remarks", columnDefinition = "TEXT", updatable = false)
    private String remarks;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "role_id", updatable = false)
    private Integer roleId;

    @Column(name = "ip_address", length = 45, updatable = false)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AcceptRejectInfo() {
    }

    public AcceptRejectInfo(Long id, UUID familyId, UUID familyMemberId, Integer lgdDistrictCode,
                            String opType, Integer rejectRevertCauseId, String remarks,
                            Long userId, Integer roleId, String ipAddress, LocalDateTime createdAt) {
        this.id = id;
        this.familyId = familyId;
        this.familyMemberId = familyMemberId;
        this.lgdDistrictCode = lgdDistrictCode;
        this.opType = opType;
        this.rejectRevertCauseId = rejectRevertCauseId;
        this.remarks = remarks;
        this.userId = userId;
        this.roleId = roleId;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public UUID getFamilyMemberId() {
        return familyMemberId;
    }

    public void setFamilyMemberId(UUID familyMemberId) {
        this.familyMemberId = familyMemberId;
    }

    public Integer getLgdDistrictCode() {
        return lgdDistrictCode;
    }

    public void setLgdDistrictCode(Integer lgdDistrictCode) {
        this.lgdDistrictCode = lgdDistrictCode;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public Integer getRejectRevertCauseId() {
        return rejectRevertCauseId;
    }

    public void setRejectRevertCauseId(Integer rejectRevertCauseId) {
        this.rejectRevertCauseId = rejectRevertCauseId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static AcceptRejectInfoBuilder builder() {
        return new AcceptRejectInfoBuilder();
    }

    public static class AcceptRejectInfoBuilder {
        private Long id;
        private UUID familyId;
        private UUID familyMemberId;
        private Integer lgdDistrictCode;
        private String opType;
        private Integer rejectRevertCauseId;
        private String remarks;
        private Long userId;
        private Integer roleId;
        private String ipAddress;
        private LocalDateTime createdAt;

        public AcceptRejectInfoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AcceptRejectInfoBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public AcceptRejectInfoBuilder familyMemberId(UUID familyMemberId) {
            this.familyMemberId = familyMemberId;
            return this;
        }

        public AcceptRejectInfoBuilder lgdDistrictCode(Integer lgdDistrictCode) {
            this.lgdDistrictCode = lgdDistrictCode;
            return this;
        }

        public AcceptRejectInfoBuilder opType(String opType) {
            this.opType = opType;
            return this;
        }

        public AcceptRejectInfoBuilder rejectRevertCauseId(Integer rejectRevertCauseId) {
            this.rejectRevertCauseId = rejectRevertCauseId;
            return this;
        }

        public AcceptRejectInfoBuilder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public AcceptRejectInfoBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public AcceptRejectInfoBuilder roleId(Integer roleId) {
            this.roleId = roleId;
            return this;
        }

        public AcceptRejectInfoBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public AcceptRejectInfoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AcceptRejectInfo build() {
            return new AcceptRejectInfo(id, familyId, familyMemberId, lgdDistrictCode, opType, rejectRevertCauseId, remarks, userId, roleId, ipAddress, createdAt);
        }
    }
}
