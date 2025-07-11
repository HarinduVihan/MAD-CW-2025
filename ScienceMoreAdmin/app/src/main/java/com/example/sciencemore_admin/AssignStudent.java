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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignStudent extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Spinner classSpinner;
    private Spinner studentNameSpinner;
    private TextInputEditText studentDisplay;
    boolean isAvailabele;

    // Firestore
    private FirebaseFirestore db;

    // Store data getting from firestore
    private List<String> subjectNameList;
    private Map<String , String> subjectIdMap;
    private List<String> studentNamesList;
    private Map<String , String> studentDocIdMap;

    // store the assigning student name and subject name
    private String selectedSubjectName;
    private String selectedStudentName;

    // store the DOC ids
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

    private void NavigationBar() {
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
                selectedSubjectName = parent.getItemAtPosition(position).toString(); // Store the actual subject name
                selectedSubjectDocId = subjectIdMap.get(selectedSubjectName); // Store the subject document ID
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSubjectName = null;
                selectedSubjectDocId = null;
            }
        });

        ArrayAdapter<String> studentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, studentNamesList);
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentNameSpinner.setAdapter(studentAdapter);

        studentNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStudentName = parent.getItemAtPosition(position).toString(); // Store the actual student name
                selectedStudentDocId = studentDocIdMap.get(selectedStudentName); // Store the student document ID
                studentDisplay.setText(selectedStudentName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedStudentName = null;
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
                        if (!subjectNameList.isEmpty()) {
                            classSpinner.setSelection(0);
                        }
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
                        if (!studentNamesList.isEmpty()) {
                            studentNameSpinner.setSelection(0);
                        }
                    } else {
                        Toast.makeText(AssignStudent.this, "Failed to load students: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // save data in firestore
    public void saveDataInFireStore(View v) {
        if (selectedStudentName == null || selectedSubjectName == null) {
            Toast.makeText(AssignStudent.this, "Please Select Student and subject", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference collectionReference = db.collection("studentSubject");
        Query query = collectionReference.whereEqualTo("studentName", selectedStudentName).whereEqualTo("subject", selectedSubjectName);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    isAvailabele = true;
                    Toast.makeText(this, "Already Assigned", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> assignmentData = new HashMap<>();
                    assignmentData.put("studentName", selectedStudentName);
                    assignmentData.put("subject", selectedSubjectName);

                    db.collection("studentSubject")
                            .add(assignmentData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(AssignStudent.this, "Assigned successfully!", Toast.LENGTH_SHORT).show();
                                resetSelections();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AssignStudent.this, "Error in assigning: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // reset all texts and spinners
    private void resetSelections(){
        studentDisplay.setText("");
        classSpinner.setSelection(0);
        studentNameSpinner.setSelection(0);
    }
}