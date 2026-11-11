package com.nic.fldc.workflow.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.nic.fldc.workflow.dto.request.QueueFilterRequest;
import com.nic.fldc.workflow.entity.Family;
import com.nic.fldc.workflow.entity.LbDataPaidApy;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class EnquiryQueueSpecification {

    public static Specification<Family> filter(QueueFilterRequest request, Integer userDistrictCode) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Soft delete filter
            predicates.add(cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));

            // 2. LGD Jurisdiction boundary
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

            // 3. Tab-based queue rules
            int tabIndex = (request.getTabIndex() != null) ? request.getTabIndex() : 1;
            switch (tabIndex) {
                case 1 -> { // Tab a: Under Enquiry Queue
                    predicates.add(cb.isTrue(root.get("underEnquiry")));
                    predicates.add(cb.equal(root.get("nextLevelRoleId"), 10));
                }
                case 2 -> { // Tab b: Office Application Queue
                    predicates.add(cb.or(
                            cb.equal(cb.upper(root.get("createMode")), "O"),
                            cb.equal(cb.lower(root.get("createMode")), "o")
                    ));
                    predicates.add(cb.isFalse(root.get("underEnquiry")));
                    predicates.add(cb.equal(root.get("nextLevelRoleId"), 10));
                }
                case 3 -> { // Tab c: Online Application Queue
                    predicates.add(cb.or(
                            cb.equal(cb.upper(root.get("createMode")), "C"),
                            cb.equal(cb.lower(root.get("createMode")), "c")
                    ));
                    predicates.add(cb.isFalse(root.get("underEnquiry")));
                    predicates.add(cb.equal(root.get("nextLevelRoleId"), 10));
                }
                case 4 -> { // Tab d: Priority / APY Paid (cross_check.lb_data_paid_apy)
                    predicates.add(cb.isFalse(root.get("underEnquiry")));
                    predicates.add(cb.equal(root.get("nextLevelRoleId"), 10));

                    Subquery<Integer> subquery = query.subquery(Integer.class);
                    Root<LbDataPaidApy> paidRoot = subquery.from(LbDataPaidApy.class);
                    subquery.select(cb.literal(1))
                            .where(
                                    cb.equal(paidRoot.get("familyId"), root.get("id")),
                                    cb.equal(paidRoot.get("lgdDistrictCode"), root.get("lgdDistrictCode"))
                            );
                    predicates.add(cb.exists(subquery));
                }
                case 5 -> { // Tab e: Recommended for Enquiry from Verifier
                    predicates.add(cb.equal(root.get("nextLevelRoleId"), 11));
                }
                default -> { // Default fallback: Role 10
                    predicates.add(cb.equal(root.get("nextLevelRoleId"), 10));
                }
            }

            // 4. Search Filter
            if (request.getSearch() != null && !request.getSearch().isBlank()) {
                String searchPattern = "%" + request.getSearch().trim().toLowerCase() + "%";
                Predicate searchPred = cb.or(
                        cb.like(cb.lower(root.get("jksId")), searchPattern),
                        cb.like(cb.lower(root.get("id").as(String.class)), searchPattern)
                );
                predicates.add(searchPred);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

