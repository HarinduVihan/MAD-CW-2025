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

public class StudentAssignmentResults extends AppCompatActivity {

    private static final String TAG = "StudentAssignmentResults";
    private LinearLayout cardContainer;
    private FirebaseFirestore db;
    private List<AssignmentResults> combinedAssignmentResults = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;

    String studentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_assignment_results);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardContainer = findViewById(R.id.cardContainer);
        bottomNavigationView = findViewById(R.id.bottomnav);

        Intent intent = getIntent();
        studentName = intent.getStringExtra("studentName");
        if (studentName == null || studentName.isEmpty()) {
            Log.e(TAG, "Student name not received via Intent.");
            Toast.makeText(this, "Error: Student name not found.", Toast.LENGTH_LONG).show();
        }

        db = FirebaseFirestore.getInstance();
        loadAssignmentData();

        NavigationBar();
    }

    private void loadAssignmentData() {
        if (studentName == null || studentName.isEmpty()) {
            Log.e(TAG, "Student name is null or empty when trying to load assignment data.");
            Toast.makeText(this, "Cannot load data: Student name is missing.", Toast.LENGTH_SHORT).show();
            populateCardViews(new ArrayList<>());
            return;
        }

        CollectionReference collectionReference = db.collection("AssignmentResult");
        Query query = collectionReference.whereEqualTo("studentName", studentName);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if(querySnapshot != null && !querySnapshot.isEmpty()) {
                    List<AssignmentResultHelp> tempResult = new ArrayList<>();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String assignmentName = document.getString("AssignmentName");
                        String result = document.getString("Result");

                        if (assignmentName != null && result != null) {
                            tempResult.add(new AssignmentResultHelp(assignmentName, result)); // Pass assignmentName as the ID
                        } else {
                            Log.w(TAG, "Missing AssignmentName or Result in document: " + document.getId());
                        }
                    }
                    fetchAssignmentDetails(tempResult);
                } else {
                    Toast.makeText(this, "No assignment results found for " + studentName, Toast.LENGTH_SHORT).show();
                    populateCardViews(new ArrayList<>());
                }
            } else {
                Log.e(TAG, "Error getting assignment results: ", task.getException());
                Toast.makeText(this, "Error loading results: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                populateCardViews(new ArrayList<>());
            }
        });
    }

    private void fetchAssignmentDetails(List<AssignmentResultHelp> tempResults) {
        combinedAssignmentResults.clear();
        final int[] completedFetches = {0};

        if (tempResults.isEmpty()) {
            populateCardViews(new ArrayList<>());
            return;
        }

        for (AssignmentResultHelp minimalResult : tempResults) {
            // MODIFIED: Use assignmentNameToFetch as the linking key
            String assignmentNameToFetch = minimalResult.getAssignmentId(); // This now holds the AssignmentName
            String resultScore = minimalResult.getResult();

            if (assignmentNameToFetch == null) {
                Log.w(TAG, "Skipping assignment with null AssignmentName.");
                completedFetches[0]++;
                if (completedFetches[0] == tempResults.size()) {
                    populateCardViews(combinedAssignmentResults);
                    Toast.makeText(StudentAssignmentResults.this, "All assignment details loaded (some skipped).", Toast.LENGTH_SHORT).show();
                }
                continue;
            }

            CollectionReference assignmentRef = db.collection("Assignment");
            // MODIFIED: Query for the "Assignment" collection using "assignmentName" field
            Query queryAssignment = assignmentRef.whereEqualTo("assignmentName", assignmentNameToFetch);

            queryAssignment.get().addOnCompleteListener(task -> {
                completedFetches[0]++;

                if (task.isSuccessful()) {
                    QuerySnapshot assignmentSnapshot = task.getResult();
                    if (assignmentSnapshot != null && !assignmentSnapshot.isEmpty()) {
                        DocumentSnapshot assignmentDoc = assignmentSnapshot.getDocuments().get(0);

                        String assignmentNameFromDetails = assignmentDoc.getString("assignmentName");
                        String assignmentDescription = assignmentDoc.getString("assignmentDescription");
                        String subject = assignmentDoc.getString("subject");

                        AssignmentResults finalResult = new AssignmentResults(
                                subject,
                                assignmentDescription != null ? assignmentDescription : assignmentNameFromDetails,
                                resultScore
                        );
                        combinedAssignmentResults.add(finalResult);
                    } else {
                        Log.w(TAG, "Assignment details not found for Name: " + assignmentNameToFetch);
                        combinedAssignmentResults.add(new AssignmentResults("Unknown Subject", "Assignment details missing (Name: " + assignmentNameToFetch + ")", resultScore));
                    }
                } else {
                    Log.e(TAG, "Error getting assignment details for Name: " + assignmentNameToFetch, task.getException());
                    combinedAssignmentResults.add(new AssignmentResults("Error", "Could not load details for Name: " + assignmentNameToFetch, resultScore));
                }

                if (completedFetches[0] == tempResults.size()) {
                    populateCardViews(combinedAssignmentResults);
                    Toast.makeText(StudentAssignmentResults.this, "All assignment details loaded.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void populateCardViews(List<AssignmentResults> results){
        runOnUiThread(() -> {
            cardContainer.removeAllViews();

            if (results.isEmpty()) {
                TextView noResultsText = new TextView(this);
                noResultsText.setText("No assignments to display.");
                noResultsText.setPadding(32, 32, 32, 32);
                noResultsText.setTextSize(18);
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
                    intent.putExtra("studentName", studentName);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}