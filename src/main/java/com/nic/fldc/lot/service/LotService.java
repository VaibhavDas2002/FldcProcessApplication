package com.nic.fldc.lot.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nic.fldc.common.exception.InvalidWorkflowTransitionException;
import com.nic.fldc.common.exception.ResourceNotFoundException;
import com.nic.fldc.lot.entity.ApyLotDetails;
import com.nic.fldc.lot.entity.ApyLotMaster;
import com.nic.fldc.lot.repository.ApyLotDetailsRepository;
import com.nic.fldc.lot.repository.ApyLotMasterRepository;

@Service
public class LotService {

    private static final Logger log = LoggerFactory.getLogger(LotService.class);

    private final ApyLotMasterRepository lotMasterRepo;
    private final ApyLotDetailsRepository lotDetailsRepo;

    @Value("${fldc.features.lot-member-update-enabled:false}")
    private boolean lotMemberUpdateEnabled;

    public LotService(ApyLotMasterRepository lotMasterRepo, ApyLotDetailsRepository lotDetailsRepo) {
        this.lotMasterRepo = lotMasterRepo;
        this.lotDetailsRepo = lotDetailsRepo;
    }

    public List<ApyLotMaster> viewPendingLots(Integer districtCode) {
        if (districtCode != null) {
            return lotMasterRepo.findByLotStatusAndLgdDistrictCode(0, districtCode);
        }
        return lotMasterRepo.findByLotStatus(0);
    }

    @Transactional
    public ApyLotMaster approveLot(Long lotId, Long userId) {
        ApyLotMaster lot = lotMasterRepo.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Lot", lotId));

        if (lot.getLotStatus() != 0) {
            throw new InvalidWorkflowTransitionException("Lot is not in Pending status (status=" + lot.getLotStatus() + ")");
        }

        LocalDateTime now = LocalDateTime.now();
        lot.setLotStatus(2); // 2 = Approved
        lot.setApprovedBy(userId);
        lot.setApprovedAt(now);
        lotMasterRepo.save(lot);

        List<ApyLotDetails> details = lotDetailsRepo.findByLotId(lotId);
        for (ApyLotDetails d : details) {
            d.setApprovedBy(userId);
            d.setApprovedAt(now);
            lotDetailsRepo.save(d);
        }

        if (lotMemberUpdateEnabled) {
            log.info("Member status update for lot {} enabled by feature flag.", lotId);
        } else {
            log.info("Member status update for lot {} is DISABLED in specification.", lotId);
        }

        return lot;
    }

    @Transactional
    public ApyLotMaster rejectLot(Long lotId, String remarks, Long userId) {
        if (remarks == null || remarks.trim().length() < 3) {
            throw new InvalidWorkflowTransitionException("Remarks must be at least 3 characters for lot rejection.");
        }

        ApyLotMaster lot = lotMasterRepo.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Lot", lotId));

        LocalDateTime now = LocalDateTime.now();
        lot.setLotStatus(-3); // -3 = Rejected
        lot.setApprovedBy(userId);
        lot.setApprovedAt(now);
        lot.setApproverRemarks(remarks);
        return lotMasterRepo.save(lot);
    }

    @Transactional
    public ApyLotDetails toggleRejectMember(Long detailId) {
        ApyLotDetails detail = lotDetailsRepo.findById(detailId)
                .orElseThrow(() -> new ResourceNotFoundException("LotDetail", detailId));
        detail.setIsRejected(!Boolean.TRUE.equals(detail.getIsRejected()));
        return lotDetailsRepo.save(detail);
    }
}

