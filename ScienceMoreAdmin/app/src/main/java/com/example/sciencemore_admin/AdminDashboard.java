package com.example.sciencemore_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminDashboard extends AppCompatActivity {

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
}
