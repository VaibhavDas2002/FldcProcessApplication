package processWorkflow;

import static responseHandler.ResponseHandler.success;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import resDTO.GetFamilyInput;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v2/test")
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.ok(success("Family members retrieved successfully"));

    }

    @PostMapping("/jsonInput")
    public ResponseEntity<?> getJsonInput(@RequestBody GetFamilyInput entity) {
       return ResponseEntity.ok(success("Sucess", Map.of("lgd_district_code", entity.getLgd_district_code(),"family_id", entity.getFamily_id())));

    }
    
}
