package com.example.sciencemore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboard extends AppCompatActivity {

    private LinearLayout cardContainer;
    private FirebaseFirestore db;

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

        cardContainer = findViewById(R.id.cardContainer);
        db = FirebaseFirestore.getInstance();
        
        Intent intent = getIntent();
        String studentName = intent.getStringExtra("studentName");

        fetchStudentSubjects(studentName);
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
            subject.setText(currentItem.getDescription());

            // Add the inflated cardview to the container
            cardContainer.addView(cardViewLayout);
        }
    }

    public void back(View v){
        startActivity(new Intent(StudentDashboard.this, StudentTeacherLogin.class));
    }
}