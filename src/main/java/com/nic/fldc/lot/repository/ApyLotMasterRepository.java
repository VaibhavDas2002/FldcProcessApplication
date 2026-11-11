package com.nic.fldc.lot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nic.fldc.lot.entity.ApyLotMaster;

@Repository
public interface ApyLotMasterRepository extends JpaRepository<ApyLotMaster, Long> {

    List<ApyLotMaster> findByLotStatusAndLgdDistrictCode(Integer lotStatus, Integer lgdDistrictCode);

    List<ApyLotMaster> findByLotStatus(Integer lotStatus);
}

