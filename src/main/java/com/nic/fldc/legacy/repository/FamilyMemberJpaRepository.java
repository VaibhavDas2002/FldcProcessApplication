package com.nic.fldc.legacy.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nic.fldc.legacy.repository.entity.FamilyMemberEntity;

@Repository
public interface FamilyMemberJpaRepository extends JpaRepository<FamilyMemberEntity, UUID> {

    /**
     * Custom Query: Find active members by district code and family ID.
     */
    @Query("""
        SELECT m FROM FamilyMemberEntity m
        WHERE m.lgdDistrictCode = :districtCode
          AND m.familyId = :familyId
          AND (m.isDeleted = false OR m.isDeleted IS NULL)
    """)
    List<FamilyMemberEntity> findCustomByDistrictAndFamily(
            @Param("districtCode") Integer districtCode,
            @Param("familyId") UUID familyId);

    /**
     * Custom Query: Find specific active member status by district, family and member ID.
     */
    @Query("""
        SELECT m FROM FamilyMemberEntity m
        WHERE m.lgdDistrictCode = :districtCode
          AND m.familyId = :familyId
          AND m.id = :memberId
          AND (m.isDeleted = false OR m.isDeleted IS NULL)
    """)
    List<FamilyMemberEntity> findCustomStatusByDistrictAndMember(
            @Param("districtCode") Integer districtCode,
            @Param("familyId") UUID familyId,
            @Param("memberId") UUID memberId);

    /**
     * Custom Query: Find members by family ID.
     */
    @Query("""
        SELECT m FROM FamilyMemberEntity m
        WHERE m.familyId = :familyId
          AND (m.isDeleted = false OR m.isDeleted IS NULL)
    """)
    List<FamilyMemberEntity> findCustomByFamilyId(@Param("familyId") UUID familyId);
}
