package com.example.sciencemore_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminManageStudent extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private AutoCompleteTextView actvStudentNames;
    private TextInputEditText etManageStudentAge, etManageStudentPassword, etManageConfirmStudentPassword;
    private Button btnUpdateStudent, btnDeleteStudent;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> studentNameList = new ArrayList<>();
    private ArrayAdapter<String> nameAdapter;

    private String selectedDocId = null; // Store the selected document ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_manage_student);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminManageStudentLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();

        // Initialize Views
        actvStudentNames = findViewById(R.id.actvStudentNames);
        etManageStudentAge = findViewById(R.id.etManageStudentAge);
        etManageStudentPassword = findViewById(R.id.etManageStudentPassword);
        etManageConfirmStudentPassword = findViewById(R.id.etManageConfirmStudentPassword);
        btnUpdateStudent = findViewById(R.id.btnUpdateStudent);
        btnDeleteStudent = findViewById(R.id.btnDeleteStudent);

        nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, studentNameList);
        actvStudentNames.setAdapter(nameAdapter);
        actvStudentNames.setOnClickListener(v -> actvStudentNames.showDropDown());

        // On name selected, fetch and populate student details
        actvStudentNames.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = parent.getItemAtPosition(position).toString();
            fetchStudentDetails(selectedName);
        });

        fetchStudentNames();

        btnUpdateStudent.setOnClickListener(v -> updateStudent());

        btnDeleteStudent.setOnClickListener(v -> deleteStudent());
    }

    private void fetchStudentNames() {
        db.collection("Student")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    studentNameList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("studentName");
                        if (name != null) {
                            studentNameList.add(name);
                        }
                    }
                    nameAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load student names", Toast.LENGTH_SHORT).show());
    }

    private void fetchStudentDetails(String name) {
        db.collection("Student")
                .whereEqualTo("studentName", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        selectedDocId = doc.getId(); // Save ID for update/delete
                        String age = doc.getString("studentAge");
                        String password = doc.getString("studentPassword");

                        etManageStudentAge.setText(age);
                        etManageStudentPassword.setText(password);
                        etManageConfirmStudentPassword.setText(password);
                    } else {
                        Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
                        selectedDocId = null;
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error fetching student details", Toast.LENGTH_SHORT).show());
    }

    private void updateStudent() {
        String name = actvStudentNames.getText().toString().trim();
        String age = etManageStudentAge.getText().toString().trim();
        String password = etManageStudentPassword.getText().toString().trim();
        String confirmPassword = etManageConfirmStudentPassword.getText().toString().trim();

        if (name.isEmpty() || age.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDocId == null) {
            Toast.makeText(this, "Please select a student to update", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("studentName", name);
        updatedData.put("studentAge", age);
        updatedData.put("studentPassword", password);

        db.collection("Student").document(selectedDocId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Student updated successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                        })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }

    private void deleteStudent() {
        if (selectedDocId == null) {
            Toast.makeText(this, "Please select a student to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Student").document(selectedDocId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                    fetchStudentNames();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to delete student", Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        actvStudentNames.setText("");
        etManageStudentAge.setText("");
        etManageStudentPassword.setText("");
        etManageConfirmStudentPassword.setText("");
        selectedDocId = null;
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_nav_home) {
                startActivity(new Intent(this, AdminDashboard.class));
                return true;
            } else if (itemId == R.id.bottom_nav_teacher) {
                startActivity(new Intent(this, AdminTeacherDashboard.class));
                return true;
            } else if (itemId == R.id.bottom_nav_students) {
                startActivity(new Intent(this, AdminStudentDashboard.class));
                return true;
            }
            return false;
        });
    }

    public void btnAdminStudentManageBack(View view) {
        startActivity(new Intent(AdminManageStudent.this, AdminStudentDashboard.class));
    }
}
