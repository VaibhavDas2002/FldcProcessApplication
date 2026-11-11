package com.nic.fldc.common.exception;

public enum ErrorCode {
    INVALID_TRANSITION("ERR_WF_001", "The requested workflow transition is not allowed for the current state and role."),
    ELIGIBILITY_FAILED("ERR_WF_002", "Family member is not eligible for Annapurna Yojana verification."),
    JURISDICTION_MISMATCH("ERR_WF_003", "Action denied: user does not have jurisdiction over this LGD location."),
    RESOURCE_NOT_FOUND("ERR_WF_004", "The requested family or member was not found."),
    VALIDATION_ERROR("ERR_WF_005", "One or more request parameters failed validation."),
    UNAUTHORIZED("ERR_WF_006", "Authentication or permission check failed."),
    DATABASE_ERROR("ERR_WF_007", "A transactional database error occurred."),
    CONCURRENCY_ERROR("ERR_WF_008", "The record was updated by another process. Please refresh."),
    GENERAL_ERROR("ERR_WF_099", "An unexpected error occurred while processing the workflow request.");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

