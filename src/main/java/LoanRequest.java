
public class LoanRequest {
    private String citizenName;
    private String bookTitle;
    private String bookAuthor;

    public LoanRequest(String citizenName, String bookTitle, String bookAuthor) {
        this.citizenName = citizenName;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
    }

    public String getCitizenName() {
        return citizenName;
    }
    public String getBookTitle() {
        return bookTitle;
    }
    public String getBookAuthor() {
        return bookAuthor;
    }
}