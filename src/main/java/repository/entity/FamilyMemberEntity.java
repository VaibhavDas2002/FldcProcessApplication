package repository.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "family_members", schema = "dbt_apy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
