package service;

import java.util.List;
import java.util.Map;

public interface StatusFLDC {

    List<Map<String, Object>> getStatusByDistrict(int districtCode, String family_id, String member_id);

    List<Map<String, Object>> geStatustByDistrict(int districtCode, String family_id, String member_id);

    List<Map<String, Object>> getStatusByFamily(int districtCode, String family_id);

}
