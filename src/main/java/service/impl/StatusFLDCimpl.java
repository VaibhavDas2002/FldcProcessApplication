package service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import repository.familymemberRepository;
import service.StatusFLDC;

@Service
public class StatusFLDCimpl implements StatusFLDC {

    private final familymemberRepository memberRepository;

    public StatusFLDCimpl(familymemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public List<Map<String, Object>> getStatusByDistrict(int districtCode, String family_id, String member_id) {
        return memberRepository.findByDistrictCode(districtCode, family_id, member_id);
    }

    @Override
    public List<Map<String, Object>> geStatustByDistrict(int districtCode, String family_id, String member_id) {
        return getStatusByDistrict(districtCode, family_id, member_id);
    }

    @Override
    public List<Map<String, Object>> getStatusByFamily(int districtCode, String family_id) {
        return memberRepository.findByFamilyId(districtCode, family_id);
    }
}
