package entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ay_lot_master", schema = "cross_check")
public class ApyLotMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "lot_no", length = 50)
    private String lotNo;

    @Column(name = "lgd_district_code", nullable = false)
    private Integer lgdDistrictCode;

    @Column(name = "total_members")
    private Integer totalMembers;

    @Column(name = "lot_status")
    private Integer lotStatus = 0; // 0 = Pending, 2 = Approved, -3 = Rejected

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approver_remarks", columnDefinition = "TEXT")
    private String approverRemarks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ApyLotMaster() {
    }

    public ApyLotMaster(Long id, String lotNo, Integer lgdDistrictCode, Integer totalMembers,
                        Integer lotStatus, Long approvedBy, LocalDateTime approvedAt,
                        String approverRemarks, LocalDateTime createdAt) {
        this.id = id;
        this.lotNo = lotNo;
        this.lgdDistrictCode = lgdDistrictCode;
        this.totalMembers = totalMembers;
        this.lotStatus = lotStatus != null ? lotStatus : 0;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.approverRemarks = approverRemarks;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLotNo() {
        return lotNo;
    }

    public void setLotNo(String lotNo) {
        this.lotNo = lotNo;
    }

    public Integer getLgdDistrictCode() {
        return lgdDistrictCode;
    }

    public void setLgdDistrictCode(Integer lgdDistrictCode) {
        this.lgdDistrictCode = lgdDistrictCode;
    }

    public Integer getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Integer totalMembers) {
        this.totalMembers = totalMembers;
    }

    public Integer getLotStatus() {
        return lotStatus;
    }

    public void setLotStatus(Integer lotStatus) {
        this.lotStatus = lotStatus;
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

    public String getApproverRemarks() {
        return approverRemarks;
    }

    public void setApproverRemarks(String approverRemarks) {
        this.approverRemarks = approverRemarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static ApyLotMasterBuilder builder() {
        return new ApyLotMasterBuilder();
    }

    public static class ApyLotMasterBuilder {
        private Long id;
        private String lotNo;
        private Integer lgdDistrictCode;
        private Integer totalMembers;
        private Integer lotStatus = 0;
        private Long approvedBy;
        private LocalDateTime approvedAt;
        private String approverRemarks;
        private LocalDateTime createdAt;

        public ApyLotMasterBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ApyLotMasterBuilder lotNo(String lotNo) {
            this.lotNo = lotNo;
            return this;
        }

        public ApyLotMasterBuilder lgdDistrictCode(Integer lgdDistrictCode) {
            this.lgdDistrictCode = lgdDistrictCode;
            return this;
        }

        public ApyLotMasterBuilder totalMembers(Integer totalMembers) {
            this.totalMembers = totalMembers;
            return this;
        }

        public ApyLotMasterBuilder lotStatus(Integer lotStatus) {
            this.lotStatus = lotStatus;
            return this;
        }

        public ApyLotMasterBuilder approvedBy(Long approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public ApyLotMasterBuilder approvedAt(LocalDateTime approvedAt) {
            this.approvedAt = approvedAt;
            return this;
        }

        public ApyLotMasterBuilder approverRemarks(String approverRemarks) {
            this.approverRemarks = approverRemarks;
            return this;
        }

        public ApyLotMasterBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ApyLotMaster build() {
            return new ApyLotMaster(id, lotNo, lgdDistrictCode, totalMembers, lotStatus, approvedBy, approvedAt, approverRemarks, createdAt);
        }
    }
}
