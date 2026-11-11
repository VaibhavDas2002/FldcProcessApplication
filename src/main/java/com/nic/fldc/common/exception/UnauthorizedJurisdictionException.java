package com.nic.fldc.common.exception;

import lombok.Getter;

@Getter
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
}

