package com.example.sciencemore_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class AdminManageTeacher extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private AutoCompleteTextView actvTeacherNames;
    private TextInputEditText etManageTeacherAge, etManageTeacherNic,
            etManageTeacherDes, etTeacherPassword, etTeacherConfirmPassword;
    private com.google.android.material.button.MaterialButton btnUpdateTeacher, btnDeleteTeacher;

    private List<String> teacherNameList = new ArrayList<>();
    private ArrayAdapter<String> nameAdapter;
    private String selectedDocId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_manage_teacher);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminManageTeacherLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();

        // Initialize UI
        actvTeacherNames = findViewById(R.id.actvTeacherNames);
        etManageTeacherAge = findViewById(R.id.etManageTeacherAge);
        etManageTeacherNic = findViewById(R.id.etManageTeacherNic);
        etManageTeacherDes = findViewById(R.id.etManageTeacherDes);
        etTeacherPassword = findViewById(R.id.etTeacherPassword);
        etTeacherConfirmPassword = findViewById(R.id.etTeacherConfirmPassword);
        btnUpdateTeacher = findViewById(R.id.btnUpdateTeacher);
        btnDeleteTeacher = findViewById(R.id.btnDeleteTeacher);

        nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, teacherNameList);
        actvTeacherNames.setAdapter(nameAdapter);
        actvTeacherNames.setOnClickListener(v -> actvTeacherNames.showDropDown());

        actvTeacherNames.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = parent.getItemAtPosition(position).toString();
            fetchTeacherDetails(selectedName);
        });

        fetchTeacherNames();

        btnUpdateTeacher.setOnClickListener(v -> updateTeacher());
        btnDeleteTeacher.setOnClickListener(v -> deleteTeacher());
    }

    private void fetchTeacherNames() {
        db.collection("Teacher")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    teacherNameList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("teacherName");
                        if (name != null) {
                            teacherNameList.add(name);
                        }
                    }
                    nameAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load teacher names", Toast.LENGTH_SHORT).show());
    }

    private void fetchTeacherDetails(String name) {
        db.collection("Teacher")
                .whereEqualTo("teacherName", name)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        DocumentSnapshot doc = snapshot.getDocuments().get(0);
                        selectedDocId = doc.getId();

                        etManageTeacherAge.setText(doc.getString("teacherAge"));
                        etManageTeacherNic.setText(doc.getString("teacherNIC"));
                        etManageTeacherDes.setText(doc.getString("teacherDescription"));
                        etTeacherPassword.setText(doc.getString("teacherPassword"));
                        etTeacherConfirmPassword.setText(doc.getString("teacherPassword"));
                    } else {
                        Toast.makeText(this, "Teacher not found", Toast.LENGTH_SHORT).show();
                        selectedDocId = null;
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error fetching teacher details", Toast.LENGTH_SHORT).show());
    }

    private void updateTeacher() {
        String name = actvTeacherNames.getText().toString().trim();
        String age = etManageTeacherAge.getText().toString().trim();
        String nic = etManageTeacherNic.getText().toString().trim();
        String description = etManageTeacherDes.getText().toString().trim();
        String password = etTeacherPassword.getText().toString().trim();
        String confirmPassword = etTeacherConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || age.isEmpty() || nic.isEmpty() || description.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDocId == null) {
            Toast.makeText(this, "Please select a teacher to update", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("teacherName", name);
        updatedData.put("teacherAge", age);
        updatedData.put("teacherNIC", nic);
        updatedData.put("teacherDescription", description);
        updatedData.put("teacherPassword", password);

        db.collection("Teacher").document(selectedDocId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Teacher updated successfully", Toast.LENGTH_SHORT).show();
                            clearFields();;
                        })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }

    private void deleteTeacher() {
        if (selectedDocId == null) {
            Toast.makeText(this, "Please select a teacher to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Teacher").document(selectedDocId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Teacher deleted successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                    fetchTeacherNames();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to delete teacher", Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        actvTeacherNames.setText("");
        etManageTeacherAge.setText("");
        etManageTeacherNic.setText("");
        etManageTeacherDes.setText("");
        etTeacherPassword.setText("");
        etTeacherConfirmPassword.setText("");
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

    public void btnAdminTeacherManageBack(View view) {
        startActivity(new Intent(AdminManageTeacher.this, AdminTeacherDashboard.class));
    }
}
