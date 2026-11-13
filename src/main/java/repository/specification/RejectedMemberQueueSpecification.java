package repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import reqDTO.QueueFilterRequest;
import entity.FamilyMember;

import jakarta.persistence.criteria.Predicate;

public class RejectedMemberQueueSpecification {

    public static Specification<FamilyMember> filter(QueueFilterRequest request, Integer userDistrictCode) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Soft delete filter
            predicates.add(cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));

            // 2. Previously rejected role IDs: -100, -101, -102, -103
            predicates.add(root.get("nextLevelRoleId").in(-100, -101, -102, -103));

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

            // 4. Search Filter
            if (request.getSearch() != null && !request.getSearch().isBlank()) {
                String searchPattern = "%" + request.getSearch().trim().toLowerCase() + "%";
                Predicate searchPred = cb.like(cb.lower(root.get("id").as(String.class)), searchPattern);
                predicates.add(searchPred);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

