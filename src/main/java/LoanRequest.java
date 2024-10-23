package main.java;

public class LoanRequest {
    private Citizen citizen;
    private String bookTitle;

    public LoanRequest(Citizen citizen, String bookTitle) {
        this.citizen = citizen;
        this.bookTitle = bookTitle;
    }

    public Citizen getCitizen() {
        return citizen;
    }

    public String getBookTitle() {
        return bookTitle;
    }
}