package resDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyAcceptResponse {

    @JsonProperty("family_id")
    private String familyId;

    @JsonProperty("app_serial")
    private Object appSerial;

    @JsonProperty("affected_members")
    private Integer affectedMembers;
}
