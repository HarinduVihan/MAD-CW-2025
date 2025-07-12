package com.example.sciencemore;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StudentUploadAssignment extends AppCompatActivity {

    //below are the variable and object declarations
    private Button btnChooseFile;
    private Button btnUploadFile;
    private Uri filePath;
    private FirebaseFirestore db;

    private FirebaseStorage storage;

    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> pickFileLauncher;

    private TextView txt;
    private TextView subjectTXT;

    private static final String TAG = "PDFDownloadFirebase";
    private String subjectName;
    private String studentName;
    private String fileMetaData;
    private String metaDataforUploading;
    private String assignmentName;
    private Button btnDownloadPdfFromUrl;

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_upload_assignment);

        btnChooseFile = findViewById(R.id.btnUpload); // Reusing ID, but text changed
        btnUploadFile = findViewById(R.id.btnSubmit);   // Reusing ID, but text changed
        txt = findViewById(R.id.description);
        btnDownloadPdfFromUrl = findViewById(R.id.btnDownloadIns);
        subjectTXT = findViewById(R.id.subjectTextView);



        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subjectName");
        studentName = intent.getStringExtra("studentName");
        subjectTXT.setText(subjectName);
        Toast.makeText(this, "Student name :" + studentName, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Subject name :" + subjectName, Toast.LENGTH_SHORT).show();

        checkForAnyAssignments();


        pickFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                            filePath = result.getData().getData();
                            Toast.makeText(StudentUploadAssignment.this, "PDF Selected: " + getFileName(filePath), Toast.LENGTH_SHORT).show();
                            // You might change the ImageView to a "PDF icon" here
                            //imageView.setImageResource(R.drawable.ic_pdf_placeholder); // Assume you have a PDF icon drawable
                        }
                    }
                });

        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePdf();
            }
        });

        btnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAssignment();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

    private void uploadPdf(String subjectname, String assignmentname, String studentname) {
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
                    .setCustomMetadata("studentKey", createUniqueMetadata(subjectname, studentname, assignmentname)) // replace with dynamic value if needed

                    .build();

            // Upload file with metadata
            ref.putFile(filePath, metadata)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(StudentUploadAssignment.this, "PDF Uploaded Successfully! 🎉", Toast.LENGTH_SHORT).show();

                            // Get and show download URL

                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(StudentUploadAssignment.this, "PDF Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        } else {
            Toast.makeText(this, "No PDF selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void findAndDownloadByMetadata(String fileMetaData) {
        if(fileMetaData == null){
            Toast.makeText(this,  "No assignments due", Toast.LENGTH_SHORT).show();
        }
        StorageReference listRef = storageReference.child("pdfs/");

        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.getMetadata().addOnSuccessListener(storageMetadata -> {
                            String key = storageMetadata.getCustomMetadata("key");

                            if (key != null && key.equals(fileMetaData)) {
                                String fileName = storageMetadata.getName();
                                item.getDownloadUrl().addOnSuccessListener(uri -> {
                                    // Download file
                                    downloadFile(uri.toString(), fileName);
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to list files: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void downloadFile(String url, String filename) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Downloading " + filename);
        request.setDescription("Downloading PDF...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
    public void downloadInstructions(View v){
        findAndDownloadByMetadata(fileMetaData);
    }
    public void checkForAnyAssignments(){
        db.collection("Assignment")
                .whereEqualTo("subject", subjectName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Documents with the specified subject name exist
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                // Get the fileMetaData field for each matching document
                                fileMetaData = document.getString("fileMetaData");
                                if (fileMetaData != null) {
                                    // Here you can use the fileMetaData
                                    // For example, log it or display it
                                    Toast.makeText(this, "File Meta Data: " + fileMetaData, Toast.LENGTH_SHORT).show();
                                    // If you only need the first one found, you can break here
                                    // break;
                                } else {
                                    Toast.makeText(this, "File Meta Data not found for this document.", Toast.LENGTH_SHORT).show();
                                }
                                assignmentName = document.getString("assignmentName");
                                Toast.makeText(this, assignmentName, Toast.LENGTH_SHORT).show();
                                txt.setText(assignmentName);

                            }
                        } else {
                            // No documents found with the specified subject name
                            Toast.makeText(this, "No assignments found for subject: " + subjectName, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error getting documents
                        Toast.makeText(this, "Error getting documents: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public String createUniqueMetadata(String subject, String studentname, String assignmentname){
        return metaDataforUploading = subject + assignmentname + studentname;
    }


    private void saveAssignment(){


        //String message = "Assignment: '" + assignmentName + "' assigned with Due Date: " + dueDate;
        //Toast.makeText(TeacherAddAssignment.this, message, Toast.LENGTH_LONG).show();

        Toast.makeText(StudentUploadAssignment.this, "assignmentName" + assignmentName, Toast.LENGTH_SHORT).show();
        Toast.makeText(StudentUploadAssignment.this, "subjectName" + subjectName, Toast.LENGTH_SHORT).show();
        Toast.makeText(StudentUploadAssignment.this, "studentName" + studentName, Toast.LENGTH_SHORT).show();
        if (assignmentName == null || subjectName == null || studentName == null) {
            Toast.makeText(StudentUploadAssignment.this, "Incomplete data", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference collectionReference = db.collection("AssignmentSubmission");
        Query query = collectionReference.whereEqualTo("assignmentName", assignmentName).whereEqualTo("subject", subjectName).whereEqualTo("studentName", studentName);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    boolean isAvailabele = true;
                    Toast.makeText(this, "Already uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> assignmentData = new HashMap<>();
                    assignmentData.put("assignmentName", assignmentName);
                    assignmentData.put("studentName", studentName);
                    assignmentData.put("subject", subjectName);
                    assignmentData.put("fileMetaData", createUniqueMetadata(subjectName, studentName, assignmentName));

                    db.collection("AssignmentSubmission")
                            .add(assignmentData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(StudentUploadAssignment.this, "Assigned successfully!", Toast.LENGTH_SHORT).show();
                                uploadPdf(subjectName, assignmentName, studentName);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(StudentUploadAssignment.this, "Error in assigning: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    Intent intent = new Intent(StudentUploadAssignment.this, StudentDashboard.class);
                    intent.putExtra("studentName", studentName);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.bottom_nav_result) {
                    Intent intent = new Intent(StudentUploadAssignment.this, StudentAssignmentResults.class);
                    intent.putExtra("studentName",studentName);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

}