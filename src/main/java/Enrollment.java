package main.java;

// Enrollment class to issue membership
class Enrollment {
    public void enroll(Citizen citizen) {
        System.out.println("Enrolling citizen with ID: " + citizen.getId());
        citizen.setMembership(true);
    }
}