package main.java;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Class representing a Book
class Book {
    private String title;
    private Lock lock = new ReentrantLock();  // Lock for concurrency control

    public Book(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean borrowBook(Citizen citizen) {
        if (lock.tryLock()) {
            try {
                System.out.println(citizen.getId() + " is borrowing " + title);
                Thread.sleep(1000);  // Simulate borrowing process
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println(citizen.getId() + " failed to borrow " + title + " (book locked).");
            return false;
        }
    }
}