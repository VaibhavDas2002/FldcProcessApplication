package reqDTO;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Hold workflow action request")
public class HoldActionRequest {

    @NotNull(message = "familyId is required")
    @Schema(description = "Family UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID familyId;

    @Schema(description = "Family member UUID (optional if holding entire family)")
    private UUID memberId;

    @NotNull(message = "districtCode is required")
    @Schema(description = "LGD District Code", example = "304")
    private Integer districtCode;

    @NotBlank(message = "Remarks are mandatory for Hold actions")
    @Size(min = 15, max = 1000, message = "Remarks must be at least 15 characters for Hold actions")
    @Schema(description = "Remarks detailing reasons for hold (min 15 characters)", example = "Bank account details mismatch with Aadhaar card.")
    private String remarks;

    @Schema(description = "Hold type classification (e.g. AADHAAR, BANK, GENERAL)", example = "GENERAL")
    private String holdType;

    public HoldActionRequest() {
    }

    public HoldActionRequest(UUID familyId, UUID memberId, Integer districtCode, String remarks, String holdType) {
        this.familyId = familyId;
        this.memberId = memberId;
        this.districtCode = districtCode;
        this.remarks = remarks;
        this.holdType = holdType;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
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

    public String getHoldType() {
        return holdType;
    }

    public void setHoldType(String holdType) {
        this.holdType = holdType;
    }

    public static HoldActionRequestBuilder builder() {
        return new HoldActionRequestBuilder();
    }

    public static class HoldActionRequestBuilder {
        private UUID familyId;
        private UUID memberId;
        private Integer districtCode;
        private String remarks;
        private String holdType;

        public HoldActionRequestBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public HoldActionRequestBuilder memberId(UUID memberId) {
            this.memberId = memberId;
            return this;
        }

        public HoldActionRequestBuilder districtCode(Integer districtCode) {
            this.districtCode = districtCode;
            return this;
        }

        public HoldActionRequestBuilder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public HoldActionRequestBuilder holdType(String holdType) {
            this.holdType = holdType;
            return this;
        }

        public HoldActionRequest build() {
            return new HoldActionRequest(familyId, memberId, districtCode, remarks, holdType);
        }
    }
}
