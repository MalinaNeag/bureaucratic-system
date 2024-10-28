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

    public synchronized boolean isCitizenEnrolled(Citizen citizen) {
        String existingMembership = FirebaseDB.getMembershipIdById(citizen.getId());
        return existingMembership != null;
    }

    public synchronized boolean addCitizen(Citizen citizen) {
        try {
            System.out.println("Enrolling citizen: " + citizen.getName());

            Membership newMembership = new Membership("M" + System.currentTimeMillis(), citizen.getName(), "2024-10-24", citizen.getId());
            FirebaseDB.addMembership(newMembership);
            // Update obtained documents
            FirebaseDB.addOrUpdateDocumentForCitizen(citizen.getId(),"Membership ID" );
            System.out.println("Citizen " + citizen.getName() + " enrolled with membership ID: " + newMembership.getMembershipNumber());
            notifyAll();
            return true;

        } catch (Exception e) {
            System.err.println("Enrollment failed for citizen " + citizen.getName() + ": " + e.getMessage());
            return false;
        }
    }

//    public boolean enrollCitizen(Citizen citizen) {
//        return addCitizen(citizenk);
//    }
}