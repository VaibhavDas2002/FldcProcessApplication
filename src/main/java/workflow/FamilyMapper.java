package workflow;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import resDTO.FamilySummaryResponse;
import resDTO.QueueItemResponse;
import entity.Family;
import workflow.NextLevelRoleId;

@Mapper(componentModel = "spring", uses = {FamilyMemberMapper.class})
public interface FamilyMapper {

    @Mapping(target = "roleStatusName", source = "nextLevelRoleId", qualifiedByName = "familyRoleStatusName")
    @Mapping(target = "members", source = "members")
    FamilySummaryResponse toSummaryResponse(Family family);

    @Mapping(target = "familyId", source = "id")
    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "applyingForAnnapurnaBhandar", ignore = true)
    @Mapping(target = "roleStatusName", source = "nextLevelRoleId", qualifiedByName = "familyRoleStatusName")
    QueueItemResponse toQueueItem(Family family);

    @Named("familyRoleStatusName")
    default String familyRoleStatusName(Integer roleId) {
        NextLevelRoleId role = NextLevelRoleId.fromCode(roleId);
        return (role != null) ? role.getDescription() : "Status Unknown (" + roleId + ")";
    }
}
