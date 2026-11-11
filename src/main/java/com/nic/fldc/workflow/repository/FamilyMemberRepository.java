package com.nic.fldc.workflow.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nic.fldc.workflow.entity.FamilyMember;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID>, JpaSpecificationExecutor<FamilyMember> {

    List<FamilyMember> findByFamilyIdAndIsDeletedFalse(UUID familyId);

    Optional<FamilyMember> findByIdAndLgdDistrictCodeAndIsDeletedFalse(UUID id, Integer lgdDistrictCode);

    @Query("""
        SELECT fm FROM FamilyMember fm
        WHERE fm.familyId = :familyId
          AND fm.nextLevelRoleId <> :excludedRoleId
          AND (fm.isDeleted = false OR fm.isDeleted IS NULL)
    """)
    List<FamilyMember> findNonApprovedMembers(@Param("familyId") UUID familyId, @Param("excludedRoleId") Integer excludedRoleId);

    List<FamilyMember> findByFamilyIdAndApplyingForAnnapurnaBhandarTrueAndIsDeletedFalse(UUID familyId);

    long countByFamilyIdAndIsDeletedFalse(UUID familyId);

    long countByFamilyIdAndNextLevelRoleIdInAndIsDeletedFalse(UUID familyId, Collection<Integer> roleIds);
}

