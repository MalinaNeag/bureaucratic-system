package main.java;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class LibraryServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Set up the context for enrollment and book borrowing
        server.createContext("/enroll", new EnrollmentHandler());
        server.createContext("/borrow", new BookLoanHandler());

        // Use a thread pool to handle multiple clients concurrently
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("Library Server is running on port 8080...");
    }
}