package service;

import java.util.List;
import java.util.UUID;

import responseHandler.PageResponse;
import security.UserPrincipal;
import reqDTO.QueueFilterRequest;
import resDTO.FamilySummaryResponse;
import resDTO.MemberDetailResponse;
import resDTO.QueueItemResponse;

public interface QueueQueryService {

    PageResponse<QueueItemResponse> getEnquiryQueue(QueueFilterRequest request, UserPrincipal user);

    PageResponse<QueueItemResponse> getVerifierQueue(QueueFilterRequest request, UserPrincipal user);

    PageResponse<QueueItemResponse> getApproverQueue(QueueFilterRequest request, UserPrincipal user);

    PageResponse<QueueItemResponse> getRejectedReProcessQueue(QueueFilterRequest request, UserPrincipal user);

    FamilySummaryResponse getFamilyDetails(UUID familyId, Integer districtCode, UserPrincipal user);

    List<MemberDetailResponse> getEligibleMembers(UUID familyId, Integer districtCode, UserPrincipal user);
}

