package com.example.sciencemore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentAssignmentResults extends AppCompatActivity {

    private LinearLayout cardContainer;
    private FirebaseFirestore db;
    private List<AssignmentResults> combinedAssignmentResults = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;

    String studentName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignment_results);
        cardContainer = findViewById(R.id.cardContainer);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        studentName = intent.getStringExtra("studentName");

        db = FirebaseFirestore.getInstance();
        loadAssigmnentData();

        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();
    }

    private void loadAssigmnentData() {
        Intent intent = getIntent();
        String studentName = intent.getStringExtra("studentName");

        CollectionReference collectionReference = db.collection("AssignmentResult");
        Query query = collectionReference.whereEqualTo("studentName", studentName);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if(querySnapshot != null && !querySnapshot.isEmpty()) {
                    List<AssignmentResultHelp> tempResult = new ArrayList<>();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String assignmentId = document.getString("AssignmentId");
                        String result = document.getString("Result");

                        if (assignmentId != null && result != null) {
                            tempResult.add(new AssignmentResultHelp(assignmentId, result));
                        } else {
                            Log.w("Firestore", "Missing AssignmentId or Result in document: " + document.getId());
                        }
                    }
                    fetchAssignmentDetails(tempResult);
                }else {
                    Toast.makeText(this, "No assignment results found for " + studentName, Toast.LENGTH_SHORT).show();
                    populateCardViews(new ArrayList<>()); // Clear existing cards if any
                }
            }else {
                Log.e("Firestore", "Error getting assignment results: ", task.getException());
                Toast.makeText(this, "Error loading results: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchAssignmentDetails(List<AssignmentResultHelp> tempResults) {
        // Clear previous data before populating
        combinedAssignmentResults.clear();

        // Use a counter to know when all asynchronous fetches are complete
        final int[] completedFetches = {0};

        if (tempResults.isEmpty()) {
            populateCardViews(new ArrayList<>()); // No assignments to fetch, clear UI
            return;
        }

        for (AssignmentResultHelp minimalResult : tempResults) {
            String assignmentIdToFetch = minimalResult.getAssignmentId();
            String resultScore = minimalResult.getResult();

            CollectionReference assignmentRef = db.collection("Assignment");
            // Query for the specific assignment using its assignmentId
            Query queryAssignment = assignmentRef.whereEqualTo("assignmentId", assignmentIdToFetch);

            queryAssignment.get().addOnCompleteListener(task -> {
                completedFetches[0]++; // Increment the counter

                if (task.isSuccessful()) {
                    QuerySnapshot assignmentSnapshot = task.getResult();
                    if (assignmentSnapshot != null && !assignmentSnapshot.isEmpty()) {
                        DocumentSnapshot assignmentDoc = assignmentSnapshot.getDocuments().get(0); // Get the first one

                        String assignmentName = assignmentDoc.getString("assignmentName");
                        String assignmentDescription = assignmentDoc.getString("assignmentDescription");
                        String subject = assignmentDoc.getString("subject"); // From Assignment collection

                        // Create your AssignmentResults object with combined data
                        AssignmentResults finalResult = new AssignmentResults(
                                subject, // Subject from Assignment collection
                                assignmentDescription != null ? assignmentDescription : assignmentName, // Use description, fallback to name
                                resultScore
                        );
                        combinedAssignmentResults.add(finalResult);
                    } else {
                        combinedAssignmentResults.add(new AssignmentResults("Unknown Subject", "Assignment details missing", resultScore));
                    }
                } else {
                    combinedAssignmentResults.add(new AssignmentResults("Error", "Could not load details", resultScore));
                }

                // Check if all assignment details have been fetched
                if (completedFetches[0] == tempResults.size()) {
                    // All data is collected, now populate the UI
                    populateCardViews(combinedAssignmentResults);
                    Toast.makeText(StudentAssignmentResults.this, "All assignment details loaded.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void populateCardViews(List<AssignmentResults> results){
        // this runs on the UI thread
        runOnUiThread(() -> {
            cardContainer.removeAllViews(); // Clear existing views before adding new ones

            if (results.isEmpty()) {
                // Show a message if no results to display
                TextView noResultsText = new TextView(this);
                noResultsText.setText("No assignments to display.");
                noResultsText.setPadding(16, 16, 16, 16);
                cardContainer.addView(noResultsText);
                return;
            }

            LayoutInflater inflater = LayoutInflater.from(this);

            for (int i = 0; i < results.size(); i++){
                AssignmentResults currentItem = results.get(i);

                View cardViewLayout = inflater.inflate(R.layout.result_card, cardContainer, false);

                TextView subjectTextView  = cardViewLayout.findViewById(R.id.txtsubject);
                TextView descriptionTextView = cardViewLayout.findViewById(R.id.txtdescription);
                TextView marksTextView = cardViewLayout.findViewById(R.id.txtmark);

                // Set text, handling nulls
                subjectTextView.setText(currentItem.getSubject() != null ? currentItem.getSubject() : "N/A");
                descriptionTextView.setText(currentItem.getDescription() != null ? currentItem.getDescription() : "N/A");
                marksTextView.setText((currentItem.getMarks() != null ? currentItem.getMarks() : "N/A") + "/100");

                cardContainer.addView(cardViewLayout);
            }
        });
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    Intent intent = new Intent(StudentAssignmentResults.this, StudentDashboard.class);
                    intent.putExtra("studentName", studentName);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.bottom_nav_result) {
                    Intent intent = new Intent(StudentAssignmentResults.this, StudentAssignmentResults.class);
                    intent.putExtra("studentName",studentName);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}