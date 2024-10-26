import java.util.ArrayList;
import java.util.List;

public class Citizen {
    private String name;
    private String id;
    private List<String> obtainedDocuments;

    public Citizen() {
        this.obtainedDocuments = new ArrayList<>();
    }

    public Citizen(String name, String id) {
        this.name = name;
        this.id = id;
        this.obtainedDocuments = new ArrayList<>();
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // Obtained Documents Management
    public List<String> getObtainedDocuments() {
        //
        return obtainedDocuments;
    }

    public void addDocument(String documentName) {
        obtainedDocuments.add(documentName);
    }


}