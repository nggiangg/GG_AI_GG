package com.example.gg_ai_gg;

import android.Manifest;
import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;

    // Define the custom permission as a string
    private static final String READ_MEDIA_DOCUMENTS = "android.permission.READ_MEDIA_DOCUMENTS";
    private RecyclerView rvRecentDocuments;
    private List<RecentDocument> recentDocuments = new ArrayList<>();
    private RecentDocumentsAdapter recentDocumentsAdapter;
    private RecentDocumentsManager documentsManager;
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

        // Khởi tạo DocumentsManager
        documentsManager = new RecentDocumentsManager(this);

        // Cài đặt RecyclerView
        rvRecentDocuments = findViewById(R.id.rvRecentDocuments);
        rvRecentDocuments.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        // Tải danh sách tài liệu gần đây
        loadRecentDocuments();
    }
    protected void onResume() {
        super.onResume();
        // Cập nhật danh sách mỗi khi quay lại màn hình
        loadRecentDocuments();
    }
    private void loadRecentDocuments() {
        recentDocuments = documentsManager.getRecentDocuments();
        recentDocumentsAdapter = new RecentDocumentsAdapter(this, recentDocuments, document -> {
            // Xử lý khi click vào tài liệu
            if (document.getTextPath() != null) {
                File textFile = new File(document.getTextPath());
                if (textFile.exists()) {
                    // Chuyển đến TermsTableActivity
                    Intent intent = new Intent(MainActivity.this, TermsTableActivity.class);
                    intent.putExtra("TEXT_FILE_PATH", document.getTextPath());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "File tạm không còn tồn tại, vui lòng tải lại tài liệu", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rvRecentDocuments.setAdapter(recentDocumentsAdapter);
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
                    "Đã xử lý xong PDF...",
                    Toast.LENGTH_SHORT).show();

            // Lưu vào danh sách tài liệu gần đây
            String fileName = documentsManager.getFileName(pdfUri, this);
            documentsManager.addDocument(fileName, pdfUri.toString(), textFilePath);

            // Cập nhật giao diện danh sách
            loadRecentDocuments();

            // Chuyển đến TermsTableActivity
            Intent intent = new Intent(this, TermsTableActivity.class);
            intent.putExtra("TEXT_FILE_PATH", textFilePath);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Lỗi khi xử lý PDF.", Toast.LENGTH_SHORT).show();
        }
    }
    // Thêm vào lớp RecentDocumentsManager
    public String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
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
