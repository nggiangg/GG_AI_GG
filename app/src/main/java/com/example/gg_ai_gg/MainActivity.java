package com.example.gg_ai_gg;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;

    // Define the custom permission as a string
    private static final String READ_MEDIA_DOCUMENTS = "android.permission.READ_MEDIA_DOCUMENTS";

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedPdfUri = result.getData().getData();
                    if (selectedPdfUri != null) {
                        processPdfDocument(selectedPdfUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up button click listener
        Button uploadButton = findViewById(R.id.button2);
        uploadButton.setOnClickListener(v -> checkPermissionsAndOpenFilePicker());
    }

    private void checkPermissionsAndOpenFilePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+, we don't need special permissions for ACTION_OPEN_DOCUMENT
            // since the system file picker handles the permissions
            openFilePicker();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android 6-12 we need READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                openFilePicker();
            }
        } else {
            // For earlier versions, permissions granted at install time
            openFilePicker();
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        filePickerLauncher.launch(intent);
    }

    private void processPdfDocument(Uri pdfUri) {
        PdfProcessor pdfProcessor = new PdfProcessor(this);
        Toast.makeText(this, "Đang xử lý PDF...", Toast.LENGTH_SHORT).show();

        // You might want to run this in a background thread in a production app
        String textFilePath = pdfProcessor.processPdfToText(pdfUri);

        if (textFilePath != null) {
            File outputFile = new File(textFilePath);
            Toast.makeText(this,
                    "Đã xử lý xong PDF. File text được lưu tại:\n" + textFilePath,
                    Toast.LENGTH_LONG).show();

            // Optionally, you can now continue to the SecondaryActivity
            // and pass the text file path as an extra
            Intent intent = new Intent(this, SecondaryActivity.class);
            intent.putExtra("TEXT_FILE_PATH", textFilePath);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Lỗi khi xử lý PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                Toast.makeText(this, "Cần quyền truy cập để xử lý file PDF",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
