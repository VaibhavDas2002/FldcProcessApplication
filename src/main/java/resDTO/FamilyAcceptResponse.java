package resDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FamilyAcceptResponse {

    @JsonProperty("family_id")
    private String familyId;

    @JsonProperty("app_serial")
    private Object appSerial;

    @JsonProperty("affected_members")
    private Integer affectedMembers;

    public FamilyAcceptResponse() {
    }

    public FamilyAcceptResponse(String familyId, Object appSerial, Integer affectedMembers) {
        this.familyId = familyId;
        this.appSerial = appSerial;
        this.affectedMembers = affectedMembers;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public Object getAppSerial() {
        return appSerial;
    }

    public void setAppSerial(Object appSerial) {
        this.appSerial = appSerial;
    }

    public Integer getAffectedMembers() {
        return affectedMembers;
    }

    public void setAffectedMembers(Integer affectedMembers) {
        this.affectedMembers = affectedMembers;
    }

    public static FamilyAcceptResponseBuilder builder() {
        return new FamilyAcceptResponseBuilder();
    }

    public static class FamilyAcceptResponseBuilder {
        private String familyId;
        private Object appSerial;
        private Integer affectedMembers;

        public FamilyAcceptResponseBuilder familyId(String familyId) {
            this.familyId = familyId;
            return this;
        }

        public FamilyAcceptResponseBuilder appSerial(Object appSerial) {
            this.appSerial = appSerial;
            return this;
        }

        public FamilyAcceptResponseBuilder affectedMembers(Integer affectedMembers) {
            this.affectedMembers = affectedMembers;
            return this;
        }

        public FamilyAcceptResponse build() {
            return new FamilyAcceptResponse(familyId, appSerial, affectedMembers);
        }
    }
}
