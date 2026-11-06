package resDTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyStatusResponse {

    @JsonProperty("roleStatusName")
    private String roleStatusName;

    @JsonProperty("nextLevelRoleId")
    private Integer nextLevelRoleId;

    @JsonProperty("members")
    private List<GetStatus> members;
}
