package com.nic.fldc.workflow.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nic.fldc.workflow.entity.Family;

@Repository
public interface FamilyRepository extends JpaRepository<Family, UUID>, JpaSpecificationExecutor<Family> {

    Optional<Family> findByIdAndLgdDistrictCodeAndIsDeletedFalse(UUID id, Integer lgdDistrictCode);

    @Query("SELECT f FROM Family f WHERE f.id = :id AND (f.isDeleted = false OR f.isDeleted IS NULL)")
    Optional<Family> findActiveById(@Param("id") UUID id);
}

