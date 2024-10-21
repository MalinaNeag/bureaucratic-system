package main.java;

// Main class to simulate the system
public class LibrarySimulator {
    public static void main(String[] args) throws InterruptedException {
        // Assume Firebase initialization is already done in the real application
        LibrarySystem librarySystem = new LibrarySystem();

        // Citizens attempting to enroll and borrow books
        Citizen citizen1 = new Citizen("123");
        Citizen citizen2 = new Citizen("456");

        // Enrollment process (generate membership after ID verification)
        librarySystem.handleEnrollment(citizen1);
        librarySystem.handleEnrollment(citizen2);

        // After enrollment, try borrowing books
        Thread t1 = new Thread(() -> librarySystem.borrowBook(citizen1));
        Thread t2 = new Thread(() -> librarySystem.borrowBook(citizen2));

        // Start both threads to simulate concurrent borrowing
        t1.start();
        t2.start();

        // Join threads to wait for them to finish
        t1.join();
        t2.join();
    }
}