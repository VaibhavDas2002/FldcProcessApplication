package reqDTO;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Bulk member approval request")
public class BulkApproveRequest {

    @NotNull(message = "districtCode is required")
    @Schema(description = "LGD District Code", example = "304")
    private Integer districtCode;

    @NotEmpty(message = "At least one memberId must be provided for approval")
    @Schema(description = "List of verified member UUIDs to approve")
    private List<UUID> memberIds;

    public BulkApproveRequest() {
    }

    public BulkApproveRequest(Integer districtCode, List<UUID> memberIds) {
        this.districtCode = districtCode;
        this.memberIds = memberIds;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Integer districtCode) {
        this.districtCode = districtCode;
    }

    public List<UUID> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<UUID> memberIds) {
        this.memberIds = memberIds;
    }

    public static BulkApproveRequestBuilder builder() {
        return new BulkApproveRequestBuilder();
    }

    public static class BulkApproveRequestBuilder {
        private Integer districtCode;
        private List<UUID> memberIds;

        public BulkApproveRequestBuilder districtCode(Integer districtCode) {
            this.districtCode = districtCode;
            return this;
        }

        public BulkApproveRequestBuilder memberIds(List<UUID> memberIds) {
            this.memberIds = memberIds;
            return this;
        }

        public BulkApproveRequest build() {
            return new BulkApproveRequest(districtCode, memberIds);
        }
    }
}
