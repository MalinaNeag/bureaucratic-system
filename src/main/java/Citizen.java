package main.java;

public class Citizen {
    private String name;
    private String id;
    private String membershipId;
    private Membership membership;

    public Citizen(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Citizen(String name, String id, String membershipId) {
        this.name = name;
        this.id = id;
        this.membershipId = membershipId;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public boolean hasValidMembership() {
        return membership != null;
    }
}