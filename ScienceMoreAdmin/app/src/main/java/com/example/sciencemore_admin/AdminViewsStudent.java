package com.example.sciencemore_admin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sciencemore_admin.databinding.ActivityAdminViewsStudentBinding;
import com.example.sciencemore_admin.databinding.ActivityMainBinding;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminViewsStudent extends AppCompatActivity {
    private static final String TAG = "TeacherNamesTest";
    private FirebaseFirestore db;
    List<String> teacherNames = new ArrayList<>();



    private ActivityAdminViewsStudentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminViewsStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_admin_views_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();
        fetchAndLogTeacherNames();

    }
    private void addNameToScrollView(String name) {
        TextView textView = new TextView(this);
        LinearLayout namesContainer = new LinearLayout(this);
        namesContainer = findViewById(R.id.namesContainer);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 8, 0, 8);
        textView.setLayoutParams(layoutParams);

        textView.setText(name);
        textView.setTextSize(24);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setGravity(Gravity.LEFT);
        textView.setTypeface(null, Typeface.NORMAL);
        textView.setPadding(16, 8, 16, 8);

        binding.namesContainer.addView(textView);
    }
    private void fetchAndLogTeacherNames() {
        Log.d(TAG, "Attempting to fetch student names..."); //all these log codes are used during development stage to make debugging easy since android studio doesn't provide a terminal.

        db.collection("Student")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String name = document.getString("studentName");
                            if (name != null) {
                                teacherNames.add(name);
                                Log.d(TAG, "Found student: " + name);
                                addNameToScrollView(name);// Log each name as it's found
                            } else {
                                Log.w(TAG, "Document " + document.getId() + " does not have 'studentName'");
                            }
                        }

                        Log.d(TAG, "All Teacher Names Fetched: " + teacherNames);



                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void backBtnViewStudent(View v){
        startActivity(new Intent(this, AdminStudentDashboard.class));
    }

}