import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.WriteResult; // Import WriteResult for update return type

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.cloud.FirestoreClient;


public class FirebaseDB {
    private static Firestore firestore;


    public static void initFirebase() throws IOException {
        // Load the service account key JSON file
        InputStream serviceAccount = new FileInputStream("src/main/resources/key.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        // Set up Firebase options
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();

        // Initialize FirebaseApp if not already initialized
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        // Assign the Firestore instance to the static field
        firestore = FirestoreClient.getFirestore();

        System.out.println("Firestore initialized successfully.");
    }

    // Fetch the citizen's membership ID from Firestore by name
    public static String getMembershipIdById(String citizenId) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection("memberships")
                    .whereEqualTo("citizenId", citizenId)
                    .get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            if (!documents.isEmpty()) {
                return documents.get(0).getId(); // Get the document ID as membership ID
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // If no membership found
    }

    // Updates the book status in Firestore and sets the borrowedBy field to the citizen's membership ID
    public static boolean borrowBook(String bookId, String membershipId) {
        // Update book information in Firestore
        DocumentReference bookRef = firestore.collection("books").document(bookId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("available", false);
        updates.put("borrowedBy", membershipId);
        updates.put("borrowDate", "2024-10-24"); // Use current date in actual implementation

        // Use WriteResult to capture the result of the update operation
        ApiFuture<WriteResult> future = bookRef.update(updates);
        try {
            WriteResult result = future.get(); // Wait for the update to complete
            System.out.println("Book borrowed by membership ID: " + membershipId + " at time: " + result.getUpdateTime());
            return true;
        } catch (Exception e) {
            System.out.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    // Fetch a book by title and author
    public static Book getBookByTitleAndAuthor(String title, String author) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection("books")
                    .whereEqualTo("name", title)
                    .whereEqualTo("author", author)
                    .whereEqualTo("available", true) // Ensure the book is available
                    .get();

            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            if (!documents.isEmpty()) {
                return documents.get(0).toObject(Book.class); // Return the first available book
            }
        } catch (Exception e) {
            System.out.println("Error fetching book: " + e.getMessage());
        }
        return null; // If no book found
    }

    public static void updateBook(Book book) {
        DocumentReference bookRef = firestore.collection("books").document(book.getId());
        Map<String, Object> updates = new HashMap<>();
        updates.put("available", book.isAvailable());
       // updates.put("borrowedBy", book.getBorrowedBy().getMembershipId());
        updates.put("borrowDate", book.getBorrowDate());

        ApiFuture<WriteResult> future = bookRef.update(updates);
        try {
            WriteResult result = future.get(); // Wait for the update to complete
            System.out.println("Book updated successfully at time: " + result.getUpdateTime());
        } catch (Exception e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }
    public static void addMembership(Membership newMembership) {
        // Create a map of membership fields to add to Firestore
        Map<String, Object> membershipData = new HashMap<>();
        membershipData.put("membershipNumber", newMembership.getMembershipNumber());
        membershipData.put("citizenName", newMembership.getCitizenName());
        membershipData.put("issueDate", newMembership.getIssueDate());
        membershipData.put("citizenId", newMembership.getCitizenId());

        // Add a new document in the "memberships" collection
        ApiFuture<WriteResult> future = firestore.collection("memberships").document(newMembership.getMembershipNumber()).set(membershipData);

        try {
            // Block and wait for the result
            WriteResult result = future.get();
            System.out.println("Membership added successfully at time: " + result.getUpdateTime());
        } catch (Exception e) {
            System.out.println("Error adding membership: " + e.getMessage());
        }
    }
}