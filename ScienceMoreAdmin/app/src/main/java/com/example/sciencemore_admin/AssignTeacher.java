package com.example.sciencemore_admin;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignTeacher extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Spinner classSpinner;
    private Spinner teacherNameSpinner;
    private TextInputEditText teacherDisplay;

    boolean isAvailabele;

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
        setupSpinners();
        loadSubjectsFromFireStore();
        loadTeacherFromFirebase();
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
                    startActivity(new Intent(AssignTeacher.this, AdminTeacherDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_students) {
                    Toast.makeText(AssignTeacher.this, "Students Selected", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AssignTeacher.this, AdminStudentDashboard.class));
                    return true;
                }
                return false;
            }
        });
    }

    public void backBtn(View v){
        startActivity(new Intent(this, AdminTeacherDashboard.class));
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
                        if (!subjectNameList.isEmpty()) {
                            classSpinner.setSelection(0); // Set initial selection
                        }
                    } else {
                        Toast.makeText(this, "Failed to load subjects" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTeacherFromFirebase() {
        db.collection("Teacher")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        teacherNameList.clear();
                        teacherDocIdMap.clear();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            String teacherDocId = documentSnapshot.getId();
                            String currentTeacherName = documentSnapshot.getString("teacherName");
                            if (currentTeacherName != null) {
                                teacherNameList.add(currentTeacherName);
                                teacherDocIdMap.put(currentTeacherName, teacherDocId);
                            }

                        }
                        ((ArrayAdapter) teacherNameSpinner.getAdapter()).notifyDataSetChanged();
                        if (!teacherNameList.isEmpty()) {
                            teacherNameSpinner.setSelection(0); // Set initial selection
                        }
                    } else {
                        Toast.makeText(this, "Failed to load Teacher" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void saveDataInFirebase(View v) {
        if(selectedTeacherName == null || selectedSubjectName == null) {
            Toast.makeText(AssignTeacher.this, "Please Select Teacher and subject", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference collectionReference = db.collection("teacherSubject");
        // Corrected: Use "subjectName" for consistency with your put operation
        Query query = collectionReference.whereEqualTo("teacherName", selectedTeacherName).whereEqualTo("subjectName", selectedSubjectName);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // isAvailabele = true; // This line is likely redundant, can be removed
                    Toast.makeText(AssignTeacher.this, "Already Assigned", Toast.LENGTH_SHORT).show(); // Consistent context
                } else {
                    Map<String , Object> assignedData = new HashMap<>();
                    assignedData.put("teacherName" , selectedTeacherName);
                    assignedData.put("subjectName" , selectedSubjectName); // This field name should match the query

                    db.collection("teacherSubject")
                            .add(assignedData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(AssignTeacher.this, "Assigned successfully!", Toast.LENGTH_SHORT).show(); // Consistent context
                                resetSelections();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AssignTeacher.this, "Error in assigning: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // Consistent context
                            });
                }
            } else {
                Toast.makeText(AssignTeacher.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show(); // Consistent context and better error message
            }
        });
    }

    private void resetSelections(){
        teacherDisplay.setText("");
        classSpinner.setSelection(0);
        teacherNameSpinner.setSelection(0);
    }


}