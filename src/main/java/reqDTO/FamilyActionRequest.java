package reqDTO;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Family-level workflow action request")
public class FamilyActionRequest {

    @NotNull(message = "familyId is required")
    @Schema(description = "Family UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID familyId;

    @NotNull(message = "districtCode is required")
    @Schema(description = "LGD District Code", example = "304")
    private Integer districtCode;

    @Schema(description = "Created by Local Body Code")
    private Long localBodyCode;

    @Size(max = 1000, message = "Remarks cannot exceed 1000 characters")
    @Schema(description = "Action remarks (mandatory for Recommend for Enquiry: min 5 chars; Hold: min 15 chars)", example = "Verified original ration card and income certificate.")
    private String remarks;

    @Schema(description = "Reject or Revert cause ID", example = "12")
    private Integer rejectRevertCauseId;

    public FamilyActionRequest() {
    }

    public FamilyActionRequest(UUID familyId, Integer districtCode, Long localBodyCode, String remarks, Integer rejectRevertCauseId) {
        this.familyId = familyId;
        this.districtCode = districtCode;
        this.localBodyCode = localBodyCode;
        this.remarks = remarks;
        this.rejectRevertCauseId = rejectRevertCauseId;
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

    public Long getLocalBodyCode() {
        return localBodyCode;
    }

    public void setLocalBodyCode(Long localBodyCode) {
        this.localBodyCode = localBodyCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getRejectRevertCauseId() {
        return rejectRevertCauseId;
    }

    public void setRejectRevertCauseId(Integer rejectRevertCauseId) {
        this.rejectRevertCauseId = rejectRevertCauseId;
    }

    public static FamilyActionRequestBuilder builder() {
        return new FamilyActionRequestBuilder();
    }

    public static class FamilyActionRequestBuilder {
        private UUID familyId;
        private Integer districtCode;
        private Long localBodyCode;
        private String remarks;
        private Integer rejectRevertCauseId;

        public FamilyActionRequestBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public FamilyActionRequestBuilder districtCode(Integer districtCode) {
            this.districtCode = districtCode;
            return this;
        }

        public FamilyActionRequestBuilder localBodyCode(Long localBodyCode) {
            this.localBodyCode = localBodyCode;
            return this;
        }

        public FamilyActionRequestBuilder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public FamilyActionRequestBuilder rejectRevertCauseId(Integer rejectRevertCauseId) {
            this.rejectRevertCauseId = rejectRevertCauseId;
            return this;
        }

        public FamilyActionRequest build() {
            return new FamilyActionRequest(familyId, districtCode, localBodyCode, remarks, rejectRevertCauseId);
        }
    }
}
