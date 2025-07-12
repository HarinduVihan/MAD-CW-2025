package com.example.sciencemore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboard extends AppCompatActivity {

    private LinearLayout cardContainer;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();

        cardContainer = findViewById(R.id.cardContainer);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String studentName = intent.getStringExtra("studentName");

        fetchStudentSubjects(studentName);

        TextView teacherDisplayName = findViewById(R.id.studentName);
        try{
            //get only the firstname if there is multiple names
            assert studentName != null;
            String studentFirstName = studentName.substring(0,studentName.indexOf(' '));
            //set the text view to studentName
            teacherDisplayName.setText("Hi "+studentFirstName+ " 🙋‍♂️");
        } catch (Exception e) {
            //set the text view to studentName
            teacherDisplayName.setText("Hi "+studentName+ " 🙋‍♂️");
        }

    }

    private void fetchStudentSubjects(String studentName) {
        if (studentName == null || studentName.isEmpty()) {
            Toast.makeText(this, "Student name not provided.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("studentSubject")
                .whereEqualTo("studentName", studentName) // Query based on studentName
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CardItem> cardDataList = new ArrayList<>();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(StudentDashboard.this, "No subjects found for " + studentName, Toast.LENGTH_LONG).show();
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String subject = document.getString("subject");
                            if (subject != null) {
                                cardDataList.add(new CardItem(subject, subject)); // Assuming subject is both title and description for now
                            } else {
                            }
                        }
                        populateCardViews(cardDataList); // Populate cards with fetched data
                    } else {
                        Toast.makeText(StudentDashboard.this, "Error loading subjects: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void populateCardViews(List<CardItem> dataList) {
        cardContainer.removeAllViews();

        if (dataList.isEmpty()) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        // Get the data from list and add a card view
        for (int i = 0; i < dataList.size(); i++) {
            CardItem currentItem = dataList.get(i); // Get item by index

            // Inflate the subject card
            View cardViewLayout = inflater.inflate(R.layout.student_dashboard_subject_card, cardContainer, false);

            TextView subject = cardViewLayout.findViewById(R.id.subjecttxt);

            // Populate the views with data
            String subjectText = currentItem.getDescription();
            subject.setText(subjectText);

            // Set an OnClickListener for the entire cardViewLayout
            cardViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When the card is clicked, create the Intent
                    Intent intent = new Intent(StudentDashboard.this, StudentViewSubjectMaterial.class);

                    // Put the subjectText as an extra in the Intent
                    // "subject_name" is a key you will use to retrieve this data in StudentViewSubjectMaterial
                    intent.putExtra("subjectName", subjectText);

                    // Start the new Activity
                    startActivity(intent);
                }
            });

            // Add the inflated cardview to the container
            cardContainer.addView(cardViewLayout);
        }
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    startActivity(new Intent(StudentDashboard.this, StudentDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_result) {
                    startActivity(new Intent(StudentDashboard.this, StudentAssignmentResults.class));
                    return true;
                }
                return false;
            }
        });
    }


    public void back(View v){
        startActivity(new Intent(StudentDashboard.this, StudentTeacherLogin.class));
    }
}