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

    public static void main(String[] args) {
        System.out.println("ppp");
    }
}