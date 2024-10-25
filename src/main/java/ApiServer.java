

import static spark.Spark.*;
import com.google.gson.Gson;

import java.io.IOException;

public class ApiServer {

    public static void main(String[] args) throws IOException {
        FirebaseDB.initFirebase();
        port(4567);  // Setting the default port for the API

        Gson gson = new Gson();  // Gson to handle JSON responses

        // Example Endpoint: GET all books
        get("/api/books", (req, res) -> {
            BookLoaningDepartment department = new BookLoaningDepartment();
            return null;// department.getAllBooks();  // Now it uses the proper method
        }, gson::toJson);

        // Example Endpoint: POST a loan request
        post("/api/loan-request", (req, res) -> {
            LoanRequest loanRequest = gson.fromJson(req.body(), LoanRequest.class);
            BookLoanHandler handler = new BookLoanHandler();
            boolean success = handler.handleLoanRequest(loanRequest);  // Assuming method handles loan requests
            res.status(success ? 200 : 400);
            return success ? "Loan request successful" : "Loan request failed";
        });

        // Example Endpoint: POST citizen enrollment
        post("/api/enroll", (req, res) -> {
            Citizen citizen = gson.fromJson(req.body(), Citizen.class);
            EnrollmentHandler enrollmentHandler = new EnrollmentHandler();
            boolean success = enrollmentHandler.enrollCitizen(citizen);  // Assuming method handles enrollment
            res.status(success ? 200 : 400);
            return success ? "Citizen enrolled successfully" : "Citizen enrollment failed";
        });
    }
}
