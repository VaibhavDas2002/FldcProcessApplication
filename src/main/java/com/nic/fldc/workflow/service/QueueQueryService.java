package com.nic.fldc.workflow.service;

import java.util.List;
import java.util.UUID;

import com.nic.fldc.common.api.PageResponse;
import com.nic.fldc.common.security.UserPrincipal;
import com.nic.fldc.workflow.dto.request.QueueFilterRequest;
import com.nic.fldc.workflow.dto.response.FamilySummaryResponse;
import com.nic.fldc.workflow.dto.response.MemberDetailResponse;
import com.nic.fldc.workflow.dto.response.QueueItemResponse;

public interface QueueQueryService {

    PageResponse<QueueItemResponse> getEnquiryQueue(QueueFilterRequest request, UserPrincipal user);

    PageResponse<QueueItemResponse> getVerifierQueue(QueueFilterRequest request, UserPrincipal user);

    PageResponse<QueueItemResponse> getApproverQueue(QueueFilterRequest request, UserPrincipal user);

    PageResponse<QueueItemResponse> getRejectedReProcessQueue(QueueFilterRequest request, UserPrincipal user);

    FamilySummaryResponse getFamilyDetails(UUID familyId, Integer districtCode, UserPrincipal user);

    List<MemberDetailResponse> getEligibleMembers(UUID familyId, Integer districtCode, UserPrincipal user);
}

