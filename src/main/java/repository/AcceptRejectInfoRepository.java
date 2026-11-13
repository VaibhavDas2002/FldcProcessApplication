package repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.AcceptRejectInfo;

@Repository
public interface AcceptRejectInfoRepository extends JpaRepository<AcceptRejectInfo, Long> {

    List<AcceptRejectInfo> findByFamilyIdOrderByCreatedAtDesc(UUID familyId);

    List<AcceptRejectInfo> findByFamilyMemberIdOrderByCreatedAtDesc(UUID familyMemberId);
}

