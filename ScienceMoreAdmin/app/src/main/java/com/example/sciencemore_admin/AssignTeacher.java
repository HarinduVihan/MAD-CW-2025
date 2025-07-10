package com.example.sciencemore_admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignTeacher extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Spinner classSpinner;
    private Spinner teacherNameSpinner;
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
    private String selectedSubjectName;

    private String selectedTeacherDocId;
    private String selectedSubjectDocId;

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
        teacherNameSpinner = findViewById(R.id.selectteacher);
        teacherDisplay = findViewById(R.id.displayteacher);

        subjectNameList = new ArrayList<>();
        subjectIdMap = new HashMap<>();
        teacherNameList = new ArrayList<>();
        teacherDocIdMap = new HashMap<>();

        NavigationBar();

    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    Toast.makeText(AssignTeacher.this, "Home Selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.bottom_nav_teacher) {
                    Toast.makeText(AssignTeacher.this, "Teacher Selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.bottom_nav_students) {
                    Toast.makeText(AssignTeacher.this, "Students Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    private void setupSpinners() {
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectNameList);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(subjectAdapter);

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubjectName = parent.getItemAtPosition(position).toString();
                selectedSubjectDocId = subjectIdMap.get(selectedSubjectName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSubjectName = null;
                selectedSubjectDocId = null;
            }
        });

        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherNameList);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherNameSpinner.setAdapter(teacherAdapter);

        teacherNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTeacherName = parent.getItemAtPosition(position).toString();
                selectedTeacherDocId = teacherDocIdMap.get(selectedTeacherName);
                teacherDisplay.setText(selectedTeacherName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTeacherName = null;
                selectedTeacherDocId = null;
                teacherDisplay.setText("");
            }
        });
    }

    private void loadSubjectsFromFireStore() {
        db.collection("Subject")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        subjectNameList.clear();
                        subjectIdMap.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String subjectDocId = document.getId();
                            String currentSubjectName = document.getString("subjectName");
                            if (currentSubjectName != null) {
                                subjectNameList.add(currentSubjectName);
                                subjectIdMap.put(currentSubjectName, subjectDocId);
                            }
                        }
                        ((ArrayAdapter) classSpinner.getAdapter()).notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load subjects" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}