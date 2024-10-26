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
                synchronized (this) {
                    while ((counterId == 1 && counter1Paused) || (counterId == 2 && counter2Paused)) wait();
                }
                BorrowRequest request;
                synchronized (queue) {
                    while (queue.isEmpty()) queue.wait();
                    request = queue.poll();
                }
                if (request != null) tryToBorrowBook(request.getCitizen(), request.getBookTitle(), request.getBookAuthor());
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }
    }

    private void tryToBorrowBook(Citizen citizen, String bookTitle, String bookAuthor) {
        Book book = FirebaseDB.getBookByTitleAndAuthor(bookTitle, bookAuthor);
        if (book == null) return;

        bookLocks.putIfAbsent(book.getId(), new ReentrantLock());
        Lock bookLock = bookLocks.get(book.getId());

        bookLock.lock();
        try {
            if (book.isAvailable() && FirebaseDB.getMembershipIdById(citizen.getId()) != null) {
                book.setAvailable(false);
                FirebaseDB.updateBook(book);
            } else System.out.println("Book unavailable.");
        } finally { bookLock.unlock(); }
    }
}