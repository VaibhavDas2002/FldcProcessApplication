package processWorkflow;

import static responseHandler.ResponseHandler.success;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import service.StatusFLDC;

@RestController
@RequestMapping("/api/v2/workflow")
public class ProcessApplication {

    private final StatusFLDC statusFLDC;

    public ProcessApplication(StatusFLDC statusFLDC) {
        this.statusFLDC = statusFLDC;
    }

    @PostMapping("/status")
    public ResponseEntity<List<Map<String, Object>>> getStatus(
            @RequestParam("districtCode") int districtCode,
            @RequestParam("family_id") String familyId,
            @RequestParam("member_id") String memberId) {
        return ResponseEntity.ok(statusFLDC.getStatusByDistrict(districtCode, familyId, memberId));
    }

    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.ok(success("Family members retrieved successfully"));
    }
}
