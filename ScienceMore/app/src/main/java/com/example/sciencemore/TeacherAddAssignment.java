package com.example.sciencemore;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView; // Make sure to import TextView if you are using it
import android.widget.Toast; // Import Toast for showing messages

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar; // Use java.util.Calendar, not android.icu.util.Calendar
import java.util.Locale; // Import Locale for SimpleDateFormat
import java.util.UUID;

public class TeacherAddAssignment extends AppCompatActivity {

    private TextInputEditText editAssignmentName;
    private TextInputEditText editDueDate;
    private Button btnAssign;
    private Button btnChoose;
    private Button btnBack;
    private TextView headingTextView;
    private TextView txtAssignmentNameLabel;
    private TextView txtDueDateLabel;

    private Calendar calendar;
    private Uri filePath;



    private FirebaseStorage storage;

    private StorageReference storageReference;
    private ActivityResultLauncher<Intent> pickFileLauncher;

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
        btnChoose = findViewById(R.id.btnChooseFile);


        //getting an instance of firebase storage.
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        pickFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                            filePath = result.getData().getData();
                            Toast.makeText(TeacherAddAssignment.this, "PDF Selected: " + getFileName(filePath), Toast.LENGTH_SHORT).show();
                            // You might change the ImageView to a "PDF icon" here
                            //imageView.setImageResource(R.drawable.ic_pdf_placeholder); // Assume you have a PDF icon drawable
                        }
                    }
                });


        calendar = Calendar.getInstance();

        editDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //

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
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) { // Check if DISPLAY_NAME column exists
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void choosePdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf"); // Filter for PDF files
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickFileLauncher.launch(Intent.createChooser(intent, "Select PDF"));
    }
    private void uploadPdf() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading PDF...");
            progressDialog.show();

            // Create a unique file reference inside the "pdfs" folder
            String uniqueFileName = UUID.randomUUID().toString() + ".pdf";
            StorageReference ref = storageReference.child("pdfs/" + uniqueFileName);

            // 👇 Define custom metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/pdf")
                    .setCustomMetadata("studentId", "s12345") // replace with dynamic value if needed
                    .setCustomMetadata("assignmentId", "a7890") // optional additional metadata
                    .build();

            // Upload file with metadata
            ref.putFile(filePath, metadata)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(TeacherAddAssignment.this, "PDF Uploaded Successfully! 🎉", Toast.LENGTH_SHORT).show();

                            // Get and show download URL

                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(TeacherAddAssignment.this, "PDF Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        } else {
            Toast.makeText(this, "No PDF selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDateInView() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editDueDate.setText(sdf.format(calendar.getTime()));
    }
    public void chooseFile(View v){
        choosePdf();
    }
    public void assignmentUpload(View v){
        Toast.makeText(this, "upload clicked", Toast.LENGTH_SHORT).show();
        uploadPdf();
    }
}