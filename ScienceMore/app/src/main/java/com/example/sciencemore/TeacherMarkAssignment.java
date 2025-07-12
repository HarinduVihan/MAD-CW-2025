package com.example.sciencemore;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_mark_assignment);
        cardContainer = findViewById(R.id.cardContainer);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        String specificSubject = "Maths grade 8";
        fetchAssignmentSubmissions(specificSubject);


//        List<Assigments> assigmentsList = new ArrayList<>();
//        assigmentsList.add(new Assigments("http", "Dinidu Dididy"));
//        populateCardViews(assigmentsList);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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

    private void saveAssignmentResult(String assignmentName, String studentName, String result) {
        Map<String, Object> assignmentResult = new HashMap<>();
        assignmentResult.put("AssignmentId", assignmentName);
        assignmentResult.put("studentName", studentName);
        assignmentResult.put("Result", result);

        db.collection("AssignmentResult") // Your target collection
                .add(assignmentResult) // Use .add() to create a new document with an auto-generated ID
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(TeacherMarkAssignment.this, "Result saved successfully for " + studentName, Toast.LENGTH_SHORT).show();
                    // Optionally, you might want to refresh the list or disable the mark button
                    // after saving to prevent re-marking.
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

}