package main.java;

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
}