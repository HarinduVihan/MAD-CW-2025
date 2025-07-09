package com.example.sciencemore_admin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sciencemore_admin.databinding.ActivityAdminViewTeachersBinding;
import com.example.sciencemore_admin.databinding.ActivityAdminViewsStudentBinding;

public class AdminViewTeachers extends AppCompatActivity {

    private ActivityAdminViewTeachersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminViewTeachersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_admin_view_teachers);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addNameToScrollView("teacher");
        addNameToScrollView("chuuti");
        addNameToScrollView("teacher");
        addNameToScrollView("chuuti");
        addNameToScrollView("teacher");
        addNameToScrollView("chuuti");
        addNameToScrollView("teacher");
        addNameToScrollView("chuuti");
        addNameToScrollView("teacher");
        addNameToScrollView("chuuti");
        addNameToScrollView("teacher");
        addNameToScrollView("chuuti");
        addNameToScrollView("teacher");
        addNameToScrollView("chuuti");
        addNameToScrollView("teacher");
        addNameToScrollView("chuuti");
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
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setGravity(Gravity.LEFT);
        textView.setTypeface(null, Typeface.NORMAL);
        textView.setPadding(16, 8, 16, 8);

        binding.namesContainer.addView(textView);
    }
}