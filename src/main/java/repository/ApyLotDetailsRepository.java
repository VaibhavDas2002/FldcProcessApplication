package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.ApyLotDetails;

@Repository
public interface ApyLotDetailsRepository extends JpaRepository<ApyLotDetails, Long> {

    List<ApyLotDetails> findByLotId(Long lotId);
}

