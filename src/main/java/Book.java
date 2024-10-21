package main.java;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Book {
    private String title;
    private Lock lock = new ReentrantLock(true);  // Fair lock to avoid starvation

    public Book(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean borrowBook(Citizen citizen) {
        try {
            if (lock.tryLock(1, TimeUnit.SECONDS)) { // prevent deadlock using timeout
                try {
                    // check for membership and book availability
                    if (!FirebaseDatabase.isCitizenEligible(citizen.getId())) {
                        System.out.println(citizen.getId() + " is not eligible to borrow.");
                        return false;
                    }
                    if (!FirebaseDatabase.isBookAvailable(title)) {
                        System.out.println("Book " + title + " is not available.");
                        return false;
                    }

                    System.out.println(citizen.getId() + " is borrowing " + title);
                    FirebaseDatabase.updateBookStatus(title, false);  // mark book as unavailable
                    Thread.sleep(1000);  // simulate time to process the borrowing
                    return true;
                } finally {
                    lock.unlock(); // release lock
                }
            } else {
                System.out.println(citizen.getId() + " could not borrow " + title + " (timeout).");
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}