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
        }
    }

    public boolean handleLoanRequest(LoanRequest loanRequest) {
        // Extract the citizen and book details from the loan request
        String citizenId = loanRequest.getCitizenId();
        Citizen citizen = new Citizen();
        citizen.setId(citizenId);
        String bookTitle = loanRequest.getBookTitle();
        String bookAuthor = loanRequest.getBookAuthor();

        // Check for valid request
        if (citizen == null || bookTitle == null || bookTitle.isEmpty()) {
            return false; // Invalid request
        }

        // Call the BookLoaningDepartment to borrow the book
        BookLoaningDepartment.getInstance().borrowBook(citizen, bookTitle, bookAuthor);

        return true; // Request initiated successfully
    }


    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}