package com.nic.fldc.workflow.entity;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "family_members", schema = "dbt_apy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Builder.Default
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

    @Builder.Default
    @Column(name = "under_enquiry")
    private Boolean underEnquiry = false;

    @Builder.Default
    @Column(name = "lb_data_paid_apy")
    private Boolean lbDataPaidApy = false;

    @Builder.Default
    @Column(name = "july_payment")
    private Boolean julyPayment = false;

    @Builder.Default
    @Column(name = "is_claim")
    private Boolean isClaim = false;

    @Builder.Default
    @Column(name = "is_asap")
    private Boolean isAsap = false;

    @Builder.Default
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

    @Builder.Default
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

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", insertable = false, updatable = false)
    private Family family;
}

