package resDTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FamilyStatusResponse {

    @JsonProperty("roleStatusName")
    private String roleStatusName;

    @JsonProperty("nextLevelRoleId")
    private Integer nextLevelRoleId;

    @JsonProperty("members")
    private List<GetStatus> members;

    public FamilyStatusResponse() {
    }

    public FamilyStatusResponse(String roleStatusName, Integer nextLevelRoleId, List<GetStatus> members) {
        this.roleStatusName = roleStatusName;
        this.nextLevelRoleId = nextLevelRoleId;
        this.members = members;
    }

    public String getRoleStatusName() {
        return roleStatusName;
    }

    public void setRoleStatusName(String roleStatusName) {
        this.roleStatusName = roleStatusName;
    }

    public Integer getNextLevelRoleId() {
        return nextLevelRoleId;
    }

    public void setNextLevelRoleId(Integer nextLevelRoleId) {
        this.nextLevelRoleId = nextLevelRoleId;
    }

    public List<GetStatus> getMembers() {
        return members;
    }

    public void setMembers(List<GetStatus> members) {
        this.members = members;
    }

    public static FamilyStatusResponseBuilder builder() {
        return new FamilyStatusResponseBuilder();
    }

    public static class FamilyStatusResponseBuilder {
        private String roleStatusName;
        private Integer nextLevelRoleId;
        private List<GetStatus> members;

        public FamilyStatusResponseBuilder roleStatusName(String roleStatusName) {
            this.roleStatusName = roleStatusName;
            return this;
        }

        public FamilyStatusResponseBuilder nextLevelRoleId(Integer nextLevelRoleId) {
            this.nextLevelRoleId = nextLevelRoleId;
            return this;
        }

        public FamilyStatusResponseBuilder members(List<GetStatus> members) {
            this.members = members;
            return this;
        }

        public FamilyStatusResponse build() {
            return new FamilyStatusResponse(roleStatusName, nextLevelRoleId, members);
        }
    }
}
