package workflow;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import responseHandler.ApiResponse;
import security.UserPrincipal;
import reqDTO.SubmitApplicationRequest;
import resDTO.WorkflowActionResponse;
import service.WorkflowExecutionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/workflow/operator")
@Tag(name = "Operator Workflow", description = "Office / CSC operator entry workflow endpoints")
@PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
public class OperatorWorkflowController {

    private final WorkflowExecutionService executionService;

    public OperatorWorkflowController(WorkflowExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/submit")
    @Operation(summary = "Operator Final Submit", description = "Submits draft office entry (role 0 -> 10)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> submit(
            @Valid @RequestBody SubmitApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.submitApplication(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/resubmit")
    @Operation(summary = "Operator Resubmit", description = "Resubmits reverted office application after correction")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> resubmit(
            @Valid @RequestBody SubmitApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.resubmitApplication(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }
}

