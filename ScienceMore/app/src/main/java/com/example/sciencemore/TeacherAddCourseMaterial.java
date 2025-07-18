package com.example.sciencemore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeacherAddCourseMaterial extends AppCompatActivity {
    private FirebaseStorage storage;

    private StorageReference storageReference;
    private ActivityResultLauncher<Intent> pickFileLauncher;
    private FirebaseFirestore db;
    private EditText descriptionTXT;
    private String description;
    private TextView subjectTXT;
    private String uniqueMetadata;
    private Uri filePath;
    private String subjectName;
    private String teacherName;//should come from an intent
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_add_course_material);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();

        descriptionTXT = findViewById(R.id.descriptionCM);
        subjectTXT = findViewById(R.id.subjectTXT);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subjectName");
        teacherName = intent.getStringExtra("teacherName");
        subjectTXT.setText(subjectName);

        db = FirebaseFirestore.getInstance();

        pickFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                            filePath = result.getData().getData();
                            Toast.makeText(TeacherAddCourseMaterial.this, "PDF Selected: " + getFileName(filePath), Toast.LENGTH_SHORT).show();

                        }
                    }
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

    private void choosePdf() {    //this method will call an intent that actually can navigate and choose a file. user can any file explorer available in his mobile phone.
        Intent intent = new Intent();
        intent.setType("application/pdf"); // Filter for PDF files
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickFileLauncher.launch(Intent.createChooser(intent, "Select PDF"));
    }


    private void uploadPdf(String subject, String description) {
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
                    .setCustomMetadata("CMKey", createUniqueMetada(subject, description)) // replace with dynamic value if needed

                    .build();

            // Upload file with metadata
            ref.putFile(filePath, metadata)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(TeacherAddCourseMaterial.this, "PDF Uploaded Successfully! 🎉", Toast.LENGTH_SHORT).show();

                            // Get and show download URL

                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(TeacherAddCourseMaterial.this, "PDF Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        } else {
            Toast.makeText(this, "No PDF selected", Toast.LENGTH_SHORT).show();
        }
    }
    public String createUniqueMetada(String subject, String assignmentName){
        uniqueMetadata = subject + assignmentName;
        return uniqueMetadata;
    }

    public void onPressChooseFile(View v){
        choosePdf();
    }
    public void onPressUpload(View v){
        description = descriptionTXT.getText().toString();
        saveAssignment();

    }

    private void saveAssignment(){
        description = descriptionTXT.getText().toString();


        if (description == null || subjectName == null) {
            Toast.makeText(TeacherAddCourseMaterial.this, "Please Enter Course material description", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference collectionReference = db.collection("SubjectMaterial");
        Query query = collectionReference.whereEqualTo("materialDescription", description).whereEqualTo("subject", subjectName);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    boolean isAvailabele = true;
                    Toast.makeText(this, "Already Assigned", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> assignmentData = new HashMap<>();
                    assignmentData.put("materialDescription", description);

                    assignmentData.put("subject", subjectName);
                    assignmentData.put("fileMetaData", createUniqueMetada(subjectName, description));

                    db.collection("SubjectMaterial")
                            .add(assignmentData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(TeacherAddCourseMaterial.this, "Assigned successfully!", Toast.LENGTH_SHORT).show();
                                uploadPdf(subjectName, description);

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(TeacherAddCourseMaterial.this, "Error in assigning: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(TeacherAddCourseMaterial.this, TeacherDashboard.class);
                    intent.putExtra("teacherName", teacherName);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.bottom_nav_result) {
                    Intent intent = new Intent(TeacherAddCourseMaterial.this , TeacherMarkAssignment.class);
                    intent.putExtra("teacherName", teacherName);
                    intent.putExtra("subjectName" , subjectName);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.bottom_nav_assignment) {
                    Intent intent = new Intent(TeacherAddCourseMaterial.this , TeacherAddAssignment.class);
                    intent.putExtra("teacherName", teacherName);
                    intent.putExtra("subjectName" , subjectName);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}