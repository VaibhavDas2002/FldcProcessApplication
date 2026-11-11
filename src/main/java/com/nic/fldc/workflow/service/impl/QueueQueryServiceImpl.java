package com.nic.fldc.workflow.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nic.fldc.common.api.PageResponse;
import com.nic.fldc.common.exception.ResourceNotFoundException;
import com.nic.fldc.common.exception.UnauthorizedJurisdictionException;
import com.nic.fldc.common.security.UserPrincipal;
import com.nic.fldc.common.security.WorkflowRole;
import com.nic.fldc.workflow.dto.request.QueueFilterRequest;
import com.nic.fldc.workflow.dto.response.FamilySummaryResponse;
import com.nic.fldc.workflow.dto.response.MemberDetailResponse;
import com.nic.fldc.workflow.dto.response.QueueItemResponse;
import com.nic.fldc.workflow.entity.Family;
import com.nic.fldc.workflow.entity.FamilyMember;
import com.nic.fldc.workflow.mapper.FamilyMapper;
import com.nic.fldc.workflow.mapper.FamilyMemberMapper;
import com.nic.fldc.workflow.repository.FamilyMemberRepository;
import com.nic.fldc.workflow.repository.FamilyRepository;
import com.nic.fldc.workflow.repository.specification.ApproverQueueSpecification;
import com.nic.fldc.workflow.repository.specification.EnquiryQueueSpecification;
import com.nic.fldc.workflow.repository.specification.RejectedMemberQueueSpecification;
import com.nic.fldc.workflow.repository.specification.VerifierQueueSpecification;
import com.nic.fldc.workflow.service.QueueQueryService;

@Service
@Transactional(readOnly = true)
public class QueueQueryServiceImpl implements QueueQueryService {

    private final FamilyRepository familyRepo;
    private final FamilyMemberRepository memberRepo;
    private final FamilyMapper familyMapper;
    private final FamilyMemberMapper memberMapper;

    public QueueQueryServiceImpl(FamilyRepository familyRepo,
                                 FamilyMemberRepository memberRepo,
                                 FamilyMapper familyMapper,
                                 FamilyMemberMapper memberMapper) {
        this.familyRepo = familyRepo;
        this.memberRepo = memberRepo;
        this.familyMapper = familyMapper;
        this.memberMapper = memberMapper;
    }

    private Pageable createPageable(QueueFilterRequest req) {
        int page = (req.getPage() != null && req.getPage() >= 0) ? req.getPage() : 0;
        int size = (req.getSize() != null && req.getSize() > 0) ? req.getSize() : 10;
        Sort.Direction direction = "ASC".equalsIgnoreCase(req.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortBy = (req.getSortBy() != null && !req.getSortBy().isBlank()) ? req.getSortBy() : "createdAt";
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    private void validateJurisdiction(Integer requestedDistrict, UserPrincipal user) {
        if (user == null || user.hasRole(WorkflowRole.ROLE_ADMIN)) {
            return;
        }
        if (user.getDistrictCode() != null && requestedDistrict != null && !user.getDistrictCode().equals(requestedDistrict)) {
            throw new UnauthorizedJurisdictionException(requestedDistrict, user.getDistrictCode());
        }
    }

    @Override
    public PageResponse<QueueItemResponse> getEnquiryQueue(QueueFilterRequest request, UserPrincipal user) {
        Integer userDistrict = (user != null) ? user.getDistrictCode() : null;
        validateJurisdiction(request.getDistrictCode(), user);
        Specification<Family> spec = EnquiryQueueSpecification.filter(request, userDistrict);
        Page<Family> pageResult = familyRepo.findAll(spec, createPageable(request));
        return PageResponse.from(pageResult.map(familyMapper::toQueueItem));
    }

    @Override
    public PageResponse<QueueItemResponse> getVerifierQueue(QueueFilterRequest request, UserPrincipal user) {
        Integer userDistrict = (user != null) ? user.getDistrictCode() : null;
        validateJurisdiction(request.getDistrictCode(), user);
        Specification<Family> spec = VerifierQueueSpecification.filter(request, userDistrict);
        Page<Family> pageResult = familyRepo.findAll(spec, createPageable(request));
        return PageResponse.from(pageResult.map(familyMapper::toQueueItem));
    }

    @Override
    public PageResponse<QueueItemResponse> getApproverQueue(QueueFilterRequest request, UserPrincipal user) {
        Integer userDistrict = (user != null) ? user.getDistrictCode() : null;
        validateJurisdiction(request.getDistrictCode(), user);
        Specification<FamilyMember> spec = ApproverQueueSpecification.filter(request, userDistrict);
        Page<FamilyMember> pageResult = memberRepo.findAll(spec, createPageable(request));
        return PageResponse.from(pageResult.map(memberMapper::toQueueItem));
    }

    @Override
    public PageResponse<QueueItemResponse> getRejectedReProcessQueue(QueueFilterRequest request, UserPrincipal user) {
        Integer userDistrict = (user != null) ? user.getDistrictCode() : null;
        validateJurisdiction(request.getDistrictCode(), user);
        Specification<FamilyMember> spec = RejectedMemberQueueSpecification.filter(request, userDistrict);
        Page<FamilyMember> pageResult = memberRepo.findAll(spec, createPageable(request));
        return PageResponse.from(pageResult.map(memberMapper::toQueueItem));
    }

    @Override
    public FamilySummaryResponse getFamilyDetails(UUID familyId, Integer districtCode, UserPrincipal user) {
        validateJurisdiction(districtCode, user);
        Family family = familyRepo.findByIdAndLgdDistrictCodeAndIsDeletedFalse(familyId, districtCode)
                .orElseThrow(() -> new ResourceNotFoundException("Family", familyId));

        List<FamilyMember> members = memberRepo.findByFamilyIdAndIsDeletedFalse(familyId);
        family.setMembers(members);

        return familyMapper.toSummaryResponse(family);
    }

    @Override
    public List<MemberDetailResponse> getEligibleMembers(UUID familyId, Integer districtCode, UserPrincipal user) {
        validateJurisdiction(districtCode, user);
        List<FamilyMember> members = memberRepo.findByFamilyIdAndIsDeletedFalse(familyId);
        return members.stream()
                .map(memberMapper::toDetailResponse)
                .filter(m -> Boolean.TRUE.equals(m.getEligibleForAction()))
                .toList();
    }
}

