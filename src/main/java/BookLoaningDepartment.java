import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Queue;
import java.util.LinkedList;

public class BookLoaningDepartment {
    private static BookLoaningDepartment instance;
    private final Queue<Citizen> queue = new LinkedList<>();
    private final Lock bookLoanLock = new ReentrantLock();

    protected BookLoaningDepartment() {}

    public static synchronized BookLoaningDepartment getInstance() {
        if (instance == null) {
            instance = new BookLoaningDepartment();
        }
        return instance;
    }

    public void borrowBook(Citizen citizen, String bookTitle, String bookAuthor) {
        synchronized (queue) {
            queue.add(citizen);
        }

        new Thread(() -> {
            try {
                synchronized (queue) {
                    while (queue.peek() != citizen) {
                        queue.wait();
                    }
                }

                bookLoanLock.lock();

                String membershipId = FirebaseDB.getMembershipIdById(citizen.getId());
                EnrollmentDepartment enrollmentDepartment = EnrollmentDepartment.getInstance();
                if (membershipId == null || !enrollmentDepartment.isCitizenEnrolled(citizen)) {
                    System.out.println("No valid membership for citizen: " + citizen.getId());
                    return;
                }

                Book book = FirebaseDB.getBookByTitleAndAuthor(bookTitle, bookAuthor);
                //FirebaseDB.addBook(book);
                //System.out.println(book.getId());
                if (book != null && book.isAvailable()) {
                    book.setAvailable(false);
                    book.setBorrowedBy(membershipId); // Set borrower with citizen details
                    book.setBorrowDate("2024-10-24"); // Placeholder date
                    FirebaseDB.updateBook(book);
                    System.out.println("Book '" + bookTitle + "' borrowed by " + membershipId);
                } else {
                    System.out.println("Book '" + bookTitle + "' is not available or does not exist.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                bookLoanLock.unlock();
                synchronized (queue) {
                    queue.remove();
                    queue.notifyAll();
                }
            }
        }).start();
    }
}