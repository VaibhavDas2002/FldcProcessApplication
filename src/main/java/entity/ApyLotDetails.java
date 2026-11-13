package entity;

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
@Table(name = "ay_lot_details", schema = "cross_check")
public class ApyLotDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "lot_id", nullable = false)
    private Long lotId;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "family_member_id", nullable = false)
    private UUID familyMemberId;

    @Column(name = "lgd_district_code", nullable = false)
    private Integer lgdDistrictCode;

    @Column(name = "is_rejected")
    private Boolean isRejected = false;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ApyLotDetails() {
    }

    public ApyLotDetails(Long id, Long lotId, UUID familyId, UUID familyMemberId,
                         Integer lgdDistrictCode, Boolean isRejected, Long approvedBy,
                         LocalDateTime approvedAt, LocalDateTime createdAt) {
        this.id = id;
        this.lotId = lotId;
        this.familyId = familyId;
        this.familyMemberId = familyMemberId;
        this.lgdDistrictCode = lgdDistrictCode;
        this.isRejected = isRejected != null ? isRejected : false;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
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

    public Boolean getIsRejected() {
        return isRejected;
    }

    public Boolean isRejected() {
        return isRejected;
    }

    public void setIsRejected(Boolean isRejected) {
        this.isRejected = isRejected;
    }

    public void setRejected(Boolean isRejected) {
        this.isRejected = isRejected;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static ApyLotDetailsBuilder builder() {
        return new ApyLotDetailsBuilder();
    }

    public static class ApyLotDetailsBuilder {
        private Long id;
        private Long lotId;
        private UUID familyId;
        private UUID familyMemberId;
        private Integer lgdDistrictCode;
        private Boolean isRejected = false;
        private Long approvedBy;
        private LocalDateTime approvedAt;
        private LocalDateTime createdAt;

        public ApyLotDetailsBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ApyLotDetailsBuilder lotId(Long lotId) {
            this.lotId = lotId;
            return this;
        }

        public ApyLotDetailsBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public ApyLotDetailsBuilder familyMemberId(UUID familyMemberId) {
            this.familyMemberId = familyMemberId;
            return this;
        }

        public ApyLotDetailsBuilder lgdDistrictCode(Integer lgdDistrictCode) {
            this.lgdDistrictCode = lgdDistrictCode;
            return this;
        }

        public ApyLotDetailsBuilder isRejected(Boolean isRejected) {
            this.isRejected = isRejected;
            return this;
        }

        public ApyLotDetailsBuilder approvedBy(Long approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public ApyLotDetailsBuilder approvedAt(LocalDateTime approvedAt) {
            this.approvedAt = approvedAt;
            return this;
        }

        public ApyLotDetailsBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ApyLotDetails build() {
            return new ApyLotDetails(id, lotId, familyId, familyMemberId, lgdDistrictCode, isRejected, approvedBy, approvedAt, createdAt);
        }
    }
}
