

public class Membership {
    private String membershipNumber;
    private String citizenName;
    private String issueDate;

    public Membership(String membershipNumber, String citizenName, String issueDate) {
        this.membershipNumber = membershipNumber;
        this.citizenName = citizenName;
        this.issueDate = issueDate;
    }

    // Getters and Setters
    public String getMembershipNumber() {
        return membershipNumber;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public String getIssueDate() {
        return issueDate;
    }
}