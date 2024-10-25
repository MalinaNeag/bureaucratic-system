

public class Membership {
    private String membershipNumber;
    private String citizenName;
    private String issueDate;
    private String citizenId;

    public Membership(String membershipNumber, String citizenName, String issueDate, String citizenId) {
        this.membershipNumber = membershipNumber;
        this.citizenId = citizenId;
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
    public String getCitizenId() {
        return citizenId;
    }
}