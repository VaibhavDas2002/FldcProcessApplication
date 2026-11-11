package com.nic.fldc.workflow.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nic.fldc.workflow.entity.LbDataPaidApy;

@Repository
public interface LbDataPaidApyRepository extends JpaRepository<LbDataPaidApy, Long> {

    Optional<LbDataPaidApy> findByFamilyIdAndLgdDistrictCode(UUID familyId, Integer lgdDistrictCode);

    boolean existsByFamilyIdAndLgdDistrictCode(UUID familyId, Integer lgdDistrictCode);
}

