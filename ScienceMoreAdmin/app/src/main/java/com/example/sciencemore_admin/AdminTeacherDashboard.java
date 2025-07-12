package com.example.sciencemore_admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminTeacherDashboard extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_teacher_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();
    }
    public void onClickBack(View v){
        startActivity(new Intent(AdminTeacherDashboard.this, AdminDashboard.class));
    }

    public void onClickAddTeacher(View v){
        startActivity(new Intent(AdminTeacherDashboard.this, AdminAddTeacher.class));
    }
    public void onClickManageTeacher(View v){
        startActivity(new Intent(AdminTeacherDashboard.this, AdminManageTeacher.class));
    }
    public void onClickAssignTeacher(View v){
        startActivity(new Intent(AdminTeacherDashboard.this, AssignTeacher.class));
    }
    public void onClickViewTeacher(View v){
        startActivity(new Intent(AdminTeacherDashboard.this, AdminViewTeachers.class));
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    startActivity(new Intent(AdminTeacherDashboard.this, AdminDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_teacher) {
                    startActivity(new Intent(AdminTeacherDashboard.this, AdminTeacherDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_students) {
                    startActivity(new Intent(AdminTeacherDashboard.this, AdminStudentDashboard.class));
                    return true;
                }
                return false;
            }
        });
    }

}