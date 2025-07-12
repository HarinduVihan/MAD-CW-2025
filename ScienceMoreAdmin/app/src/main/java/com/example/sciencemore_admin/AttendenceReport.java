package com.example.sciencemore_admin;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.barteksc.pdfviewer.PDFView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import com.google.firebase.firestore.FirebaseFirestore; // Import FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AttendenceReport extends AppCompatActivity {

    List<AttendanceEntry> attendanceList;

    private static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODE_PERMISSIONS = 12;

    private File pFile;
    private PDFView pdfView;
    private Button generateReportButton;
    private Uri pdfUri;

    private FirebaseFirestore db; // Declare FirebaseFirestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendence_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.attendance_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Attendance Report");
        }

        pdfView = findViewById(R.id.attendance_pdf_viewer);
        generateReportButton = findViewById(R.id.button_generate_attendance_report);

        attendanceList = new ArrayList<>();

        db = FirebaseFirestore.getInstance(); // Initialize FirebaseFirestore

        // Call populateAttendanceData to fetch data from Firestore
        populateAttendanceData();

        generateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermissionsAndGenerateReport();
            }
        });
    }

    private void requestStoragePermissionsAndGenerateReport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            generateAndDisplayReport();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                generateAndDisplayReport();
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generateAndDisplayReport();
            } else {
                Toast.makeText(this, "Storage permission denied. Cannot generate report to Downloads.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void generateAndDisplayReport() {
        try {
            // Ensure attendanceList is populated before attempting to create the report
            if (attendanceList.isEmpty()) {
                Toast.makeText(this, "No attendance data available to generate report.", Toast.LENGTH_SHORT).show();
                return;
            }
            createAttendanceReport(attendanceList);
            if (pdfUri != null) {
                Toast.makeText(this, "Report generated to Downloads: " + pdfUri.toString(), Toast.LENGTH_LONG).show();
                displayPdfFromUri(pdfUri);
            } else if (pFile != null && pFile.exists()) {
                Toast.makeText(this, "Report generated to Downloads: " + pFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                pdfView.fromFile(pFile)
                        .pages(0)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .defaultPage(0)
                        .load();
            } else {
                Toast.makeText(this, "Failed to generate PDF report.", Toast.LENGTH_SHORT).show();
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating report: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void populateAttendanceData() {
        attendanceList.clear(); // Clear any existing hardcoded data

        // Fetch data from Firestore
        db.collection("Attendance")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming your document fields are 'date', 'studentName', 'subject'
                            String date = document.getString("date");
                            String studentName = document.getString("studentName");
                            String subject = document.getString("subject");

                            if (date != null && studentName != null && subject != null) {
                                attendanceList.add(new AttendanceEntry(date, studentName, subject));
                                Log.d("AttendanceData", "Fetched: Date: " + date +
                                        ", Student: " + studentName +
                                        ", Subject: " + subject);
                            } else {
                                Log.w("AttendanceData", "Skipping document with null fields: " + document.getId());
                            }
                        }
                        // Data is now loaded into attendanceList
                        // You can choose to generate the report automatically here if desired,
                        // or rely on the button click. For now, the button click will trigger generation.
                        Toast.makeText(this, "Attendance data loaded from Firestore.", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.e("AttendanceData", "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Error loading attendance data from Firestore.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createAttendanceReport(List<AttendanceEntry> attendanceEntries) throws DocumentException, IOException {
        BaseColor colorWhite = BaseColor.WHITE;
        BaseColor grayColor = new BaseColor(66, 80, 102);

        OutputStream outputStream = null;
        Document document = new Document(PageSize.A4);
        String fileName = "AttendanceReport_" + System.currentTimeMillis() + ".pdf";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                pdfUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (pdfUri == null) {
                    throw new IOException("Failed to create new MediaStore entry.");
                }
                outputStream = getContentResolver().openOutputStream(pdfUri);
                if (outputStream == null) {
                    throw new IOException("Failed to open output stream for MediaStore URI.");
                }
            } else {
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs();
                }
                pFile = new File(downloadsDir, fileName);
                outputStream = new FileOutputStream(pFile);
                pdfUri = Uri.fromFile(pFile);
            }

            if (outputStream == null) {
                throw new IOException("Output stream is null, cannot write PDF.");
            }

            PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfPTable table = new PdfPTable(new float[]{5, 25, 25, 25});
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setFixedHeight(50);
            table.setTotalWidth(PageSize.A4.getWidth());
            table.setWidthPercentage(100);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

            Font whiteFont = new Font(Font.FontFamily.HELVETICA, 15.0f, Font.BOLD, colorWhite);

            Chunk noText = new Chunk("No.", whiteFont);
            PdfPCell noCell = new PdfPCell(new Phrase(noText));
            noCell.setFixedHeight(50);
            noCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            noCell.setVerticalAlignment(Element.ALIGN_CENTER);

            Chunk dateText = new Chunk("Date", whiteFont);
            PdfPCell dateCell = new PdfPCell(new Phrase(dateText));
            dateCell.setFixedHeight(50);
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateCell.setVerticalAlignment(Element.ALIGN_CENTER);

            Chunk studentNameText = new Chunk("Student Name", whiteFont);
            PdfPCell studentNameCell = new PdfPCell(new Phrase(studentNameText));
            studentNameCell.setFixedHeight(50);
            studentNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            studentNameCell.setVerticalAlignment(Element.ALIGN_CENTER);

            Chunk subjectText = new Chunk("Subject", whiteFont);
            PdfPCell subjectCell = new PdfPCell(new Phrase(subjectText));
            subjectCell.setFixedHeight(50);
            subjectCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            subjectCell.setVerticalAlignment(Element.ALIGN_CENTER);

            Chunk footerText = new Chunk("ScienceMore Admin - Copyright @ 2025");
            PdfPCell footCell = new PdfPCell(new Phrase(footerText));
            footCell.setFixedHeight(70);
            footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footCell.setVerticalAlignment(Element.ALIGN_CENTER);
            footCell.setColspan(4);

            table.addCell(noCell);
            table.addCell(dateCell);
            table.addCell(studentNameCell);
            table.addCell(subjectCell);
            table.setHeaderRows(1);

            PdfPCell[] cells = table.getRow(0).getCells();
            for (PdfPCell cell : cells) {
                cell.setBackgroundColor(grayColor);
            }

            for (int i = 0; i < attendanceEntries.size(); i++) {
                AttendanceEntry entry = attendanceEntries.get(i);

                String id = String.valueOf(i + 1);
                String date = entry.getDate();
                String studentName = entry.getStudentName();
                String subject = entry.getSubject();

                table.addCell(id + ". ");
                table.addCell(date);
                table.addCell(studentName);
                table.addCell(subject);
            }

            PdfPTable footTable = new PdfPTable(new float[]{5, 25, 25, 25});
            footTable.setTotalWidth(PageSize.A4.getWidth());
            footTable.setWidthPercentage(100);
            footTable.addCell(footCell);

            document.add(new Paragraph("ScienceMore Attendance Report\n\n", new Font(Font.FontFamily.HELVETICA, 25.0f, Font.NORMAL, grayColor)));
            document.add(table);
            document.add(footTable);

        } finally {
            if (document.isOpen()) {
                document.close();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayPdfFromUri(Uri uri) {
        if (uri != null) {
            pdfView.fromUri(uri)
                    .pages(0)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .load();
        } else {
            Toast.makeText(this, "PDF URI is null, cannot display report.", Toast.LENGTH_SHORT).show();
            Log.e("AttendenceReport", "PDF URI is null.");
        }
    }
}