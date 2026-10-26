package resDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class GetStatus {
    private Object next_level_role_id;
    private Object approval_datetime;
    private Object verification_datetime;
}
