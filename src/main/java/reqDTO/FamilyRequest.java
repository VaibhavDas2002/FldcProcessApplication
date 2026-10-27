package reqDTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyRequest {

    @JsonProperty("districtCode")
    @JsonAlias({"district_code", "districtCode"})
    private Integer districtCode;

    @JsonProperty("familyId")
    @JsonAlias({"family_id", "familyId"})
    private String familyId;

    @JsonProperty("memberId")
    @JsonAlias({"member_id", "memberId"})
    private String memberId;
}
