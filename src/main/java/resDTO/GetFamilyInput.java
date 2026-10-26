package resDTO;

import com.fasterxml.jackson.annotation.JsonAlias;

public class GetFamilyInput {

	@JsonAlias({"lgd_distrct_code", "lgd_district_code"})
	private int lgd_district_code;
	
	@JsonAlias({"family_id", "family_id"})
	private String family_id;

	private String remarks;

	@JsonAlias({"local_body_code", "created_by_local_body_code"})
	private Object local_body_code;

	@JsonAlias({"user_id", "userId"})
	private Object user_id;

	@JsonAlias({"ip_address", "ipAddress"})
	private String ip_address;

	public String getFamily_id() {
		return family_id;
	}

	public void setFamily_id(String family_id) {
		this.family_id = family_id;
	}

	public int getLgd_district_code() {
		return lgd_district_code;
	}

	public void setLgd_district_code(int lgd_district_code) {
		this.lgd_district_code = lgd_district_code;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Object getLocal_body_code() {
		return local_body_code;
	}

	public void setLocal_body_code(Object local_body_code) {
		this.local_body_code = local_body_code;
	}

	public Object getUser_id() {
		return user_id;
	}

	public void setUser_id(Object user_id) {
		this.user_id = user_id;
	}

	public String getIp_address() {
		return ip_address;
	}

	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
}
