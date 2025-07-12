package com.example.sciencemore;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class StudentUploadAssignment extends AppCompatActivity {

    private Button btnChooseFile;
    private Button btnUploadFile;


    private Uri filePath;
    private FirebaseFirestore db;



    private FirebaseStorage storage;

    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> pickFileLauncher;

    private TextView txt;

    private static final String TAG = "PDFDownloadFirebase";
    private String subjectName;
    private String fileMetaData;
    private String assignmentName;
    private Button btnDownloadPdfFromUrl;
    private static final String ACTUAL_DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/madsciencemore.firebasestorage.app/o/pdfs%2Fed4fd8cc-6f93-4dc9-8ba8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_upload_assignment);

        btnChooseFile = findViewById(R.id.btnUpload); // Reusing ID, but text changed
        btnUploadFile = findViewById(R.id.btnSubmit);   // Reusing ID, but text changed
        txt = findViewById(R.id.description);
        btnDownloadPdfFromUrl = findViewById(R.id.btnDownloadIns);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subjectName");
        subjectName = "Maths grad 9";
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

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePdf();
            }
        });

        btnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPdf();
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




}