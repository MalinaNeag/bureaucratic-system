public class Book {
    private String id; // Unique identifier for each book
    private String name;
    private String author;
    private boolean available;
    private String borrowDate;
    private String returnDate;
    private Citizen borrowedBy;

    public Book(String id, String name, String author, boolean available) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.available = available;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public Citizen getBorrowedBy() { return borrowedBy; }
    public void setBorrowedBy(Citizen borrowedBy) { this.borrowedBy = borrowedBy; }

    public void setName(String name) {
        this.name = name;
    }
}
