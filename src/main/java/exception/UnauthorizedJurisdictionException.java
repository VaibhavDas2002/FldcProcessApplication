package exception;

public class UnauthorizedJurisdictionException extends RuntimeException {

    private final Integer requestedDistrictCode;
    private final Integer authorizedDistrictCode;

    public UnauthorizedJurisdictionException(Integer requestedDistrictCode, Integer authorizedDistrictCode) {
        super(String.format("User authorized for district %s is not permitted to access or modify records in district %s",
                authorizedDistrictCode, requestedDistrictCode));
        this.requestedDistrictCode = requestedDistrictCode;
        this.authorizedDistrictCode = authorizedDistrictCode;
    }

    public UnauthorizedJurisdictionException(String message) {
        super(message);
        this.requestedDistrictCode = null;
        this.authorizedDistrictCode = null;
    }

    public Integer getRequestedDistrictCode() {
        return requestedDistrictCode;
    }

    public Integer getAuthorizedDistrictCode() {
        return authorizedDistrictCode;
    }
}
