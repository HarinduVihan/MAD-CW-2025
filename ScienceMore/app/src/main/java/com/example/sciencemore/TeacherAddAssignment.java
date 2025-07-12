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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar; // Use java.util.Calendar, not android.icu.util.Calendar
import java.util.HashMap;
import java.util.Locale; // Import Locale for SimpleDateFormat
import java.util.Map;
import java.util.UUID;

public class TeacherAddAssignment extends AppCompatActivity {

    //declaration of variables and objects that will be used in this class
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


    private String assignmentName;
    boolean isAvailabele;


    private FirebaseStorage storage;

    private StorageReference storageReference;
    private ActivityResultLauncher<Intent> pickFileLauncher;
    String subjectName;

    private FirebaseFirestore db;
    String uniqueMetadata;

    //below is the method that will always be called whenever this activity class is called
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
        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subjectName");
        subjectName = "Maths grade 8";


        //initializing variables
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

        //getting an instance of firebase firestore
        db = FirebaseFirestore.getInstance();

        pickFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                            filePath = result.getData().getData();
                            Toast.makeText(TeacherAddAssignment.this, "PDF Selected: " + getFileName(filePath), Toast.LENGTH_SHORT).show();

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

    private void choosePdf() {    //this method will call an intent that actually can navigate and choose a file. user can any file explorer available in his mobile phone.
        Intent intent = new Intent();
        intent.setType("application/pdf"); // Filter for PDF files
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickFileLauncher.launch(Intent.createChooser(intent, "Select PDF"));
    }
    private void uploadPdf(String subject, String assignmentName) {
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
                    .setCustomMetadata("key", createUniqueMetada(subjectName, assignmentName)) // replace with dynamic value if needed

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

        saveAssignment();

    }

    private void saveAssignment(){
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

        if (assignmentName == null || dueDate == null) {
            Toast.makeText(TeacherAddAssignment.this, "Please Enter Assignment name and due date", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference collectionReference = db.collection("Assignment");
        Query query = collectionReference.whereEqualTo("assignmentName", assignmentName).whereEqualTo("subject", subjectName);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    isAvailabele = true;
                    Toast.makeText(this, "Already Assigned", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> assignmentData = new HashMap<>();
                    assignmentData.put("assignmentName", assignmentName);
                    assignmentData.put("dueDate", dueDate);
                    assignmentData.put("subject", subjectName);
                    assignmentData.put("fileMetaData", createUniqueMetada(subjectName, assignmentName));

                    db.collection("Assignment")
                            .add(assignmentData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(TeacherAddAssignment.this, "Assigned successfully!", Toast.LENGTH_SHORT).show();

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(TeacherAddAssignment.this, "Error in assigning: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        uploadPdf(subjectName, assignmentName);



    }
    public String createUniqueMetada(String subject, String assignmentName){
        uniqueMetadata = subject + assignmentName;
        return uniqueMetadata;
    }

}
