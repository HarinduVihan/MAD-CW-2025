package com.example.sciencemore_admin;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
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

public class AssignStudent extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Spinner classSpinner;
    private Spinner studentNameSpinner;
    private TextInputEditText studentDisplay;

    // Firestore
    private FirebaseFirestore db;

    // Store data getting from firestore
    private List<String> subjectNameList;
    private Map<String , String> subjectIdMap;
    private List<String> studentNamesList;
    private Map<String , String> studentDocIdMap;

    // store the assigning student name and subject name
    private String selectedSubjectNameActual;
    private String selectedStudentNameActual;

    // store the ids
    private String selectedSubjectDocId;
    private String selectedStudentDocId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assign_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initializing FireStore
        db = FirebaseFirestore.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        classSpinner = findViewById(R.id.classSpinner);
        studentNameSpinner = findViewById(R.id.studentSpinner);
        studentDisplay = findViewById(R.id.editUserName);

        subjectNameList  = new ArrayList<>();
        subjectIdMap = new HashMap<>();
        studentNamesList  = new ArrayList<>();
        studentDocIdMap = new HashMap<>();

        // calling methods
        NavigationBar();
        setupSpinners();
        loadSubjectsFromFirestore();
        loadStudentsFromFirestore();
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

    // set data to the spinners
    private void setupSpinners() {
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectNameList);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(subjectAdapter);

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubjectNameActual = parent.getItemAtPosition(position).toString(); // Store the actual subject name
                selectedSubjectDocId = subjectIdMap.get(selectedSubjectNameActual); // Store the subject document ID
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSubjectNameActual = null;
                selectedSubjectDocId = null;
            }
        });

        ArrayAdapter<String> studentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, studentNamesList);
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentNameSpinner.setAdapter(studentAdapter);

        studentNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStudentNameActual = parent.getItemAtPosition(position).toString(); // Store the actual student name
                selectedStudentDocId = studentDocIdMap.get(selectedStudentNameActual); // Store the student document ID
                studentDisplay.setText(selectedStudentNameActual);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedStudentNameActual = null;
                selectedStudentDocId = null;
                studentDisplay.setText("");
            }
        });
    }

    // load subject data from firestore
    private void loadSubjectsFromFirestore() {
        db.collection("Subject")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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
                        Toast.makeText(AssignStudent.this, "Failed to load subjects: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // load student data from firestore
    private void loadStudentsFromFirestore() {
        db.collection("Student")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        studentNamesList.clear();
                        studentDocIdMap.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String studentDocId = document.getId();
                            String currentStudentName = document.getString("studentName");
                            if (currentStudentName != null) {
                                studentNamesList.add(currentStudentName);
                                studentDocIdMap.put(currentStudentName, studentDocId);
                            }
                        }
                        ((ArrayAdapter) studentNameSpinner.getAdapter()).notifyDataSetChanged();
                    } else {
                        Toast.makeText(AssignStudent.this, "Failed to load students: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // save data in firestore
    public void saveDataInFireStore(View v){
        if (selectedStudentNameActual != null && selectedSubjectNameActual != null){
            Map<String, Object> assignmentData = new HashMap<>();
            assignmentData.put("studentName", selectedStudentNameActual);
            assignmentData.put("subject", selectedSubjectNameActual);

            db.collection("studentSubject")
                    .add(assignmentData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AssignStudent.this, "Assignment created successfully!", Toast.LENGTH_SHORT).show();
                        resetSelections();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AssignStudent.this, "Error adding assignment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(AssignStudent.this, "Please select both a student and a subject.", Toast.LENGTH_SHORT).show();
        }
    }

    // reset all texts and spinners
    private void resetSelections(){
        studentDisplay.setText("");
        classSpinner.setSelection(0);
        studentNameSpinner.setSelection(0);
        selectedSubjectNameActual = null;
        selectedStudentNameActual = null;
        selectedSubjectDocId = null;
        selectedStudentDocId = null;
    }
}