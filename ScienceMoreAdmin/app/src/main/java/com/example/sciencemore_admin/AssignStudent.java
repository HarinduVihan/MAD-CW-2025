package com.example.sciencemore_admin;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AssignStudent extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

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

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId(); // Get the ID of the selected menu item

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
}