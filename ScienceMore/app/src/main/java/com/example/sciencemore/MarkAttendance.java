package com.example.sciencemore;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkAttendance extends AppCompatActivity {

    private TextView studentNameTXT;
    String scannedData;
    private static final String SCANNED_DATA_KEY = "scannedData";
    private static final String TAG = "FirestoreSubjectLoader";
    private FirebaseFirestore db;
    private Spinner subjectsSpinner;
    private BottomNavigationView bottomNavigationView;
    String teacherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mark_attendance);
        db = FirebaseFirestore.getInstance();

        // Initialize your Spinner from the layout
        subjectsSpinner = findViewById(R.id.subSpinner);


        studentNameTXT = findViewById(R.id.studentId);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        teacherName = intent.getStringExtra("teacherName");

        bottomNavigationView = findViewById(R.id.bottomnav);
        NavigationBar();

    }
    public void scanQR(View v){
        IntentIntegrator integrator = new IntentIntegrator(MarkAttendance.this);
        integrator.setPrompt("Scan a QR Code"); // Message to display
        integrator.setOrientationLocked(true); // Lock orientation during scan
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE); // Only scan QR codes
        integrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Handle cancelled scan
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Handle successful scan
                scannedData = result.getContents();
                //String formatName = result.getFormatName();
                //studentIDTXT.setText("Scanned: " + scannedData + "\nFormat: " + formatName);
                Toast.makeText(this, "Scanned: " + scannedData, Toast.LENGTH_LONG).show();
                // You can further process the scannedData here (e.g., open a URL, display information)
                setData();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
    private void setData(){
        studentNameTXT.setText(scannedData);
        loadStudentSubjectsIntoSpinner(scannedData, subjectsSpinner);
    }
    private void loadStudentSubjectsIntoSpinner(String studentName, Spinner spinner) {
        // Create a list to hold the subjects
        List<String> subjectsList = new ArrayList<>();

        // Query the "studentSubject" collection in Firestore
        db.collection("studentSubject")
                .whereEqualTo("studentName", studentName) // Filter by studentName
                .get() // Get the documents
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Iterate through the documents returned by the query
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log the document data for debugging
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                // Get the "subject" field from the document
                                String subject = document.getString("subject");
                                if (subject != null) {
                                    subjectsList.add(subject); // Add the subject to the list
                                }
                            }

                            // Create an ArrayAdapter using the subjects list and a default spinner layout
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    MarkAttendance.this,
                                    android.R.layout.simple_spinner_item, // Default spinner item layout
                                    subjectsList
                            );

                            // Specify the layout to use when the list of choices appears
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Apply the adapter to the spinner
                            spinner.setAdapter(adapter);

                            // Show a success message
                            Toast.makeText(MarkAttendance.this,
                                    "Subjects loaded for " + studentName,
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // Log the error if the task was not successful
                            Log.w(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(MarkAttendance.this,
                                    "Error loading subjects: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log and show an error message if the operation fails
                        Log.e(TAG, "Firestore query failed: " + e.getMessage());
                        Toast.makeText(MarkAttendance.this,
                                "Firestore query failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void markAttendance(){
        LocalDate today = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }
        String date = today.toString();
        String subject = subjectsSpinner.getSelectedItem().toString();
        String name = studentNameTXT.getText().toString();


        if (subject == null || name == null) {
            Toast.makeText(MarkAttendance.this, "Please Enter Course material description", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference collectionReference = db.collection("Attendance");
        Query query = collectionReference.whereEqualTo("studentName", name).whereEqualTo("subject", subject).whereEqualTo("date", date);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    boolean isAvailabele = true;
                    Toast.makeText(this, "Already Marked", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> assignmentData = new HashMap<>();
                    assignmentData.put("studentName", name);

                    assignmentData.put("subject", subject);
                    assignmentData.put("date", date);

                    db.collection("Attendance")
                            .add(assignmentData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(MarkAttendance.this, "Assigned successfully!", Toast.LENGTH_SHORT).show();


                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MarkAttendance.this, "Error in assigning: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        });





    }
    public void mark(View v){
        markAttendance();
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    Intent intent = new Intent(MarkAttendance.this, TeacherDashboard.class);
                    intent.putExtra("teacherName", teacherName);
                    startActivity(intent);
                    return true;
                }else if (itemId == R.id.bottom_nav_qr) {
                    Intent intent = new Intent(MarkAttendance.this, MarkAttendance.class);
                    intent.putExtra("teacherName", teacherName);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}