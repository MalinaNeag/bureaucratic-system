
public class LoanRequest {
    private String citizenId;
    private String bookTitle;
    private String bookAuthor;

    public LoanRequest(String citizenId, String bookTitle, String bookAuthor) {
        this.citizenId = citizenId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
    }

    public String getCitizenId() {
        return citizenId;
    }
    public String getBookTitle() {
        return bookTitle;
    }
    public String getBookAuthor() {
        return bookAuthor;
    }
}