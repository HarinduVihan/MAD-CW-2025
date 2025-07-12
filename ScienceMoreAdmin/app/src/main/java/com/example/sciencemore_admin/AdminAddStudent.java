package com.example.sciencemore_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminAddStudent extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private BottomNavigationView bottomNavigationView;
    private TextInputEditText etStudentName, etStudentAge, etStudentPassword, etConfirmStudentPassword;
    private Button btnAddStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_add_student);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addStudentLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bottom Nav
        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();

        // Initialize UI elements
        etStudentName = findViewById(R.id.etStudentName);
        etStudentAge = findViewById(R.id.etStudentAge);
        etStudentPassword = findViewById(R.id.etStudentPassword);
        etConfirmStudentPassword = findViewById(R.id.etConfirmStudentPassword);
        btnAddStudent = findViewById(R.id.btnAddStudent);

        // Button click to add student
        btnAddStudent.setOnClickListener(v -> addStudentToFirestore());
    }

    private void addStudentToFirestore() {
        String name = etStudentName.getText().toString().trim();
        String age = etStudentAge.getText().toString().trim();
        String password = etStudentPassword.getText().toString().trim();
        String confirmPassword = etConfirmStudentPassword.getText().toString().trim();

        if (name.isEmpty() || age.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare student data (without grade)
        Map<String, Object> student = new HashMap<>();
        student.put("studentName", name);
        student.put("studentAge", age);
        student.put("studentPassword", password);

        // Save to Firestore under "Student" collection
        db.collection("Student")
                .add(student)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AdminAddStudent.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                    etStudentName.setText("");
                    etStudentAge.setText("");
                    etStudentPassword.setText("");
                    etConfirmStudentPassword.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminAddStudent.this, "Failed to add student", Toast.LENGTH_SHORT).show();
                });
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_nav_home) {
                startActivity(new Intent(AdminAddStudent.this, AdminDashboard.class));
                return true;
            } else if (itemId == R.id.bottom_nav_teacher) {
                startActivity(new Intent(AdminAddStudent.this, AdminTeacherDashboard.class));
                return true;
            } else if (itemId == R.id.bottom_nav_students) {
                startActivity(new Intent(AdminAddStudent.this, AdminStudentDashboard.class));
                return true;
            }
            return false;
        });
    }

    public void backBtnStudentAdd(View view) {
        startActivity(new Intent(AdminAddStudent.this, AdminStudentDashboard.class));
    }
}
