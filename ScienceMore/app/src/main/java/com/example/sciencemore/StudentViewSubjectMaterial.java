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
import java.util.List;

public class StudentViewSubjectMaterial extends AppCompatActivity {
    private LinearLayout cardContainer;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String subjectName;

    private StorageReference storageReference;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view_subject_material);
        cardContainer = findViewById(R.id.cardContainer);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //List<CardItem> cardDataList = new ArrayList<>();
        //cardDataList.add(new CardItem("Card One", "This is the description for card number one."));
        //cardDataList.add(new CardItem("Card Two", "Here's some content for the second card."));

        //populateCardViews(cardDataList);
        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subjectName");
//        fetchSubjectMaterials("Math grade 8");
        fetchSubjectMaterials(subjectName);

        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_student_view_subject_material);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();
    }
    private void populateCardViews(List<CardItem> dataList) {
        // Clear any existing views
        cardContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < dataList.size(); i++) {  //for each item in data list, a new card will be added to the ui
            CardItem currentItem = dataList.get(i);

            // Inflate the card_item.xml layout
            View cardViewLayout = inflater.inflate(R.layout.card_item, cardContainer, false);


            TextView titleTextView = cardViewLayout.findViewById(R.id.titleTextView);
            TextView descriptionTextView = cardViewLayout.findViewById(R.id.descriptionTextView);
            Button actionButton = cardViewLayout.findViewById(R.id.actionButton);

            // Populate the views with data
            titleTextView.setText("File " + (i + 1));
            descriptionTextView.setText(currentItem.getDescription());
            actionButton.setText("Download");

            // Set a click listener for the button (optional)
            final int cardIndex = i;
            actionButton.setOnClickListener(new View.OnClickListener() {  //below code is just used to test the functioning of button
                @Override
                public void onClick(View v) {

                    findAndDownloadByMetadata(currentItem.getLink());
                }
            });

            // Add the inflated CardView to the container
            cardContainer.addView(cardViewLayout);
        }
    }
    private void fetchSubjectMaterials(String subject) {
        //Toast.makeText(this, "fetch assignment executed", Toast.LENGTH_SHORT).show();
        db.collection("SubjectMaterial")
                .whereEqualTo("subject", subject) // Filter by the specific subject
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CardItem> cardDataList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert each document to your Assigments object
                            String description = document.getString("materialDescription");
                            String fileMetaData = document.getString("fileMetaData"); // This contains the download link

                            // Create an Assigments object and add to the list
                            if (description != null && fileMetaData != null) {
                                cardDataList.add(new CardItem(fileMetaData, description));
                            }
                        }
                        // This will populate the UI with the fetched data
                        populateCardViews(cardDataList);
                    } else {
                        Toast.makeText(StudentViewSubjectMaterial.this, "Error getting documents: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void findAndDownloadByMetadata(String fileMetaData) {
        Toast.makeText(this, "find and download executed", Toast.LENGTH_SHORT).show();
        if(fileMetaData == null){
            Toast.makeText(this,  "No material available", Toast.LENGTH_SHORT).show();
        }
        StorageReference listRef = storageReference.child("pdfs/");

        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.getMetadata().addOnSuccessListener(storageMetadata -> {
                            String key = storageMetadata.getCustomMetadata("CMKey");

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

    public void assignmentPageBtn(View v) {
        // intent passing
        Intent intent = new Intent(StudentViewSubjectMaterial.this , StudentUploadAssignment.class);
        intent.putExtra("subjectName" , subjectName);
        startActivity(intent);
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    startActivity(new Intent(StudentViewSubjectMaterial.this, StudentDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_result) {
                    startActivity(new Intent(StudentViewSubjectMaterial.this, StudentAssignmentResults.class));
                    return true;
                }
                return false;
            }
        });
    }
}