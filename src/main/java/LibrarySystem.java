package main.java;

class LibrarySystem {
    private Book book1 = new Book("Java Concurrency in Practice");
    private Enrollment enrollment = new Enrollment();

    // Method to allow a citizen to borrow a book
    public void borrowBook(Citizen citizen) {
        if (citizen.hasMembership()) {
            if (!book1.borrowBook(citizen)) {
                System.out.println("Retrying borrowing process for " + citizen.getId());
            }
        } else {
            System.out.println(citizen.getId() + " does not have membership. Borrowing denied.");
        }
    }

    // Method to handle enrollment via the Enrollment Department
    public void handleEnrollment(Citizen citizen) {
        enrollment.verifyAndGenerateMembership(citizen);  // Verify ID and generate membership
    }
}