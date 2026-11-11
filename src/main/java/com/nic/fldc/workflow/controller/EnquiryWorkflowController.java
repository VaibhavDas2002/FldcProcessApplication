package com.nic.fldc.workflow.controller;

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
import com.nic.fldc.workflow.dto.request.EnquiryActionRequest;
import com.nic.fldc.workflow.dto.request.QueueFilterRequest;
import com.nic.fldc.workflow.dto.response.FamilySummaryResponse;
import com.nic.fldc.workflow.dto.response.QueueItemResponse;
import com.nic.fldc.workflow.dto.response.WorkflowActionResponse;
import com.nic.fldc.workflow.service.QueueQueryService;
import com.nic.fldc.workflow.service.WorkflowExecutionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/workflow/enquiry")
@Tag(name = "Enquiry Officer Workflow", description = "Enquiry queues and verification actions")
@PreAuthorize("hasAnyRole('ENQUIRY_OFFICER', 'ADMIN')")
public class EnquiryWorkflowController {

    private final QueueQueryService queryService;
    private final WorkflowExecutionService executionService;

    public EnquiryWorkflowController(QueueQueryService queryService, WorkflowExecutionService executionService) {
        this.queryService = queryService;
        this.executionService = executionService;
    }

    @PostMapping("/queue")
    @Operation(summary = "Enquiry Officer Datatable Queue", description = "Lazy-loads queue by active tab (1: Under Enquiry, 2: Office, 3: Online, 4: APY Paid, 5: Recommended from Verifier)")
    public ResponseEntity<ApiResponse<PageResponse<QueueItemResponse>>> getQueue(
            @RequestBody QueueFilterRequest filterRequest,
            @AuthenticationPrincipal UserPrincipal user) {
        PageResponse<QueueItemResponse> response = queryService.getEnquiryQueue(filterRequest, user);
        return ResponseEntity.ok(ApiResponse.success("Queue fetched successfully", response));
    }

    @GetMapping("/{familyId}")
    @Operation(summary = "Get Family Details", description = "Fetches family snapshot with member details")
    public ResponseEntity<ApiResponse<FamilySummaryResponse>> getDetails(
            @PathVariable UUID familyId,
            @RequestParam Integer districtCode,
            @AuthenticationPrincipal UserPrincipal user) {
        FamilySummaryResponse response = queryService.getFamilyDetails(familyId, districtCode, user);
        return ResponseEntity.ok(ApiResponse.success("Details retrieved", response));
    }

    @PostMapping("/recommend-accept")
    @Operation(summary = "Recommend for Accept", description = "Forwards to Verifier as accepted (role 15, ENQUIRY_PROCESSED)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> recommendAccept(
            @Valid @RequestBody EnquiryActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.enquiryRecommendAccept(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/recommend-reject")
    @Operation(summary = "Recommend for Rejection", description = "Forwards to Verifier as rejected (role 13, ENQUIRY_REJECTED)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> recommendReject(
            @Valid @RequestBody EnquiryActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.enquiryRecommendReject(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/revert")
    @Operation(summary = "Revert Application", description = "Reverts to Operator (role 7) or Citizen (role 5)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> revert(
            @Valid @RequestBody EnquiryActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.enquiryRevert(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/edit-save")
    @Operation(summary = "Enquiry Form Edit & Save", description = "Updates enquiry details and forwards to Verifier (role 15)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> editSave(
            @Valid @RequestBody EnquiryActionRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.enquiryEditSave(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }
}

