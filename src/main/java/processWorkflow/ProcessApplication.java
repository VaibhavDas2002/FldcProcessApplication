package processWorkflow;

import static responseHandler.ResponseHandler.error;
import static responseHandler.ResponseHandler.success;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import repository.processApplication;
import reqDTO.FamilyRequest;
import reqDTO.MemberRequest;
import resDTO.GetFamilyInput;
import service.StatusFLDC;
import service.ZoneDatabaseManager;

/**
 * REST Controller for Process Application Workflow.
 * Responsible for HTTP endpoint routing and Request Validation.
 * Delegates Business Logic, Transactions, and DTO mapping to service.ProcessApplication.
 */
@RestController
@RequestMapping("/api/v2/workflow")
public class ProcessApplication {

    private final service.ProcessApplication processApplicationService;

    public ProcessApplication() {
        this(new service.ProcessApplication());
    }

    public ProcessApplication(StatusFLDC statusFLDC) {
        this(new service.ProcessApplication(statusFLDC));
    }

    public ProcessApplication(StatusFLDC statusFLDC, ZoneDatabaseManager zoneDatabaseManager) {
        this(new service.ProcessApplication(statusFLDC, zoneDatabaseManager));
    }

    public ProcessApplication(
            StatusFLDC statusFLDC,
            ZoneDatabaseManager zoneDatabaseManager,
            processApplication processApplicationRepo) {
        this(new service.ProcessApplication(statusFLDC, zoneDatabaseManager, processApplicationRepo));
    }

    @Autowired
    public ProcessApplication(service.ProcessApplication processApplicationService) {
        this.processApplicationService = (processApplicationService != null)
                ? processApplicationService
                : new service.ProcessApplication();
    }

    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.ok(success("Family members retrieved successfully"));
    }

    /**
     * Endpoint for retrieving family member status.
     * Validates request parameters and delegates to Service.
     */
    @PostMapping("/status")
    public ResponseEntity<?> getStatus(
            @RequestParam("districtCode") int districtCode,
            @RequestParam("family_id") String familyId,
            @RequestParam("member_id") String memberId) {

        if (districtCode <= 0) {
            return ResponseEntity.badRequest().body(error("Invalid districtCode", HttpStatus.BAD_REQUEST));
        }
        if (familyId == null || familyId.isBlank()) {
            return ResponseEntity.badRequest().body(error("family_id is required", HttpStatus.BAD_REQUEST));
        }
        if (memberId == null || memberId.isBlank()) {
            return ResponseEntity.badRequest().body(error("member_id is required", HttpStatus.BAD_REQUEST));
        }

        return processApplicationService.getStatus(districtCode, familyId, memberId);
    }

    /**
     * Endpoint for accepting a family application.
     * Performs request validation on GetFamilyInput DTO and delegates to Service.
     */
    @PostMapping("/acceptFamily")
    public ResponseEntity<?> acceptFamily(
            @Valid @RequestBody(required = false) GetFamilyInput data,
            HttpServletRequest request) {

        String ipAddress = null;
        if (request != null) {
            String forwarded = request.getHeader("X-Forwarded-For");
            ipAddress = (forwarded != null && !forwarded.isBlank())
                    ? forwarded.split(",")[0].trim()
                    : request.getRemoteAddr();
        }

        return processApplicationService.acceptFamily(data, ipAddress);
    }

    public ResponseEntity<?> acceptFamily(GetFamilyInput data) {
        return acceptFamily(data, null);
    }

    /**
     * Endpoint for retrieving family or member status with FamilyRequest DTO.
     * Uses @Valid for Bean Validation and delegates to Service.
     */
    @PostMapping
    public ResponseEntity<?> getStatusByFamily(@Valid @RequestBody(required = false) FamilyRequest request) {
        if (request == null || request.getDistrictCode() == null || request.getFamilyId() == null) {
            return ResponseEntity.badRequest()
                    .body(error("districtCode and familyId are required in request body", HttpStatus.BAD_REQUEST));
        }
        return processApplicationService.getStatusByFamily(request);
    }

    /**
     * Endpoint for retrieving member status with MemberRequest DTO.
     * Uses @Valid for Bean Validation and delegates to Service.
     */
    @PostMapping("/member")
    public ResponseEntity<?> getStatusByMember(
            @Valid @RequestBody MemberRequest request,
            BindingResult bindingResult) {

        if (bindingResult != null && bindingResult.hasErrors()) {
            String firstError = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(error(firstError, HttpStatus.BAD_REQUEST));
        }

        return processApplicationService.getStatusByMember(request);
    }

    public service.ProcessApplication getProcessApplicationService() {
        return processApplicationService;
    }
}
