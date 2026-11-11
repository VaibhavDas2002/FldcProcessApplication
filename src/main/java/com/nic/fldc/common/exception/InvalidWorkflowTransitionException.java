package com.nic.fldc.common.exception;

public class InvalidWorkflowTransitionException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Integer currentRoleId;
    private final String requestedAction;
    private final String roleName;

    public InvalidWorkflowTransitionException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_TRANSITION;
        this.currentRoleId = null;
        this.requestedAction = null;
        this.roleName = null;
    }

    public InvalidWorkflowTransitionException(Integer currentRoleId, String requestedAction, String roleName) {
        super(String.format("Invalid workflow transition: Action '%s' is not permitted for actor '%s' from state (role_id=%s)",
                requestedAction, roleName, currentRoleId));
        this.errorCode = ErrorCode.INVALID_TRANSITION;
        this.currentRoleId = currentRoleId;
        this.requestedAction = requestedAction;
        this.roleName = roleName;
    }

    public InvalidWorkflowTransitionException(String message, Integer currentRoleId, String requestedAction, String roleName) {
        super(message);
        this.errorCode = ErrorCode.INVALID_TRANSITION;
        this.currentRoleId = currentRoleId;
        this.requestedAction = requestedAction;
        this.roleName = roleName;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Integer getCurrentRoleId() {
        return currentRoleId;
    }

    public String getRequestedAction() {
        return requestedAction;
    }

    public String getRoleName() {
        return roleName;
    }
}
