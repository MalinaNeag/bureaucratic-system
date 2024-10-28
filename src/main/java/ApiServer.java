import static spark.Spark.*;

import com.google.gson.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiServer {
    private static List<Office> offices = new ArrayList<>();

    // Getter for offices
    public static List<Office> getOffices() {
        return offices;
    }

    public static void main(String[] args) throws IOException {
        FirebaseDB.initFirebase();
        port(4567);
        Gson gson = new Gson();

        // GET all books
        //get("/api/books", (req, res) -> BookLoaningDepartment.getInstance().getAllBooks(), gson::toJson);

        // Load departments into map
        Map<String, Department> departments = new HashMap<>();
        departments.put("BookLoaningDepartment", BookLoaningDepartment.getInstance());

        // Endpoint to process a loan request
        post("/api/loan-request", (req, res) -> {
            LoanRequest loanRequest = gson.fromJson(req.body(), LoanRequest.class);
            Citizen citizen = new Citizen();
            citizen.setId(loanRequest.getCitizenId());
            BookLoaningDepartment.getInstance().addCitizenToQueue(citizen, loanRequest.getBookTitle(), loanRequest.getBookAuthor());
            res.status(200);
            return "Citizen added to queue for loan request";
        });

        post("/api/pause-counter", (req, res) -> {
            String requestBody = req.body();

            // Parse JSON
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            String departmentName = jsonObject.has("department") ? jsonObject.get("department").getAsString() : null;
            String counterIdParam = jsonObject.has("counterId") ? jsonObject.get("counterId").getAsString() : null;

            // Check if department or counterId is missing
            if (departmentName == null || counterIdParam == null) {
                res.status(400); // Bad Request
                return "Missing required parameters: department and/or counterId";
            }

            try {
                int counterId = Integer.parseInt(counterIdParam); // Parse counterId as an integer
                Department department = departments.get(departmentName);

                if (department != null) {
                    department.pauseCounter(counterId);
                    res.status(200);
                    return "Counter " + counterId + " in " + departmentName + " paused for a coffee break";
                } else {
                    res.status(404); // Department not found
                    return "Department not found";
                }
            } catch (NumberFormatException e) {
                res.status(400); // Bad Request if counterId is not a valid number
                return "Invalid counterId format. Must be an integer.";
            }
        });

        post("/api/resume-counter", (req, res) -> {
            String requestBody = req.body();

            // Parse JSON
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();

            String departmentName = jsonObject.has("department") ? jsonObject.get("department").getAsString() : null;
            String counterIdParam = jsonObject.has("counterId") ? jsonObject.get("counterId").getAsString() : null;

            // Check if department or counterId is missing
            if (departmentName == null || counterIdParam == null) {
                res.status(400); // Bad Request
                return "Missing required parameters: department and/or counterId";
            }

            try {
                int counterId = Integer.parseInt(counterIdParam); // Parse counterId as an integer
                Department department = departments.get(departmentName);

                if (department != null) {
                    department.resumeCounter(counterId);
                    res.status(200);
                    return "Counter " + counterId + " in " + departmentName + " resumed work";
                } else {
                    res.status(404); // Department not found
                    return "Department not found";
                }
            } catch (NumberFormatException e) {
                res.status(400); // Bad Request if counterId is not a valid number
                return "Invalid counterId format. Must be an integer.";
            }
        });

        post("/api/enroll", (req, res) -> {
            Citizen citizen = gson.fromJson(req.body(), Citizen.class);
            EnrollmentHandler enrollmentHandler = new EnrollmentHandler();
            boolean success = enrollmentHandler.enrollCitizen(citizen);
            res.status(success ? 200 : 400);
            return success ? "Citizen enrolled successfully" : "Citizen enrollment failed";
        });

        // POST endpoint to accept configuration
        post("/api/config", (req, res) -> {
            JsonObject configJson = gson.fromJson(req.body(), JsonObject.class);
            offices = parseConfiguration(configJson);  // Parse and store config in-memory
            res.status(200);
            return "Configuration received and loaded successfully";
        });
    }

    private static List<Office> parseConfiguration(JsonObject configJson) {
        System.out.println("Starting to parse configuration for offices.");
        List<Office> parsedOffices = new ArrayList<>();
        JsonArray officeArray = configJson.getAsJsonArray("offices");
        System.out.println("Found " + officeArray.size() + " offices in configuration.");

        for (JsonElement officeElement : officeArray) {
            JsonObject officeObj = officeElement.getAsJsonObject();
            String officeName = officeObj.get("name").getAsString();
            int counters = officeObj.get("counters").getAsInt();
            System.out.println("Parsing office: " + officeName + " with " + counters + " counters.");

            List<Document> documents = new ArrayList<>();
            JsonArray documentArray = officeObj.getAsJsonArray("documents");
            System.out.println("Found " + documentArray.size() + " documents in office: " + officeName);

            for (JsonElement documentElement : documentArray) {
                JsonObject docObj = documentElement.getAsJsonObject();
                String docName = docObj.get("name").getAsString();

                List<String> dependencies = new ArrayList<>();
                JsonArray depArray = docObj.getAsJsonArray("dependencies");
                System.out.println("Document '" + docName + "' has " + depArray.size() + " dependencies.");

                for (JsonElement depElement : depArray) {
                    dependencies.add(depElement.getAsString());
                }
                System.out.println("Added document '" + docName + "' with dependencies: " + dependencies);

                documents.add(new Document(docName, dependencies));
            }

            parsedOffices.add(new Office(officeName, counters, documents));
            System.out.println("Added office: " + officeName + " with " + documents.size() + " documents.");
        }

        System.out.println("Finished parsing configuration. Total offices parsed: " + parsedOffices.size());
        return parsedOffices;
    }
}