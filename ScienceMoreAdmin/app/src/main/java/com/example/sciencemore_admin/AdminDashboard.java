package com.example.sciencemore_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminDashboard extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboardLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationBar();
    }

    // Called when the "Teachers" card is clicked
    public void onClickAssignTeacher(View view) {
        Intent intent = new Intent(this, AdminTeacherDashboard.class);
        startActivity(intent);
    }

    // Called when the "Students" card is clicked
    public void onClickStudents(View view) {
        Intent intent = new Intent(this, AdminStudentDashboard.class);
        startActivity(intent);
    }

    // Optional placeholders for the rest if you want
    public void onClickAttendance(View view) {
        // Implement your navigation to Attendance screen here
    }

    public void onClickResults(View view) {
        // Implement your navigation to Results screen here
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    startActivity(new Intent(AdminDashboard.this, AdminDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_teacher) {
                    startActivity(new Intent(AdminDashboard.this, AdminTeacherDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_students) {
                    startActivity(new Intent(AdminDashboard.this, AdminStudentDashboard.class));
                    return true;
                }
                return false;
            }
        });
    }
}
