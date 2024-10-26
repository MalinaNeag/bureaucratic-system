import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FirebaseDB {
    private static Firestore firestore;

    public static void initFirebase() throws IOException {
        InputStream serviceAccount = new FileInputStream("src/main/resources/key.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        firestore = FirestoreClient.getFirestore();
        System.out.println("Firestore initialized successfully.");
    }

    public static String getMembershipIdById(String citizenId) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection("memberships")
                    .whereEqualTo("citizenId", citizenId).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            if (!documents.isEmpty()) return documents.get(0).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean borrowBook(String bookId, String membershipId) {
        DocumentReference bookRef = firestore.collection("books").document(bookId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("available", false);
        updates.put("borrowedBy", membershipId);
        updates.put("borrowDate", "2024-10-24");

        ApiFuture<WriteResult> future = bookRef.update(updates);
        try {
            WriteResult result = future.get();
            System.out.println("Book borrowed by membership ID: " + membershipId + " at time: " + result.getUpdateTime());
            return true;
        } catch (Exception e) {
            System.out.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    public static void updateBook(Book book) {
        DocumentReference bookRef = firestore.collection("books").document(book.getId());
        Map<String, Object> updates = new HashMap<>();
        updates.put("available", book.isAvailable());
        updates.put("borrowedBy", book.getBorrowedBy());
        updates.put("borrowDate", book.getBorrowDate());

        ApiFuture<WriteResult> future = bookRef.update(updates);
        try {
            WriteResult result = future.get();
            System.out.println("Book updated successfully at time: " + result.getUpdateTime());
        } catch (Exception e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }

    public static Book getBookByTitleAndAuthor(String title, String author) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection("books")
                    .whereEqualTo("name", title)
                    .whereEqualTo("author", author)
                    .whereEqualTo("available", true).get();
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            if (!documents.isEmpty()) return documents.get(0).toObject(Book.class);
        } catch (Exception e) {
            System.out.println("Error fetching book: " + e.getMessage());
        }
        return null;
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

    public static void addOrUpdateDocumentForCitizen(String citizenId, String documentName) {
        DocumentReference docRef = firestore.collection("citizenDocuments").document(citizenId);

        // Execute a transaction to update or add a new document to the list of documents
        ApiFuture<Void> transactionFuture = firestore.runTransaction(trans -> {
            // Correctly fetch the document snapshot by resolving the future
            DocumentSnapshot snapshot = trans.get(docRef).get();
            List<String> documents;
            if (snapshot.exists() && snapshot.contains("documentNames")) {
                documents = (List<String>) snapshot.get("documentNames");
                if (!documents.contains(documentName)) {
                    documents.add(documentName);
                }
            } else {
                documents = new ArrayList<>();
                documents.add(documentName);
            }
            trans.set(docRef, Collections.singletonMap("documentNames", documents), SetOptions.merge());
            return null; // No result needed
        });

        try {
            transactionFuture.get(); // Blocking call to ensure transaction completes
            System.out.println("Document successfully added/updated for citizen: " + citizenId);
        } catch (Exception e) {
            System.err.println("Error during transaction for citizen: " + e.getMessage());
        }
    }
    public static boolean hasDocument(String citizenId, String documentName) {
        DocumentReference docRef = firestore.collection("citizenDocuments").document(citizenId);
        try {
            DocumentSnapshot documentSnapshot = docRef.get().get();
            if (documentSnapshot.exists()) {
                Object data = documentSnapshot.get("documentNames");
                if (data instanceof List<?>) {
                    List<?> documents = (List<?>) data;
                    if (documents.stream().allMatch(item -> item instanceof String)) {
                        return documents.contains(documentName);
                    } else {
                        System.err.println("Document list contains non-string items.");
                    }
                } else {
                    assert data != null;
                    System.err.println("Expected documentNames to be a list but was " + data.getClass().getSimpleName());
                }
            } else {
                System.out.println("No document found for citizen ID: " + citizenId);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error checking document for citizen: " + e.getMessage());
            return false;
        }
    }
}