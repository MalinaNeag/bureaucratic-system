

public class Book {
    private String name;
    private String author;
    private boolean available;
    private String borrowDate;
    private String returnDate;
    private Citizen borrowedBy;

    public Book(String name, String author, boolean available) {
        this.name = name;
        this.author = author;
        this.available = available;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public Citizen getBorrowedBy() {
        return borrowedBy;
    }

    public void setBorrowedBy(Citizen borrowedBy) {
        this.borrowedBy = borrowedBy;
    }
}