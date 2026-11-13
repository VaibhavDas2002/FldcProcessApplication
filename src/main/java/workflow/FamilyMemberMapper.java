package workflow;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import resDTO.MemberDetailResponse;
import resDTO.QueueItemResponse;
import entity.FamilyMember;
import workflow.NextLevelRoleId;

@Mapper(componentModel = "spring")
public interface FamilyMemberMapper {

    @Mapping(target = "roleStatusName", source = "nextLevelRoleId", qualifiedByName = "memberRoleStatusName")
    @Mapping(target = "eligibleForAction", source = "member", qualifiedByName = "checkEligibility")
    MemberDetailResponse toDetailResponse(FamilyMember member);

    @Mapping(target = "memberId", source = "id")
    @Mapping(target = "familyId", source = "familyId")
    @Mapping(target = "roleStatusName", source = "nextLevelRoleId", qualifiedByName = "memberRoleStatusName")
    @Mapping(target = "appSerial", ignore = true)
    @Mapping(target = "tmpApplicationId", ignore = true)
    @Mapping(target = "jksId", ignore = true)
    QueueItemResponse toQueueItem(FamilyMember member);

    @Named("memberRoleStatusName")
    default String memberRoleStatusName(Integer roleId) {
        NextLevelRoleId role = NextLevelRoleId.fromCode(roleId);
        return (role != null) ? role.getDescription() : "Status Unknown (" + roleId + ")";
    }

    @Named("checkEligibility")
    default Boolean checkEligibility(FamilyMember m) {
        if (m == null) return false;
        boolean isFemale = "F".equalsIgnoreCase(m.getGender());
        boolean isAy = Boolean.TRUE.equals(m.getApplyingForAnnapurnaBhandar());
        boolean isRole18 = m.getNextLevelRoleId() != null && m.getNextLevelRoleId() == 18;
        int age = (m.getAge() != null) ? m.getAge() : 0;
        boolean ageOk = age >= 25 && age <= 60;
        return isFemale && isAy && isRole18 && ageOk;
    }
}
