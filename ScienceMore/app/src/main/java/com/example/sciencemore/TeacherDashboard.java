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

public class TeacherDashboard extends AppCompatActivity {

    private LinearLayout cardContainer;

    private FirebaseFirestore db;

    private BottomNavigationView bottomNavigationView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_dashboard);
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
        String teacherName = intent.getStringExtra("teacherName");

        fetchTeacherGrades(teacherName);

        TextView teacherDisplayName = findViewById(R.id.teacherName);

        try{
            //get only the firstname if there is multiple names
            assert teacherName != null;
            String teacherFirstName = teacherName.substring(0,teacherName.indexOf(' '));
            //set the text view to teacher name
            teacherDisplayName.setText("Welcome "+teacherFirstName+ " 🙏");
        } catch (Exception e) {
            //set the text view to teacher name
            teacherDisplayName.setText("Welcome "+teacherName+ " 🙏");
        }

    }

    private void fetchTeacherGrades(String teacherName){
        if(teacherName == null || teacherName.isEmpty()){
            Toast.makeText(this, "Teacher name is not provided", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("teacherSubject")
                .whereEqualTo("teacherName",teacherName) //Query based on teacher name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CardItem> cardDataList = new ArrayList<>();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "No grades found for " + teacherName, Toast.LENGTH_SHORT).show();
                        }

                        for(QueryDocumentSnapshot document : task.getResult()){
                            String grades = document.getString("subjectName");
                            if(grades != null){
                                cardDataList.add(new CardItem(grades,grades)); // Assuming grade is both title and description for now
                            }
                        }
                        populateCardViews(cardDataList); //Populate Cards with fetched data
                    }else{
                        Toast.makeText(this, "Error loading subjects" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onClickCard(View v){}

    private void populateCardViews(List<CardItem> dataList) {
        //clear any existing views
        cardContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        //get the data from list and add a card view
        for (int i = 0; i < dataList.size(); i++) {
            CardItem currentItem = dataList.get(i); // Get item by index

            //Inflate the subject card
            View cardViewLayout = inflater.inflate(R.layout.teacher_dashboard_grade_card, cardContainer, false);

            TextView subject = cardViewLayout.findViewById(R.id.gardetxt);

            //populate the views with data
            String subjectName = currentItem.getDescription();
            subject.setText(subjectName);

            // Set an OnClickListener for the entire cardViewLayout
            cardViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When the card is clicked, create the Intent
                    Intent intent = new Intent(TeacherDashboard.this, TeacherAddCourseMaterial.class);

                    // Put the subjectText as an extra in the Intent
                    // "subject_name" is a key you will use to retrieve this data in StudentViewSubjectMaterial
                    intent.putExtra("subjectName", subjectName);

                    // Start the new Activity
                    startActivity(intent);
                }
            });

            //Add the inflated cardview to the container
            cardContainer.addView(cardViewLayout);
        }
    }

    public void back(View v){
        startActivity(new Intent(TeacherDashboard.this, StudentTeacherLogin.class));
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    startActivity(new Intent(TeacherDashboard.this, TeacherDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_result) {
                    startActivity(new Intent(TeacherDashboard.this, TeacherMarkAssignment.class));
                    return true;
                }else if (itemId == R.id.bottom_nav_qr) {
                    startActivity(new Intent(TeacherDashboard.this, MarkAttendance.class));
                    return true;
                }
                return false;
            }
        });
    }
}