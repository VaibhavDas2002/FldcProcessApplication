package reqDTO;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Enquiry Officer action request")
public class EnquiryActionRequest {

    @NotNull(message = "familyId is required")
    @Schema(description = "Family UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID familyId;

    @NotNull(message = "districtCode is required")
    @Schema(description = "LGD District Code", example = "304")
    private Integer districtCode;

    @Size(max = 1000, message = "Remarks cannot exceed 1000 characters")
    @Schema(description = "Enquiry remarks", example = "Physical verification completed successfully.")
    private String remarks;

    @Schema(description = "Reject or Revert cause ID (mandatory on revert)", example = "4")
    private Integer rejectRevertCauseId;

    public EnquiryActionRequest() {
    }

    public EnquiryActionRequest(UUID familyId, Integer districtCode, String remarks, Integer rejectRevertCauseId) {
        this.familyId = familyId;
        this.districtCode = districtCode;
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

    public static EnquiryActionRequestBuilder builder() {
        return new EnquiryActionRequestBuilder();
    }

    public static class EnquiryActionRequestBuilder {
        private UUID familyId;
        private Integer districtCode;
        private String remarks;
        private Integer rejectRevertCauseId;

        public EnquiryActionRequestBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public EnquiryActionRequestBuilder districtCode(Integer districtCode) {
            this.districtCode = districtCode;
            return this;
        }

        public EnquiryActionRequestBuilder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public EnquiryActionRequestBuilder rejectRevertCauseId(Integer rejectRevertCauseId) {
            this.rejectRevertCauseId = rejectRevertCauseId;
            return this;
        }

        public EnquiryActionRequest build() {
            return new EnquiryActionRequest(familyId, districtCode, remarks, rejectRevertCauseId);
        }
    }
}
