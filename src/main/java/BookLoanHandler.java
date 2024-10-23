package main.java;

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
}