package com.example.sciencemore;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherMarkAssignment extends AppCompatActivity {

    private LinearLayout cardContainer;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private StorageReference storageReference;

    private BottomNavigationView bottomNavigationView;

    String teacherName;
    String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_mark_assignment);
        cardContainer = findViewById(R.id.cardContainer);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        teacherName = intent.getStringExtra("teacherName");
        subjectName = intent.getStringExtra("subjectName");

        
        fetchAssignmentSubmissions(subjectName);


//        List<Assigments> assigmentsList = new ArrayList<>();
//        assigmentsList.add(new Assigments("http", "Dinidu Dididy"));
//        populateCardViews(assigmentsList);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();
    }

    private void populateCardViews(List<Assigments> assigments){
        cardContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i =0; i < assigments.size(); i++){
            Assigments currentItem = assigments.get(i);

            View cardViewLayout = inflater.inflate(R.layout.mark_assignment_card,cardContainer, false);

            LinearLayout cardContentLayout = cardViewLayout.findViewById(R.id.cardContentLayout);

            TextView name = cardContentLayout.findViewById(R.id.txtStudentName);
            Button downloadbtn = cardViewLayout.findViewById(R.id.btnDownloadAnswerSheet);
            Button markButton = cardViewLayout.findViewById(R.id.btnMark);
            EditText markInput = cardViewLayout.findViewById(R.id.txtMarkDisplay);

            name.setText(currentItem.getName());

            final int cardIndex = i;
            downloadbtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Toast.makeText(TeacherMarkAssignment.this, "download link : " + currentItem.getLink(), Toast.LENGTH_SHORT).show();
                    findAndDownloadByMetadata(currentItem.getLink());
                }
            });

            markButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String studentName = currentItem.getName();
                    String markStr = markInput.getText().toString().trim();

                    if (markStr.isEmpty()) {
                        Toast.makeText(TeacherMarkAssignment.this, "Please enter a mark.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String assignmentName = currentItem.getAssignmentName();

                    if (assignmentName == null || assignmentName.isEmpty()) {
                        assignmentName = "assign_id_" + System.currentTimeMillis();
                    }

                    saveAssignmentResult(assignmentName, studentName, markStr);
                }
            });

            cardContainer.addView(cardViewLayout);
        }

    }

    private void deleteAssignemtSubmissions(String assignmentName, String studentName){
        db.collection("AssignmentSubission")
                .whereEqualTo("assignementName", assignmentName)
                .whereEqualTo("studentName", studentName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(TeacherMarkAssignment.this, "No matching submission found to delete.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveAssignmentResult(String assignmentName, String studentName, String result) {
        Map<String, Object> assignmentResult = new HashMap<>();
        assignmentResult.put("AssignmentName", assignmentName);
        assignmentResult.put("studentName", studentName);
        assignmentResult.put("Result", result);

        db.collection("AssignmentResult")
                .add(assignmentResult)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(TeacherMarkAssignment.this, "Result saved successfully for " + studentName, Toast.LENGTH_SHORT).show();
                    getDocumentID(studentName, assignmentName); // this method call will get the document id of assignment submission and then will delete ot using standard delete method
                    fetchAssignmentSubmissions(subjectName);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TeacherMarkAssignment.this, "Error saving result: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void fetchAssignmentSubmissions(String subject) {
        //Toast.makeText(this, "fetch assignment executed", Toast.LENGTH_SHORT).show();
        db.collection("AssignmentSubmission")
                .whereEqualTo("subject", subject) // Filter by the specific subject
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Assigments> assigmentsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert each document to your Assigments object
                            String studentName = document.getString("studentName");
                            String fileMetaData = document.getString("fileMetaData");
                            String assignmentName = document.getString("assignmentName");// This contains the download link

                            // Create an Assigments object and add to the list
                            if (studentName != null && fileMetaData != null) {
                                assigmentsList.add(new Assigments(fileMetaData, studentName,assignmentName));
                            }
                        }
                        // This will populate the UI with the fetched data
                        populateCardViews(assigmentsList);
                    } else {
                        Toast.makeText(TeacherMarkAssignment.this, "Error getting documents: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void findAndDownloadByMetadata(String fileMetaData) {
        Toast.makeText(this, "find and download executed", Toast.LENGTH_SHORT).show();
        if(fileMetaData == null){
            Toast.makeText(this,  "No assignments due", Toast.LENGTH_SHORT).show();
        }
        StorageReference listRef = storageReference.child("pdfs/");

        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.getMetadata().addOnSuccessListener(storageMetadata -> {
                            String key = storageMetadata.getCustomMetadata("studentKey");

                            if (key != null && key.equals(fileMetaData)) {
                                String fileName = storageMetadata.getName();
                                item.getDownloadUrl().addOnSuccessListener(uri -> {
                                    // calls the download method
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
    //below is the method used to download file using download manager.
    private void downloadFile(String url, String filename) {
        Toast.makeText(this, "download file executed ", Toast.LENGTH_SHORT).show();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Downloading " + filename);
        request.setDescription("Downloading PDF...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    Intent intent = new Intent(TeacherMarkAssignment.this, TeacherDashboard.class);
                    intent.putExtra("teacherName", teacherName);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.bottom_nav_result) {
                    Intent intent = new Intent(TeacherMarkAssignment.this , TeacherMarkAssignment.class);
                    intent.putExtra("teacherName", teacherName);
                    intent.putExtra("subjectName" , subjectName);
                    startActivity(intent);
                    return true;
                }else if (itemId == R.id.bottom_nav_assignment) {
                    Intent intent = new Intent(TeacherMarkAssignment.this , TeacherAddAssignment.class);
                    intent.putExtra("teacherName", teacherName);
                    intent.putExtra("subjectName" , subjectName);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
    public void getDocumentID(String studentName, String assignmentName){
        db.collection("AssignmentSubmission")
                .whereEqualTo("studentName", studentName)
                .whereEqualTo("assignmentName", assignmentName)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {

                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            String documentId = document.getId();
                            Toast.makeText(this, "Found Document ID: " + documentId, Toast.LENGTH_LONG).show();
                            deleteAssignmentSubmissionById(documentId);

                        } else {
                            // No document found. according to the data flow of our app this couldn't happen, but this line was kept for any run time error that could happen
                            Toast.makeText(this, "No submission found for " + studentName + " in " + assignmentName, Toast.LENGTH_LONG).show();
                        }
                    } else {

                        Toast.makeText(this, "Error getting document ID: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void deleteAssignmentSubmissionById(String documentId) {
        db.collection("AssignmentSubmission").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TeacherMarkAssignment.this, "Submission deleted.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TeacherMarkAssignment.this, "Error deleting submission: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

}