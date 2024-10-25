public class Citizen {
    private String name;
    private String id;

    // No-argument constructor (needed for deserialization)
    public Citizen() {}

    // Constructor with name and id
    public Citizen(String name, String id) {
        this.name = name;
        this.id = id;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}