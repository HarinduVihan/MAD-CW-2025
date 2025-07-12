package com.example.sciencemore_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class AdminManageStudent extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private TextInputEditText etManageStudentName, etManageStudentAge, etManageStudentPassword, etManageConfirmStudentPassword;

    private Spinner spinnerGrade;
    private Button btnUpdateStudent, btnDeleteStudent;

    private String selectedGrade ="";


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

        etManageStudentName = findViewById(R.id.etManageStudentName);
        etManageStudentAge = findViewById(R.id.etManageStudentAge);
        etManageStudentPassword =findViewById(R.id.etManageStudentPassword);
        etManageConfirmStudentPassword = findViewById(R.id.etManageConfirmStudentPassword);
        spinnerGrade = findViewById(R.id.spinnerManageGrade);
        btnUpdateStudent = findViewById(R.id.btnUpdateStudent);
        btnDeleteStudent = findViewById(R.id.btnDeleteStudent);

        // Setup grade spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.grade_list,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrade.setAdapter(adapter);

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGrade = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGrade = "";
            }
        });

        // Button click listener
        btnUpdateStudent.setOnClickListener(v -> {
            String name = etManageStudentName.getText().toString().trim();
            String age = etManageStudentAge.getText().toString().trim();
            String password = etManageStudentPassword.getText().toString().trim();
            String confirmPassword = etManageConfirmStudentPassword.getText().toString().trim();

            // Basic validation
            if (name.isEmpty() || age.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || selectedGrade.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Success - show message (replace this with database save if needed)
            Toast.makeText(this, "Student Added:\n" +
                    "Name: " + name + "\nAge: " + age + "\nGrade: " + selectedGrade, Toast.LENGTH_LONG).show();
        });
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    startActivity(new Intent(AdminManageStudent.this, AdminDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_teacher) {
                    startActivity(new Intent(AdminManageStudent.this, AdminTeacherDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_students) {
                    startActivity(new Intent(AdminManageStudent.this, AdminStudentDashboard.class));
                    return true;
                }
                return false;
            }
        });
    }
}