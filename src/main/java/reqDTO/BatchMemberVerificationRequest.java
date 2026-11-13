package reqDTO;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Batch member verification confirmation request")
public class BatchMemberVerificationRequest {

    @NotNull(message = "familyId is required")
    @Schema(description = "Family UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID familyId;

    @NotNull(message = "districtCode is required")
    @Schema(description = "LGD District Code", example = "304")
    private Integer districtCode;

    @NotEmpty(message = "At least one member action must be provided")
    @Valid
    @Schema(description = "List of member verification decisions")
    private List<MemberVerificationItem> members;

    public BatchMemberVerificationRequest() {
    }

    public BatchMemberVerificationRequest(UUID familyId, Integer districtCode, List<MemberVerificationItem> members) {
        this.familyId = familyId;
        this.districtCode = districtCode;
        this.members = members;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Integer districtCode) {
        this.districtCode = districtCode;
    }

    public List<MemberVerificationItem> getMembers() {
        return members;
    }

    public void setMembers(List<MemberVerificationItem> members) {
        this.members = members;
    }

    public boolean hasRevertAction() {
        if (members == null) return false;
        return members.stream().anyMatch(m -> "REVERT".equalsIgnoreCase(m.getAction()));
    }

    public static BatchMemberVerificationRequestBuilder builder() {
        return new BatchMemberVerificationRequestBuilder();
    }

    public static class BatchMemberVerificationRequestBuilder {
        private UUID familyId;
        private Integer districtCode;
        private List<MemberVerificationItem> members;

        public BatchMemberVerificationRequestBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public BatchMemberVerificationRequestBuilder districtCode(Integer districtCode) {
            this.districtCode = districtCode;
            return this;
        }

        public BatchMemberVerificationRequestBuilder members(List<MemberVerificationItem> members) {
            this.members = members;
            return this;
        }

        public BatchMemberVerificationRequest build() {
            return new BatchMemberVerificationRequest(familyId, districtCode, members);
        }
    }
}
