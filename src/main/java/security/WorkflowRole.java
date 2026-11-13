package security;

public enum WorkflowRole {
    ROLE_CITIZEN("CITIZEN"),
    ROLE_OPERATOR("OPERATOR"),
    ROLE_ENQUIRY_OFFICER("ENQUIRY_OFFICER"),
    ROLE_VERIFIER("VERIFIER"),
    ROLE_APPROVER("APPROVER"),
    ROLE_ADMIN("ADMIN");

    private final String roleName;

    WorkflowRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static WorkflowRole fromString(String text) {
        if (text == null) return null;
        String clean = text.trim().toUpperCase();
        if (!clean.startsWith("ROLE_")) {
            clean = "ROLE_" + clean;
        }
        for (WorkflowRole r : values()) {
            if (r.name().equalsIgnoreCase(clean)) {
                return r;
            }
        }
        return null;
    }
}

