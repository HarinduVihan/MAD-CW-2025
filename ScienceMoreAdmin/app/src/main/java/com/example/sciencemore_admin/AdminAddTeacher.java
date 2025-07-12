package com.example.sciencemore_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminAddTeacher extends AppCompatActivity {

    // Firebase Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // UI components
    private BottomNavigationView bottomNavigationView;
    private TextInputEditText etTeacherName, etTeacherAge, etTeacherNic, etTeacherDes,
            etTeacherPassword, etTeacherConfirmPassword;
    private Button btnAddTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_add_teacher);

        // Handle window insets for full screen layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addTeacherLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();

        // Initialize input fields
        etTeacherName = findViewById(R.id.etTeacherName);
        etTeacherAge = findViewById(R.id.etTeacherAge);
        etTeacherNic = findViewById(R.id.etTeacherNic);
        etTeacherDes = findViewById(R.id.etTeacherDes);
        etTeacherPassword = findViewById(R.id.etTeacherPassword);
        etTeacherConfirmPassword = findViewById(R.id.etTeacherConfirmPassword);
        btnAddTeacher = findViewById(R.id.btnAddTeacher);

        // Handle add button click
        btnAddTeacher.setOnClickListener(v -> addTeacherToFirestore());
    }

    // Method to add teacher to Firestore
    private void addTeacherToFirestore() {
        // Get input values
        String name = etTeacherName.getText().toString().trim();
        String age = etTeacherAge.getText().toString().trim();
        String nic = etTeacherNic.getText().toString().trim();
        String description = etTeacherDes.getText().toString().trim();
        String password = etTeacherPassword.getText().toString().trim();
        String confirmPassword = etTeacherConfirmPassword.getText().toString().trim();

        // Input validation
        if (name.isEmpty() || age.isEmpty() || nic.isEmpty() || description.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create teacher object
        Map<String, Object> teacher = new HashMap<>();
        teacher.put("teacherName", name);
        teacher.put("teacherAge", age);
        teacher.put("teacherNIC", nic);
        teacher.put("teacherDescription", description);
        teacher.put("teacherPassword", password);

        // Save to Firestore
        db.collection("Teacher")
                .add(teacher)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Teacher added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add teacher", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to clear input fields
    private void clearFields() {
        etTeacherName.setText("");
        etTeacherAge.setText("");
        etTeacherNic.setText("");
        etTeacherDes.setText("");
        etTeacherPassword.setText("");
        etTeacherConfirmPassword.setText("");
    }

    // Bottom navigation listener
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

    // Back button method (linked in XML)
    public void backBtnTeacherAdd(View view) {
        startActivity(new Intent(AdminAddTeacher.this, AdminTeacherDashboard.class));
    }
}
