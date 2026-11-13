package config;

public class RoleId {

    public static String getRoleStatusName(Integer roleId) {
        if (roleId == null) {
            return null;
        }
        switch (roleId) {
            case 0:
                return "Draft Entry";
            case 4:
                return "Revert Application / Family (Citizen Entry) - Approver";
            case 5:
                return "Revert Application / Family (Citizen Entry) - Enquiry";
            case 6:
                return "Revert Application / Family (Citizen Entry) - Verifier";
            case 7:
                return "Revert Application / Family (Office Entry) - Enquiry";
            case 8:
                return "Revert Application / Family (Office Entry) - Verifier";
            case 9:
                return "Revert Application / Family (Office Entry) - Approver";
            case 10:
                return "Final Submission";
            case 11:
                return "Recommend for enquiry - Verifier";
            case 13:
                return "Recommend For Rejection - Enquiry Officer";
            case 15:
                return "Recommend For Accept - Enquiry Officer";
            case 17:
                return "Verifier Hold - Verifier";
            case 18:
                return "Accept Application / Family Details - Verifier";
            case 20:
                return "Verify Member (Annapurna) Accepted Application - Verifier";
            case 25:
                return "Hold Member / Family Details - Approver";
            case 26:
                return "Approver Hold - Approver";
            case 27:
                return "Recommend Approved Member (Annapurna) For Rejection - Verifier";
            case 30:
                return "Approve Member (Annapurna) - Approver";
            case -100:
                return "Reject Application / Family";
            case -101:
                return "Reject Accepted Member (Annapurna)";
            case -102:
                return "Reject Member (Annapurna)";
            case -103:
                return "Reject Approved / Recommended Member (Annapurna)";
            case -104:
                return "Approver End (Exclusive Cases)";
            default:
                return null;
        }
    }

}
