package com.example.sciencemore;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MarkAttendance extends AppCompatActivity {

    private TextView studentIDTXT;
    String scannedData;
    private static final String SCANNED_DATA_KEY = "scannedData";

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mark_attendance);


        studentIDTXT = findViewById(R.id.studentId);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        studentIDTXT.setText(scannedData);
    }

    private void NavigationBar() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_nav_home) {
                    startActivity(new Intent(MarkAttendance.this, AdminDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_result) {
                    startActivity(new Intent(MarkAttendance.this, AdminTeacherDashboard.class));
                    return true;
                } else if (itemId == R.id.bottom_nav_assignment) {
                    startActivity(new Intent(MarkAttendance.this, AdminStudentDashboard.class));
                    return true;
                }
                return false;
            }
        });
    }

}