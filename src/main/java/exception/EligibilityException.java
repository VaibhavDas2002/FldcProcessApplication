package exception;

import java.util.UUID;

public class EligibilityException extends RuntimeException {

    private final UUID memberId;
    private final String reason;

    public EligibilityException(UUID memberId, String reason) {
        super(String.format("Member '%s' does not satisfy Annapurna Bhandar eligibility criteria: %s", memberId, reason));
        this.memberId = memberId;
        this.reason = reason;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public String getReason() {
        return reason;
    }
}
