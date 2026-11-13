package entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "lb_data_paid_apy", schema = "cross_check")
public class LbDataPaidApy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "lgd_district_code", nullable = false)
    private Integer lgdDistrictCode;

    public LbDataPaidApy() {
    }

    public LbDataPaidApy(Long id, UUID familyId, Integer lgdDistrictCode) {
        this.id = id;
        this.familyId = familyId;
        this.lgdDistrictCode = lgdDistrictCode;
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

    public Integer getLgdDistrictCode() {
        return lgdDistrictCode;
    }

    public void setLgdDistrictCode(Integer lgdDistrictCode) {
        this.lgdDistrictCode = lgdDistrictCode;
    }

    public static LbDataPaidApyBuilder builder() {
        return new LbDataPaidApyBuilder();
    }

    public static class LbDataPaidApyBuilder {
        private Long id;
        private UUID familyId;
        private Integer lgdDistrictCode;

        public LbDataPaidApyBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LbDataPaidApyBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public LbDataPaidApyBuilder lgdDistrictCode(Integer lgdDistrictCode) {
            this.lgdDistrictCode = lgdDistrictCode;
            return this;
        }

        public LbDataPaidApy build() {
            return new LbDataPaidApy(id, familyId, lgdDistrictCode);
        }
    }
}
