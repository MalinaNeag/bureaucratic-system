import java.util.List;

public class Document {
    private String name;
    private List<String> dependencies;

    public Document(String name, List<String> dependencies) {
        this.name = name;
        this.dependencies = dependencies;
    }

    public String getName() {
        return name;
    }

    public List<String> getDependencies() {
        return dependencies;
    }
}