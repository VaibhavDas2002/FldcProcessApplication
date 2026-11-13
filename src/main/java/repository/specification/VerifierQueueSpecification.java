package repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import reqDTO.QueueFilterRequest;
import entity.Family;

import jakarta.persistence.criteria.Predicate;

public class VerifierQueueSpecification {

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

            // 3. Entry mode filter if specified
            if (request.getCreateMode() != null && !request.getCreateMode().isBlank()) {
                String mode = request.getCreateMode().trim().toUpperCase();
                predicates.add(cb.or(
                        cb.equal(cb.upper(root.get("createMode")), mode),
                        cb.equal(cb.lower(root.get("createMode")), mode.toLowerCase())
                ));
            }

            // 4. Tab selection
            int tabIndex = (request.getTabIndex() != null) ? request.getTabIndex() : 1;
            switch (tabIndex) {
                case 1 -> { // Tab 1: Under Enquiry Processed (role 13 or 15, under_enquiry = true)
                    predicates.add(root.get("nextLevelRoleId").in(13, 15));
                    predicates.add(cb.isTrue(root.get("underEnquiry")));
                }
                case 2 -> { // Tab 2: Pending after Enquiry (role 13 or 15, under_enquiry = false)
                    predicates.add(root.get("nextLevelRoleId").in(13, 15));
                    predicates.add(cb.isFalse(root.get("underEnquiry")));
                }
                case 3 -> { // Tab 3: Accepted Applications (role 18)
                    predicates.add(cb.equal(root.get("nextLevelRoleId"), 18));
                }
                case 5 -> { // JKS Entry (role 13 or 15, jks_id is not null and <> 0000000000000)
                    predicates.add(root.get("nextLevelRoleId").in(13, 15));
                    predicates.add(cb.isNotNull(root.get("jksId")));
                    predicates.add(cb.notEqual(root.get("jksId"), "0000000000000"));
                }
                case 6 -> { // Physical Application Earlier (role 13 or 15, jks_id = 0000000000000)
                    predicates.add(root.get("nextLevelRoleId").in(13, 15));
                    predicates.add(cb.equal(root.get("jksId"), "0000000000000"));
                }
                case 8 -> { // Claim Applications
                    predicates.add(root.get("nextLevelRoleId").in(13, 15));
                    predicates.add(cb.isTrue(root.get("isClaim")));
                }
                default -> { // Default All Pending
                    predicates.add(root.get("nextLevelRoleId").in(13, 15));
                }
            }

            // 5. Search Filter
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

