import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;
import java.util.Queue;

public class BookLoaningDepartment implements Department {
    private static BookLoaningDepartment instance;
    private final Queue<BorrowRequest> queue = new LinkedBlockingQueue<>();
    private final Map<String, Lock> bookLocks = new ConcurrentHashMap<>();
    private final Thread counter1;
    private final Thread counter2;
    private volatile boolean counter1Paused = false;
    private volatile boolean counter2Paused = false;

    private BookLoaningDepartment() {
        counter1 = new Thread(() -> processQueue(1));
        counter2 = new Thread(() -> processQueue(2));
        counter1.start();
        counter2.start();
    }

    public static synchronized BookLoaningDepartment getInstance() {
        if (instance == null) {
            instance = new BookLoaningDepartment();
        }
        return instance;
    }

    public void addCitizenToQueue(Citizen citizen, String bookTitle, String bookAuthor) {
        synchronized (queue) {
            if (canIssueDocument("Loan Approval", citizen)) {
                queue.add(new BorrowRequest(citizen, bookTitle, bookAuthor));
                queue.notifyAll();
            } else {
                System.out.println("Citizen " + citizen.getId() + " does not meet requirements for loan.");
            }
        }
    }

    private boolean canIssueDocument(String documentName, Citizen citizen) {
        System.out.println("Checking issuance requirements for document: " + documentName + " for citizen: " + citizen.getId());

        for (Office office : ApiServer.getOffices()) {
            for (Document document : office.getDocuments()) {
                if (document.getName().equals(documentName)) {
                    System.out.println("Found document: " + documentName + " with dependencies: " + document.getDependencies());
                    for (String dependency : document.getDependencies()) {
                        if (!FirebaseDB.hasDocument(citizen.getId(), dependency)) {
                            System.out.println("Citizen " + citizen.getId() + " does not have required dependency: " + dependency);
                            return false;  // Missing required dependency
                        }
                    }
                    System.out.println("All dependencies met for document: " + documentName);
                    return true;  // All dependencies met
                }
            }
        }
        System.out.println("Document not found: " + documentName);
        return false;  // Document not found
    }

    @Override
    public void pauseCounter(int counterId) {
        if (counterId == 1) {
            synchronized (counter1) {
                counter1Paused = true;
            }
            System.out.println("Counter 1 is paused.");
        } else if (counterId == 2) {
            synchronized (counter2) {
                counter2Paused = true;
            }
            System.out.println("Counter 2 is paused.");
        }
    }

    @Override
    public void resumeCounter(int counterId) {
        if (counterId == 1) {
            synchronized (counter1) {
                counter1Paused = false;
                counter1.notify();  // Notify any waiting threads on this object
            }
            System.out.println("Counter 1 is resumed.");
        } else if (counterId == 2) {
            synchronized (counter2) {
                counter2Paused = false;
                counter2.notify();  // Notify any waiting threads on this object
            }
            System.out.println("Counter 2 is resumed.");
        }
    }

    private void processQueue(int counterId) {
        while (true) {
            try {
                final Object pauseLock = (counterId == 1) ? counter1 : counter2;
                synchronized (pauseLock) {
                    // Wait if the specific counter is paused
                    while ((counterId == 1 && counter1Paused) || (counterId == 2 && counter2Paused)) {
                        System.out.println("Counter " + counterId + " is paused, waiting...");
                        pauseLock.wait();
                    }
                }

                // Check for the global pause condition when both counters are paused
                synchronized (this) {
                    // If both counters are paused, wait on the global object
                    if (counter1Paused && counter2Paused) {
                        System.out.println("Both counters are paused, waiting...");
                        this.wait();
                        continue; // After being notified, check the pause condition again
                    }
                }

                BorrowRequest request = null;
                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        request = queue.poll();
                    }
                }

                // If a request is available, process it
                if (request != null) {
                    tryToBorrowBook(request.getCitizen(), request.getBookTitle(), request.getBookAuthor());
                } else {
                    // If the queue is empty, wait for new requests
                    synchronized (queue) {
                        System.out.println("Queue is empty, waiting for requests...");
                        queue.wait();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interruption status
                System.out.println("Thread interrupted: " + counterId);
                return; // Optionally return to end the thread or handle interruption appropriately
            }
        }
    }

    private void tryToBorrowBook(Citizen citizen, String bookTitle, String bookAuthor) {
        System.out.println("Attempting to borrow the book titled '" + bookTitle + "' by '" + bookAuthor + "' for citizen with ID: " + citizen.getId());

        // Retrieve the book based on title and author
        Book book = FirebaseDB.getBookByTitleAndAuthor(bookTitle, bookAuthor);
        if (book == null) {
            System.out.println("No book found with title '" + bookTitle + "' and author '" + bookAuthor + "'.");
            return;
        }
        System.out.println("Book found. Book ID: " + book.getId() + ", Available: " + book.isAvailable());

        // Ensure a lock is in place for the book if not already locked
        bookLocks.putIfAbsent(book.getId(), new ReentrantLock());
        Lock bookLock = bookLocks.get(book.getId());
        System.out.println("Lock acquired for book ID: " + book.getId());

        // Try to lock the book to start the borrowing process
        bookLock.lock();
        try {
            System.out.println("Locked book ID: " + book.getId() + ". Checking availability and membership validity.");

            // Check if the book is available and if the citizen has a valid membership
            if (book.isAvailable() && FirebaseDB.getMembershipIdById(citizen.getId()) != null) {
                // Update the book status to unavailable
                book.setAvailable(false);
                FirebaseDB.updateBook(book);
                System.out.println("Book '" + bookTitle + "' by '" + bookAuthor + "' is now borrowed by citizen ID: " + citizen.getId());
            } else {
                // Notify that the book is not available for borrowing
                System.out.println("Book unavailable or citizen does not have a valid membership. Book ID: " + book.getId());
            }
        } finally {
            // Unlock the book lock regardless of the outcome to ensure other threads can access it
            bookLock.unlock();
            System.out.println("Lock released for book ID: " + book.getId());
        }
    }
}