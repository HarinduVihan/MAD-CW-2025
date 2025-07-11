package com.example.sciencemore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class StudentUploadAssignment extends AppCompatActivity {
    private ImageView imageView;
    private Button btnChooseFile;
    private Button btnUploadFile;
    private Button btnDownloadFile;

    private Uri filePath;

    String filedburl;

    private FirebaseStorage storage;

    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> pickFileLauncher;

    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_upload_assignment);

        btnChooseFile = findViewById(R.id.btnUpload); // Reusing ID, but text changed
        btnUploadFile = findViewById(R.id.btnSubmit);   // Reusing ID, but text changed
        txt = findViewById(R.id.description);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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

            // Store PDFs in a "pdfs" folder with a unique name
            StorageReference ref = storageReference.child("pdfs/" + UUID.randomUUID().toString() + ".pdf");

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(StudentUploadAssignment.this, "PDF Uploaded Successfully! 🎉", Toast.LENGTH_SHORT).show();

                            // Get the download URL
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    Toast.makeText(StudentUploadAssignment.this, "Download URL: " + downloadUrl, Toast.LENGTH_LONG).show();
                                    System.out.println("PDF Download URL: " + downloadUrl);
                                    filedburl = downloadUrl;
                                    txt.setText(downloadUrl);
                                    // You would typically save this URL to Firestore/Realtime Database
                                    // to easily retrieve the PDF later.
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@androidx.annotation.NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(StudentUploadAssignment.this, "PDF Upload Failed: " + e.getMessage() + " 😔", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@androidx.annotation.NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            Toast.makeText(this, "No PDF selected", Toast.LENGTH_SHORT).show();
        }
    }
}