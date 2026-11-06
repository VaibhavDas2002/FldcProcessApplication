package reqDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
}
