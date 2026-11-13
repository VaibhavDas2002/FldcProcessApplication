package com.nic.fldc.legacy.reqDTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MemberRequest {

    @NotNull(message = "District code is required")
    @JsonProperty("districtCode")
    @JsonAlias({ "district_code", "districtCode", "lgd_district_code" })
    private Integer districtCode;

    @NotBlank(message = "Family id is required")
    @JsonProperty("familyId")
    @JsonAlias({ "family_id", "familyId" })
    private String familyId;

    @NotBlank(message = "Member id is required")
    @JsonProperty("memberId")
    @JsonAlias({ "member_id", "memberId" })
    private String memberId;

    @JsonProperty("localBodyCode")
    @JsonAlias({ "local_body_code", "localBodyCode" })
    private Object localBodyCode;

    @JsonProperty("appserial")
    @JsonAlias({ "app_serial", "appSerial" })
    private Integer appserial;

    @JsonProperty("familyserial")
    @JsonAlias({ "family_serial", "familySerial" })
    private Integer familyserial;

    public MemberRequest() {
    }

    public MemberRequest(Integer districtCode, String familyId, String memberId, Object localBodyCode,
                         Integer appserial, Integer familyserial) {
        this.districtCode = districtCode;
        this.familyId = familyId;
        this.memberId = memberId;
        this.localBodyCode = localBodyCode;
        this.appserial = appserial;
        this.familyserial = familyserial;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Integer districtCode) {
        this.districtCode = districtCode;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Object getLocalBodyCode() {
        return localBodyCode;
    }

    public void setLocalBodyCode(Object localBodyCode) {
        this.localBodyCode = localBodyCode;
    }

    public Integer getAppserial() {
        return appserial;
    }

    public void setAppserial(Integer appserial) {
        this.appserial = appserial;
    }

    public Integer getFamilyserial() {
        return familyserial;
    }

    public void setFamilyserial(Integer familyserial) {
        this.familyserial = familyserial;
    }

    public static MemberRequestBuilder builder() {
        return new MemberRequestBuilder();
    }

    public static class MemberRequestBuilder {
        private Integer districtCode;
        private String familyId;
        private String memberId;
        private Object localBodyCode;
        private Integer appserial;
        private Integer familyserial;

        public MemberRequestBuilder districtCode(Integer districtCode) {
            this.districtCode = districtCode;
            return this;
        }

        public MemberRequestBuilder familyId(String familyId) {
            this.familyId = familyId;
            return this;
        }

        public MemberRequestBuilder memberId(String memberId) {
            this.memberId = memberId;
            return this;
        }

        public MemberRequestBuilder localBodyCode(Object localBodyCode) {
            this.localBodyCode = localBodyCode;
            return this;
        }

        public MemberRequestBuilder appserial(Integer appserial) {
            this.appserial = appserial;
            return this;
        }

        public MemberRequestBuilder familyserial(Integer familyserial) {
            this.familyserial = familyserial;
            return this;
        }

        public MemberRequest build() {
            return new MemberRequest(districtCode, familyId, memberId, localBodyCode, appserial, familyserial);
        }
    }
}
