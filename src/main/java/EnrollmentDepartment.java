package main.java;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EnrollmentDepartment {
    private static EnrollmentDepartment instance;
    private Lock enrollmentLock = new ReentrantLock();

    private EnrollmentDepartment() {}

    public static synchronized EnrollmentDepartment getInstance() {
        if (instance == null) {
            instance = new EnrollmentDepartment();
        }
        return instance;
    }

    public void enrollCitizen(Citizen citizen) {
        try {
            enrollmentLock.lock();
            // Simulate identity verification and membership creation
            System.out.println("Enrolling citizen: " + citizen.getName());
            Membership newMembership = new Membership("M" + System.currentTimeMillis(), citizen.getName(), "2024-10-24");
            citizen.setMembership(newMembership);

            // Add membership to the Firebase database (simulated)
            FirebaseDB.addMembership(newMembership);

            System.out.println("Citizen " + citizen.getName() + " enrolled with membership ID: " + newMembership.getMembershipNumber());
        } finally {
            enrollmentLock.unlock();
        }
    }
}