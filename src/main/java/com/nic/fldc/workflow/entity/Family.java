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
              