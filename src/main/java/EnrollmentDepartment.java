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
    public boolean isCitizenEnrolled(Citizen citizen) {
        Membership existingMembership = FirebaseDB.getMembershipByCitizenName(citizen.getName());
        return existingMembership != null; // Return true if membership exists, false otherwise
    }

    // Add the citizen to the system (only if not already enrolled)
    public boolean addCitizen(Citizen citizen) {
        try {
            enrollmentLock.lock();

            // Check if the citizen is already enrolled
            if (isCitizenEnrolled(citizen)) {
                System.out.println("Citizen " + citizen.getName() + " is already enrolled.");
                return false;  // Citizen is already enrolled, return false
            }

            // Proceed with enrollment if no existing membership
            System.out.println("Enrolling citizen: " + citizen.getName());
            Membership newMembership = new Membership("M" + System.currentTimeMillis(), citizen.getName(), "2024-10-24");
            citizen.setMembership(newMembership);

            // Add membership to the Firebase database
            FirebaseDB.addMembership(newMembership);

            System.out.println("Citizen " + citizen.getName() + " enrolled with membership ID: " + newMembership.getMembershipNumber());
            return true;  // Enrollment successful

        } finally {
            enrollmentLock.unlock();
        }
    }

    // Main enrollment method (uses addCitizen internally)
    public boolean enrollCitizen(Citizen citizen) {
        return addCitizen(citizen);  // Delegate to addCitizen method
    }
}
