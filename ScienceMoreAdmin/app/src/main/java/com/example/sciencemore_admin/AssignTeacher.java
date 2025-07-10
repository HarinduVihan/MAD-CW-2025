package com.example.sciencemore_admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignTeacher extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Spinner classSpinner;
    private Spinner TeacherNameSpinner;
    private TextInputEditText teacherDisplay;

    // Firestore
    private FirebaseFirestore db;

    // Store data getting from firestore
    private List<String> subjectNameList;
    private Map<String, String> subjectIdMap;
    private List<String> teacherNameList;
    private Map<String , String> teacherDocIdMap;

    // Store the assigning teacher name and subject name
    private String selectedTeacherName;
    private String selectedStudentDocId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assign_teacher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        classSpinner = findViewById(R.id.selectclass);
        TeacherNameSpinner = findViewById(R.id.selectteacher);
        teacherDisplay = findViewById(R.id.displayteacher);

        subjectNameList = new ArrayList<>();
        subjectIdMap = new HashMap<>();
        teacherNameList = new ArrayList<>();
        teacherDocIdMap = new HashMap<>();

        NavigationBar();

    }

    private void NavigationBar(){
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    Toast.makeText(AssignStudent.this, "Home Selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.bottom_nav_teacher) {
                    Toast.makeText(AssignStudent.this, "Teacher Selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.bottom_nav_students) {
                    Toast.makeText(AssignStudent.this, "Students Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }
}