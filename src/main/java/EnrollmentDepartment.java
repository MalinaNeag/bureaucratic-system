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

    // Check if the citizen is already enrolled
    public synchronized boolean isCitizenEnrolled(Citizen citizen) {
        String existingMembership = FirebaseDB.getMembershipIdById(citizen.getId());
        return existingMembership != null; // Return true if membership exists, false otherwise
    }

    // Add the citizen to the system (only if not already enrolled)
    public synchronized boolean addCitizen(Citizen citizen) {
        try {
            System.out.println("Enrolling citizen: " + citizen.getName());

            // Create a new membership ID and assign it to the citizen
            Membership newMembership = new Membership("M" + System.currentTimeMillis(), citizen.getName(), "2024-10-24", citizen.getId());

            // Add membership to the Firebase database
            FirebaseDB.addMembership(newMembership);

            System.out.println("Citizen " + citizen.getName() + " enrolled with membership ID: " + newMembership.getMembershipNumber());
            notifyAll();  // Notify other waiting threads that enrollment is complete
            return true;  // Enrollment successful

        } catch (Exception e) {
            System.err.println("Enrollment failed for citizen " + citizen.getName() + ": " + e.getMessage());
            return false;

        }
    }

    // Main enrollment method (uses addCitizen internally)
    public boolean enrollCitizen(Citizen citizen) {
        return addCitizen(citizen);  // Delegate to addCitizen method
    }
}
