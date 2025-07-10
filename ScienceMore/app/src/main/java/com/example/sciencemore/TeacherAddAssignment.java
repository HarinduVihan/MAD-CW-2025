package com.example.sciencemore;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView; // Make sure to import TextView if you are using it
import android.widget.Toast; // Import Toast for showing messages

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar; // Use java.util.Calendar, not android.icu.util.Calendar
import java.util.Locale; // Import Locale for SimpleDateFormat

public class TeacherAddAssignment extends AppCompatActivity {

    private TextInputEditText editAssignmentName;
    private TextInputEditText editDueDate;
    private Button btnAssign;
    private Button btnBack;
    private TextView headingTextView;
    private TextView txtAssignmentNameLabel;
    private TextView txtDueDateLabel;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_add_assignment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        editAssignmentName = findViewById(R.id.editassigmentname);
        editDueDate = findViewById(R.id.editduedate);
        btnAssign = findViewById(R.id.btnAssign);
        btnBack = findViewById(R.id.btnbk);
        headingTextView = findViewById(R.id.heading);
        txtAssignmentNameLabel = findViewById(R.id.txtassigmentName);
        txtDueDateLabel = findViewById(R.id.txtduedate);


        calendar = Calendar.getInstance();

        editDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String assignmentName = editAssignmentName.getText().toString().trim();
                String dueDate = editDueDate.getText().toString().trim();

                if (assignmentName.isEmpty()) {
                    editAssignmentName.setError("Assignment Name is required");
                    return;
                }
                if (dueDate.isEmpty()) {
                    editDueDate.setError("Due Date is required");
                    return;
                }

                String message = "Assignment: '" + assignmentName + "' assigned with Due Date: " + dueDate;
                Toast.makeText(TeacherAddAssignment.this, message, Toast.LENGTH_LONG).show();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        updateDateInView();
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                TeacherAddAssignment.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);

                        updateDateInView();
                    }
                },
                year, month, day);

        // Show the dialog
        datePickerDialog.show();
    }

    private void updateDateInView() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editDueDate.setText(sdf.format(calendar.getTime()));
    }
}