package com.nic.fldc.workflow.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.nic.fldc.workflow.dto.request.QueueFilterRequest;
import com.nic.fldc.workflow.entity.Family;
import com.nic.fldc.workflow.entity.FamilyMember;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class ApproverQueueSpecification {

    public static Specification<FamilyMember> filter(QueueFilterRequest request, Integer userDistrictCode) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Soft delete filter
            predicates.add(cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));

            // 2. Member baseline: role 20, applying_for_annapurna_bhandar = true
            predicates.add(cb.equal(root.get("nextLevelRoleId"), 20));
            predicates.add(cb.isTrue(root.get("applyingForAnnapurnaBhandar")));

            // Join with Family
            Join<FamilyMember, Family> familyJoin = root.join("family", JoinType.INNER);
            predicates.add(cb.or(cb.isNull(familyJoin.get("isDeleted")), cb.isFalse(familyJoin.get("isDeleted"))));
            predicates.add(cb.equal(familyJoin.get("nextLevelRoleId"), 18)); // Family must be accepted

            // 3. LGD Jurisdiction boundary
            Integer district = (request.getDistrictCode() != null) ? request.getDistrictCode() : userDistrictCode;
            if (district != null) {
                predicates.add(cb.equal(root.get("lgdDistrictCode"), district));
            }
            if (request.getBlockCode() != null) {
                predicates.add(cb.equal(root.get("lgdBlockMcCode"), request.getBlockCode()));
            }
            if (request.getGpCode() != null) {
                predicates.add(cb.equal(root.get("lgdGpWardCode"), request.getGpCode()));
            }

            // 4. Tab selection
            int tabIndex = (request.getTabIndex() != null) ? request.getTabIndex() : 0;
            switch (tabIndex) {
                case 1 -> { // Under Enquiry Verified
                    predicates.add(cb.or(cb.isTrue(familyJoin.get("underEnquiry")), cb.isTrue(root.get("underEnquiry"))));
                }
                case 2 -> { // Verified / After Verification (non-enquiry)
                    predicates.add(cb.isFalse(familyJoin.get("underEnquiry")));
                    predicates.add(cb.isFalse(root.get("underEnquiry")));
                }
                case 3 -> { // Paid on June
                    predicates.add(cb.isTrue(root.get("lbDataPaidApy")));
                }
                case 5 -> { // JKS Entry
                    predicates.add(cb.isNotNull(familyJoin.get("jksId")));
                    predicates.add(cb.notEqual(familyJoin.get("jksId"), "0000000000000"));
                }
                case 6 -> { // Physical Application Earlier
                    predicates.add(cb.equal(familyJoin.get("jksId"), "0000000000000"));
                }
                case 7 -> { // July Lot
                    predicates.add(cb.isTrue(root.get("julyPayment")));
                }
                case 8 -> { // Claim
                    predicates.add(cb.or(cb.isTrue(familyJoin.get("isClaim")), cb.isTrue(root.get("isClaim"))));
                }
                case 9 -> { // ASAP
                    predicates.add(cb.isTrue(root.get("isAsap")));
                }
                default -> {
                    // Default All: no extra tab condition
                }
            }

            // 5. Search Filter
            if (request.getSearch() != null && !request.getSearch().isBlank()) {
                String searchPattern = "%" + request.getSearch().trim().toLowerCase() + "%";
                Predicate searchPred = cb.or(
                        cb.like(cb.lower(familyJoin.get("jksId")), searchPattern),
                        cb.like(cb.lower(root.get("id").as(String.class)), searchPattern)
                );
                predicates.add(searchPred);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

