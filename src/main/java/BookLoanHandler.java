
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class BookLoanHandler implements HttpHandler {
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            LoanRequest request = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), LoanRequest.class);
            Citizen citizen = request.getCitizen();
            String requestedBook = request.getBookTitle();

            Thread citizenThread = new Thread(() -> BookLoaningDepartment.getInstance().borrowBook(citizen, requestedBook));
            citizenThread.start();

            String response = "Borrowing process started for: " + citizen.getName();
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public boolean handleLoanRequest(LoanRequest loanRequest) {
        // Extract the citizen and book title from the loan request
        Citizen citizen = loanRequest.getCitizen();
        String requestedBook = loanRequest.getBookTitle();

        if (citizen == null || requestedBook == null || requestedBook.isEmpty()) {
            // Invalid request, missing citizen or book title
            return false;
        }

        // Start a thread to handle the book borrowing process asynchronously
        Thread loanThread = new Thread(() -> {
            BookLoaningDepartment.getInstance().borrowBook(citizen, requestedBook);
        });
        loanThread.start();

        // Return true to indicate that the loan request has been initiated
        return true;
    }

}