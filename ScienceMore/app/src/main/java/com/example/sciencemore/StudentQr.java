package com.example.sciencemore;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class StudentQr extends AppCompatActivity {
    private EditText editTextQrContent;
    private Button buttonGenerateQr;
    private ImageView imageViewQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_qr);

        editTextQrContent = findViewById(R.id.editTextQrContent);
        buttonGenerateQr = findViewById(R.id.buttonGenerateQr);
        imageViewQrCode = findViewById(R.id.imageViewQrCode);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        buttonGenerateQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQrCode();
            }
        });
    }
    private void generateQrCode() {
        String qrContent = editTextQrContent.getText().toString().trim();

        if (qrContent.isEmpty()) {
            Toast.makeText(this, "Please enter some text to generate QR code", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create a MultiFormatWriter instance
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            // Encode the content into a BitMatrix (QR code data)
            // Specify QR_CODE format, and desired width/height
            BitMatrix bitMatrix = multiFormatWriter.encode(qrContent, BarcodeFormat.QR_CODE, 500, 500); // 500x500 pixels

            // Use BarcodeEncoder from zxing-android-embedded to convert BitMatrix to Bitmap
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Set the generated Bitmap to the ImageView
            imageViewQrCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            // Handle any errors during QR code generation
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR code: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            e.printStackTrace();
            Toast.makeText(this, "An unexpected error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}