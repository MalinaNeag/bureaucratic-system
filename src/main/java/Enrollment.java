package main.java;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Enrollment class to issue membership
class Enrollment {
    private final Lock enrollmentLock = new ReentrantLock(); // Lock for ID verification and membership generation

    // Method to verify the citizen's ID and generate a membership
    public void verifyAndGenerateMembership(Citizen citizen) {
        try {
            enrollmentLock.lock();  // Ensure only one citizen is processed at a time
            System.out.println("Enrollment process started for Citizen ID: " + citizen.getId());
            if (FirebaseDatabase.verifyCitizenID(citizen.getId())) {
                System.out.println("Citizen ID " + citizen.getId() + " verified.");
                citizen.setMembership(true);
                FirebaseDatabase.storeMembership(citizen.getId()); // Store the membership in Firebase
            } else {
                System.out.println("Citizen ID " + citizen.getId() + " could not be verified.");
            }
        } finally {
            enrollmentLock.unlock(); // Release lock
        }
    }
}