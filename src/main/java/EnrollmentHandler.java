

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class EnrollmentHandler implements HttpHandler {
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Citizen citizen = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Citizen.class);

            Thread citizenThread = new Thread(() -> EnrollmentDepartment.getInstance().enrollCitizen(citizen));
            citizenThread.start();

            String response = "Enrollment started for: " + citizen.getName();
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public boolean enrollCitizen(Citizen citizen) {
        if (citizen == null || citizen.getName() == null || citizen.getName().isEmpty()) {
            // Invalid citizen data
            return false;
        }

        // Assume EnrollmentDepartment handles the storage and validation of citizens
        EnrollmentDepartment enrollmentDepartment = EnrollmentDepartment.getInstance();

        // Check if the citizen is already enrolled
        if (enrollmentDepartment.isCitizenEnrolled(citizen)) {
            // Citizen is already enrolled, return false
            System.out.println("Citizen " + citizen.getName() + " is already enrolled.");
            return false;
        }

        // Enroll the citizen
        boolean success = enrollmentDepartment.addCitizen(citizen);

        if (success) {
            System.out.println("Citizen " + citizen.getName() + " enrolled successfully.");
        } else {
            System.out.println("Failed to enroll citizen " + citizen.getName() + ".");
        }

        return success;
    }

}