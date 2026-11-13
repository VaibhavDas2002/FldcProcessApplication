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
import reqDTO.BulkApproveRequest;
import reqDTO.FamilyActionRequest;
import reqDTO.HoldActionRequest;
import reqDTO.QueueFilterRequest;
import resDTO.QueueItemResponse;
import resDTO.WorkflowActionResponse;
import service.QueueQueryService;
import service.WorkflowExecutionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/workflow/approver")
@Tag(name = "Approver Workflow", description = "Approver datatables, member approval, cascade revert, and hold operations")
@PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
public class ApproverWorkflowController {

    private final QueueQueryService queryService;
    private final WorkflowExecutionService executionService;

    public ApproverWorkflowController(QueueQueryService queryService, WorkflowExecutionService executionService) {
        this.queryService = queryService;
        this.executionService = executionService;
    }

    @PostMapping("/queue")
    @Operation(summary = "Approver Queue", description = "Displays verified members (role 20, applying_for_annapurna_bhandar = true) with tab filters")
    public ResponseEntity<ApiResponse<PageResponse<QueueItemResponse>>> getQueue(
            @RequestBody QueueFilterRequest filterRequest,
            @AuthenticationPrincipal UserPrincipal user) {
        PageResponse<QueueItemResponse> response = queryService.getApproverQueue(filterRequest, user);
        return ResponseEntity.ok(ApiResponse.success("Approver queue retrieved", response));
    }

    @PostMapping("/approve-member/{memberId}")
    @Operation(summary = "Approve Single Member", description = "Final approval of verified member (fm role 20 -> 30, APPROVED)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> approveMember(
            @PathVariable UUID memberId,
            @RequestParam Integer districtCode,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.approverApproveMember(memberId, districtCode, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/bulk-approve")
    @Operation(summary = "Bulk Approve Members", description = "Batch approves verified members (role 20 -> 30)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> bulkApprove(
            @Valid @RequestBody BulkApproveRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.approverBulkApprove(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/reject-member/{memberId}")
    @Operation(summary = "Reject Member", description = "Approver rejects verified member (fm role 20 -> -102, REJECTED)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> rejectMember(
            @PathVariable UUID memberId,
            @Valid @RequestBody FamilyActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.approverRejectMember(request, memberId, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/revert-member/{memberId}")
    @Operation(summary = "Revert Member with Family Cascade",
               description = "Reverting a verified member reverts the whole family and all non-approved members to Operator (role 9) or Citizen (role 4) with detail_status = 99.")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> revertMember(
            @PathVariable UUID memberId,
            @Valid @RequestBody FamilyActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.approverRevertCascade(request, memberId, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/hold-family")
    @Operation(summary = "Approver Hold Whole Family", description = "Marks entire family and non-approved members on hold (role 26, HELDUP_A, min 15 chars remarks)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> holdFamily(
            @Valid @RequestBody HoldActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.approverHoldFamily(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/hold-member")
    @Operation(summary = "Approver Hold Single Member", description = "Marks single member on hold (fm role 26, HELDUP_M_A, min 15 chars remarks) without changing family status")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> holdMember(
            @Valid @RequestBody HoldActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.approverHoldSingleMember(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/temp-hold")
    @Operation(summary = "Temporary Hold on Approved Member", description = "Applies temporary hold on role 30 member (role 25, HOLD_<type>)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> tempHold(
            @Valid @RequestBody HoldActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.approverTempHold(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/reject-hold-member/{memberId}")
    @Operation(summary = "Reject Held Member", description = "Rejects member from temporary hold (role 25 -> -104, REJECTED_HOLD_A)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> rejectHoldMember(
            @PathVariable UUID memberId,
            @Valid @RequestBody FamilyActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.approverRejectHoldMember(request, memberId, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/reject-recommended/{memberId}")
    @Operation(summary = "Reject Recommended Member", description = "Rejects member recommended by Verifier for exclusion (role 27 -> -103, REJECTED)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> rejectRecommended(
            @PathVariable UUID memberId,
            @Valid @RequestBody FamilyActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.approverRejectRecommended(request, memberId, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }
}

