package main.java;

// Class representing a Citizen
class Citizen {
    private String id;
    private boolean membership;

    public Citizen(String id) {
        this.id = id;
        this.membership = false;
    }

    public String getId() {
        return id;
    }

    public boolean hasMembership() {
        return membership;
    }

    public void setMembership(boolean membership) {
        this.membership = membership;
    }

    // verify ID and generate membership
    public void verifyAndGenerateMembership() {
        if (FirebaseDatabase.verifyCitizenID(id)) {
            System.out.println("Citizen ID " + id + " verified.");
            this.membership = true;
            FirebaseDatabase.storeMembership(id); // Store the membership in Firebase
        } else {
            System.out.println("Citizen ID " + id + " could not be verified.");
        }
    }
}