

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class EnrollmentHandler{
    private final Gson gson = new Gson();

    public synchronized boolean enrollCitizen(Citizen citizen) {
        if (citizen == null || citizen.getName() == null || citizen.getName().isEmpty()) {
            return false; // Invalid citizen data
        }

        // Run the enrollment in a new thread to handle each request independently
        Thread enrollmentThread = new Thread(() -> {
            EnrollmentDepartment enrollmentDepartment = EnrollmentDepartment.getInstance();
            // Check if the citizen is already enrolled before attempting to add
            if (enrollmentDepartment.isCitizenEnrolled(citizen)) {
                System.out.println("Citizen " + citizen.getName() + " is already enrolled.");
                return;
            }
            // Attempt to add the citizen, capturing the success of the operation
            boolean success = enrollmentDepartment.addCitizen(citizen);

            if (success) {
                System.out.println("Citizen " + citizen.getName() + " enrolled successfully.");
            } else {
                System.out.println("Citizen " + citizen.getName() + " is already enrolled or enrollment failed.");
            }
        });

        enrollmentThread.start(); // Start the thread for this enrollment request
        return true; // Return immediately as the enrollment process starts asynchronously
    }

}