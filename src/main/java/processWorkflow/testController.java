package processWorkflow;

import static responseHandler.ResponseHandler.success;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v2/test")
public class testController {
	
	  @GetMapping("/test")
	    public ResponseEntity<?> getTest() {
	        return ResponseEntity.ok(success("Every Thing is fine"));
	    }

}
