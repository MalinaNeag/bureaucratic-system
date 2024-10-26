public class BorrowRequest {
    private final Citizen citizen;
    private final String bookTitle;
    private final String bookAuthor;

    public BorrowRequest(Citizen citizen, String bookTitle, String bookAuthor) {
        this.citizen = citizen;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
    }

    public Citizen getCitizen() {
        return citizen;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }
}