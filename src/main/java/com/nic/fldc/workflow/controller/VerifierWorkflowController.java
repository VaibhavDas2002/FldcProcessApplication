package com.nic.fldc.workflow.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nic.fldc.common.api.ApiResponse;
import com.nic.fldc.common.api.PageResponse;
import com.nic.fldc.common.security.UserPrincipal;
import com.nic.fldc.workflow.dto.request.BatchMemberVerificationRequest;
import com.nic.fldc.workflow.dto.request.FamilyActionRequest;
import com.nic.fldc.workflow.dto.request.HoldActionRequest;
import com.nic.fldc.workflow.dto.request.QueueFilterRequest;
import com.nic.fldc.workflow.dto.response.FamilySummaryResponse;
import com.nic.fldc.workflow.dto.response.MemberDetailResponse;
import com.nic.fldc.workflow.dto.response.QueueItemResponse;
import com.nic.fldc.workflow.dto.response.WorkflowActionResponse;
import com.nic.fldc.workflow.service.QueueQueryService;
import com.nic.fldc.workflow.service.WorkflowExecutionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/workflow/verifier")
@Tag(name = "Verifier Workflow", description = "Verifier datatables, family accept/reject/revert/hold, and member verification")
@PreAuthorize("hasAnyRole('VERIFIER', 'ADMIN')")
public class VerifierWorkflowController {

    private final QueueQueryService queryService;
    private final WorkflowExecutionService executionService;

    public VerifierWorkflowController(QueueQueryService queryService, WorkflowExecutionService executionService) {
        this.queryService = queryService;
        this.executionService = executionService;
    }

    @PostMapping("/queue")
    @Operation(summary = "Verifier Tab-Based Queue", description = "Lazy-loads queue by tab (1: Under Enquiry Processed, 2: Pending after Enquiry, 3: Accepted Applications, 4-9: Paid/JKS/Physical/July/Claim/ASAP)")
    public ResponseEntity<ApiResponse<PageResponse<QueueItemResponse>>> getQueue(
            @RequestBody QueueFilterRequest filterRequest,
            @AuthenticationPrincipal UserPrincipal user) {
        PageResponse<QueueItemResponse> response = queryService.getVerifierQueue(filterRequest, user);
        return ResponseEntity.ok(ApiResponse.success("Verifier queue retrieved", response));
    }

    @GetMapping("/{familyId}")
    @Operation(summary = "Get Family Verification Details")
    public ResponseEntity<ApiResponse<FamilySummaryResponse>> getDetails(
            @PathVariable UUID familyId,
            @RequestParam Integer districtCode,
            @AuthenticationPrincipal UserPrincipal user) {
        FamilySummaryResponse response = queryService.getFamilyDetails(familyId, districtCode, user);
        return ResponseEntity.ok(ApiResponse.success("Family details retrieved", response));
    }

    @GetMapping("/{familyId}/eligible-members")
    @Operation(summary = "List Eligible Members for Verification", description = "Returns members eligible for Annapurna Bhandar (Female, 25-60, AY=true, Role=18)")
    public ResponseEntity<ApiResponse<List<MemberDetailResponse>>> getEligibleMembers(
            @PathVariable UUID familyId,
            @RequestParam Integer districtCode,
            @AuthenticationPrincipal UserPrincipal user) {
        List<MemberDetailResponse> response = queryService.getEligibleMembers(familyId, districtCode, user);
        return ResponseEntity.ok(ApiResponse.success("Eligible members retrieved", response));
    }

    @PostMapping("/accept-family")
    @Operation(summary = "Accept Application / Family Details", description = "Sets f and non-approved fm to role 18, ACCEPTED")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> acceptFamily(
            @Valid @RequestBody FamilyActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.verifierAcceptFamily(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/reject-family")
    @Operation(summary = "Reject Family Application", description = "Sets f and non-approved fm to role -100, REJECTED")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> rejectFamily(
            @Valid @RequestBody FamilyActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.verifierRejectFamily(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/revert-family")
    @Operation(summary = "Revert Family Application", description = "Reverts to Operator (role 8) or Citizen (role 6) with detail_status = 99")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> revertFamily(
            @Valid @RequestBody FamilyActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.verifierRevertFamily(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/recommend-enquiry")
    @Operation(summary = "Recommend for Enquiry", description = "Routes back to Enquiry Officer (role 11, min 5 chars remarks)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> recommendEnquiry(
            @Valid @RequestBody FamilyActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.verifierRecommendEnquiry(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/hold-family")
    @Operation(summary = "Further Verification Required / Held-Up Family", description = "Marks on hold (role 17, HELDUP_V, min 15 chars remarks)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> holdFamily(
            @Valid @RequestBody HoldActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.verifierHoldFamily(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/confirm-verification")
    @Operation(summary = "Confirm Verification (Single or Batch with Mixed-Revert Cascade)",
               description = "Applies Verify/Reject on eligible members. If any member has 'Revert', automatically cascades revert to the entire family and all non-approved members.")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> confirmVerification(
            @Valid @RequestBody BatchMemberVerificationRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.verifierConfirmVerification(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/recommend-reject-approved")
    @Operation(summary = "Recommend Approved Member for Rejection", description = "Flags approved beneficiary (role 30 -> 27)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> recommendRejectApproved(
            @RequestParam UUID memberId,
            @RequestParam Integer districtCode,
            @RequestParam String remarks,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.verifierRecommendRejectApproved(memberId, districtCode, remarks, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }
}

