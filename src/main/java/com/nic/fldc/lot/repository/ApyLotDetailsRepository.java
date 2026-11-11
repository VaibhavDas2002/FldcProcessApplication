package com.nic.fldc.lot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nic.fldc.lot.entity.ApyLotDetails;

@Repository
public interface ApyLotDetailsRepository extends JpaRepository<ApyLotDetails, Long> {

    List<ApyLotDetails> findByLotId(Long lotId);
}

