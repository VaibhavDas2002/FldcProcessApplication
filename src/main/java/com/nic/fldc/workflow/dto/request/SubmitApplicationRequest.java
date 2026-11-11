package com.nic.fldc.workflow.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Citizen or Operator final submission request")
public class SubmitApplicationRequest {

    @NotNull(message = "familyId is required")
    @Schema(description = "Family UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID familyId;

    @NotNull(message = "districtCode is required")
    @Schema(description = "LGD District Code", example = "304")
    private Integer districtCode;

    @Schema(description = "Citizen / applicant declaration agreement", example = "true")
    private Boolean isAgreed = true;

    public SubmitApplicationRequest() {
    }

    public SubmitApplicationRequest(UUID familyId, Integer districtCode, Boolean isAgreed) {
        this.familyId = familyId;
        this.districtCode = districtCode;
        this.isAgreed = isAgreed != null ? isAgreed : true;
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

    public Boolean getIsAgreed() {
        return isAgreed;
    }

    public void setIsAgreed(Boolean isAgreed) {
        this.isAgreed = isAgreed;
    }

    public static SubmitApplicationRequestBuilder builder() {
        return new SubmitApplicationRequestBuilder();
    }

    public static class SubmitApplicationRequestBuilder {
        private UUID familyId;
        private Integer districtCode;
        private Boolean isAgreed = true;

        public SubmitApplicationRequestBuilder familyId(UUID familyId) {
            this.familyId = familyId;
            return this;
        }

        public SubmitApplicationRequestBuilder districtCode(Integer districtCode) {
            this.districtCode = districtCode;
            return this;
        }

        public SubmitApplicationRequestBuilder isAgreed(Boolean isAgreed) {
            this.isAgreed = isAgreed;
            return this;
        }

        public SubmitApplicationRequest build() {
            return new SubmitApplicationRequest(familyId, districtCode, isAgreed);
        }
    }
}
