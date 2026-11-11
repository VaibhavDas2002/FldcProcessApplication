package com.nic.fldc.workflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nic.fldc.common.api.ApiResponse;
import com.nic.fldc.common.security.UserPrincipal;
import com.nic.fldc.workflow.dto.request.SubmitApplicationRequest;
import com.nic.fldc.workflow.dto.response.WorkflowActionResponse;
import com.nic.fldc.workflow.service.WorkflowExecutionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/workflow/citizen")
@Tag(name = "Citizen Workflow", description = "Online citizen entry workflow endpoints")
@PreAuthorize("hasAnyRole('CITIZEN', 'ADMIN')")
public class CitizenWorkflowController {

    private final WorkflowExecutionService executionService;

    public CitizenWorkflowController(WorkflowExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/submit")
    @Operation(summary = "Citizen Final Submit", description = "Submits draft application (role 0 -> 10)")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> submit(
            @Valid @RequestBody SubmitApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.submitApplication(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/resubmit")
    @Operation(summary = "Citizen Resubmit", description = "Resubmits reverted application after correction")
    public ResponseEntity<ApiResponse<WorkflowActionResponse>> resubmit(
            @Valid @RequestBody SubmitApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest) {
        String clientIp = httpRequest != null ? httpRequest.getRemoteAddr() : null;
        WorkflowActionResponse response = executionService.resubmitApplication(request, user, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }
}

