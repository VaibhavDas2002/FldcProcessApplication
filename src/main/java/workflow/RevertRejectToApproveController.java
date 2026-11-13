package workflow;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import responseHandler.ApiResponse;
import responseHandler.PageResponse;
import security.UserPrincipal;
import reqDTO.QueueFilterRequest;
import resDTO.QueueItemResponse;
import resDTO.WorkflowActionResponse;
import service.QueueQueryService;
import service.WorkflowExecutionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/workflow/reprocess")
@Tag(name = "Post-Rejection Re-processing", description = "Administrative workflow for re-approving or re-enquiring previously rejected records")
@PreAuthorize("hasRole('ADMIN')")
public class RevertRejectToApproveController {

    private final QueueQueryService queryService;
    private final WorkflowExecutionService executionService;

    public RevertRejectToApproveController(QueueQueryService queryService, WorkflowExecutionService executionService) {
        this.queryService = queryService;
        this.executionService = executionService;
    }

    @PostMapping("/queue")
    @Operation(summary = "Rejected Applications Queue", description = "Lists records with role in (-100, -101, -102, -103)")
    public ResponseEntity<ApiResponse<PageResponse<QueueItemResponse>>> getQueue(
            @RequestBody QueueFilterRequest filterRequest,
            @AuthenticationPrincipal UserPrincipal user) {
        PageResponse<QueueItemResponse> response = queryService.getRejectedReProcessQueue(filterRequest, user);
        return ResponseEntity.ok(ApiResponse.success("Rejected members queue retrieved", response));
    }

    @PostMapping("/re-approve/{memberId}")
    @Operation(summary = "Re-Approve from Rejected", description = "Approves previously rejected member (role 30, APPROVE). If whole family is rejected, family becomes 18 ACCEPTED.")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> reApprove(
            @PathVariable UUID memberId,
            @RequestParam Integer districtCode,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.adminApproveFromReject(memberId, districtCode, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/send-to-enquiry/{familyId}")
    @Operation(summary = "Send Back to Enquiry", description = "Re-opens rejected case and resets family and members to role 10, 'REJECT TO ENQUIRY' with min 5 chars remarks")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> sendToEnquiry(
            @PathVariable UUID familyId,
            @RequestParam Integer districtCode,
            @RequestParam String remarks,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.adminSendToEnquiry(familyId, districtCode, remarks, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }
}

