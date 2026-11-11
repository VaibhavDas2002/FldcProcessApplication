package resDTO;

public class GetStatus {
    private Object next_level_role_id;
    private Object approval_datetime;
    private Object verification_datetime;

    public GetStatus() {
    }

    public GetStatus(Object next_level_role_id, Object approval_datetime, Object verification_datetime) {
        this.next_level_role_id = next_level_role_id;
        this.approval_datetime = approval_datetime;
        this.verification_datetime = verification_datetime;
    }

    public Object getNext_level_role_id() {
        return next_level_role_id;
    }

    public void setNext_level_role_id(Object next_level_role_id) {
        this.next_level_role_id = next_level_role_id;
    }

    public Object getApproval_datetime() {
        return approval_datetime;
    }

    public void setApproval_datetime(Object approval_datetime) {
        this.approval_datetime = approval_datetime;
    }

    public Object getVerification_datetime() {
        return verification_datetime;
    }

    public void setVerification_datetime(Object verification_datetime) {
        this.verification_datetime = verification_datetime;
    }

    public static GetStatusBuilder builder() {
        return new GetStatusBuilder();
    }

    public static class GetStatusBuilder {
        private Object next_level_role_id;
        private Object approval_datetime;
        private Object verification_datetime;

        public GetStatusBuilder next_level_role_id(Object next_level_role_id) {
            this.next_level_role_id = next_level_role_id;
            return this;
        }

        public GetStatusBuilder approval_datetime(Object approval_datetime) {
            this.approval_datetime = approval_datetime;
            return this;
        }

        public GetStatusBuilder verification_datetime(Object verification_datetime) {
            this.verification_datetime = verification_datetime;
            return this;
        }

        public GetStatus build() {
            return new GetStatus(next_level_role_id, approval_datetime, verification_datetime);
        }
    }
}
