package workflow;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum NextLevelRoleId {

    DRAFT_ENTRY(0, "Draft Entry", TargetScope.BOTH),
    REVERT_CITIZEN_APPROVER(4, "Revert Application / Family (Citizen Entry) - Approver", TargetScope.BOTH),
    REVERT_CITIZEN_ENQUIRY(5, "Revert Application / Family (Citizen Entry) - Enquiry", TargetScope.BOTH),
    REVERT_CITIZEN_VERIFIER(6, "Revert Application / Family (Citizen Entry) - Verifier", TargetScope.BOTH),
    REVERT_OFFICE_ENQUIRY(7, "Revert Application / Family (Office Entry) - Enquiry", TargetScope.BOTH),
    REVERT_OFFICE_VERIFIER(8, "Revert Application / Family (Office Entry) - Verifier", TargetScope.BOTH),
    REVERT_OFFICE_APPROVER(9, "Revert Application / Family (Office Entry) - Approver", TargetScope.BOTH),
    FINAL_SUBMISSION(10, "Final Submission", TargetScope.BOTH),
    RECOMMEND_FOR_ENQUIRY_VERIFIER(11, "Recommend for Enquiry - Verifier", TargetScope.BOTH),
    RECOMMEND_FOR_REJECTION_ENQUIRY(13, "Recommend For Rejection - Enquiry Officer", TargetScope.BOTH),
    RECOMMEND_FOR_ACCEPT_ENQUIRY(15, "Recommend For Accept - Enquiry Officer", TargetScope.BOTH),
    VERIFIER_HOLD(17, "Verifier Hold / Further Verification Required - Verifier", TargetScope.BOTH),
    ACCEPT_APPLICATION_VERIFIER(18, "Accept Application / Family Details - Verifier", TargetScope.BOTH),
    VERIFY_MEMBER_VERIFIER(20, "Verify Member (Annapurna) Accepted Application - Verifier", TargetScope.MEMBER),
    HOLD_MEMBER_APPROVER(25, "Hold Member / Family Details - Approver (Exclusive Cases)", TargetScope.MEMBER),
    APPROVER_HOLD(26, "Approver Hold / Further Verification Required - Approver", TargetScope.BOTH),
    RECOMMEND_APPROVED_REJECTION_VERIFIER(27, "Recommend Approved Member (Annapurna) For Rejection", TargetScope.MEMBER),
    APPROVE_MEMBER_APPROVER(30, "Approve Member (Annapurna) - Approver", TargetScope.MEMBER),
    REJECT_APPLICATION_VERIFIER(-100, "Reject Application / Family", TargetScope.BOTH),
    REJECT_ACCEPTED_MEMBER_VERIFIER(-101, "Reject Accepted Member (Annapurna)", TargetScope.MEMBER),
    REJECT_MEMBER_APPROVER(-102, "Reject Member (Annapurna)", TargetScope.MEMBER),
    REJECT_APPROVED_RECOMMENDED_APPROVER(-103, "Reject Approved / Recommended Member (Annapurna)", TargetScope.MEMBER),
    REJECT_HOLD_MEMBER_APPROVER(-104, "Reject Hold Member (Exclusive Cases)", TargetScope.MEMBER),
    REJECT_PRIORITY_REVERIFICATION(-106, "Reject Priority Re-verification", TargetScope.MEMBER);

    public enum TargetScope {
        BOTH,
        MEMBER
    }

    private final int code;
    private final String description;
    private final TargetScope scope;

    private static final Map<Integer, NextLevelRoleId> CODE_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(NextLevelRoleId::getCode, Function.identity()));

    NextLevelRoleId(int code, String description, TargetScope scope) {
        this.code = code;
        this.description = description;
        this.scope = scope;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public TargetScope getScope() {
        return scope;
    }

    public static NextLevelRoleId fromCode(Integer code) {
        if (code == null) return null;
        return CODE_MAP.get(code);
    }

    public boolean isRevert() {
        return this.code >= 4 && this.code <= 9;
    }

    public boolean isNegativeReject() {
        return this.code < 0;
    }

    public boolean isApproved() {
        return this.code == 30;
    }
}

