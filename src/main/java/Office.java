import java.util.List;

public class Office {
    private String name;
    private int counters;
    private List<Document> documents;

    public Office(String name, int counters, List<Document> documents) {
        this.name = name;
        this.counters = counters;
        this.documents = documents;
    }

    public String getName() {
        return name;
    }

    public int getCounters() {
        return counters;
    }

    public List<Document> getDocuments() {
        return documents;
    }
}