package main.java;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Queue;
import java.util.LinkedList;

public class BookLoaningDepartment {
    private static BookLoaningDepartment instance;
    private final Queue<Citizen> queue = new LinkedList<>();
    private Lock bookLoanLock = new ReentrantLock();

    private BookLoaningDepartment() {}

    public static synchronized BookLoaningDepartment getInstance() {
        if (instance == null) {
            instance = new BookLoaningDepartment();
        }
        return instance;
    }

    public void borrowBook(Citizen citizen, String bookTitle) {
        synchronized (queue) {
            queue.add(citizen);
        }

        try {
            // Wait until citizen is at the front of the queue
            synchronized (queue) {
                while (queue.peek() != citizen) {
                    queue.wait();
                }
            }

            // Proceed with book borrowing process
            bookLoanLock.lock();
            Book book = FirebaseDB.getBook(bookTitle);

            if (book.isAvailable() && Objects.requireNonNull(citizen).hasValidMembership()) {
                book.setAvailable(false);
                book.setBorrowedBy(citizen);
                book.setBorrowDate("2024-10-24");
                FirebaseDB.updateBook(book);
                System.out.println("Book '" + bookTitle + "' borrowed by " + citizen.getName());
            } else {
                System.out.println("Book '" + bookTitle + "' is not available or citizen has no valid membership.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bookLoanLock.unlock();
            synchronized (queue) {
                queue.remove();
                queue.notifyAll(); // Notify the next citizen in the queue
            }
        }
    }
}