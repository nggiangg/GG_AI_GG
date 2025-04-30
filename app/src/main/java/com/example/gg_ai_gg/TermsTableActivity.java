package com.example.gg_ai_gg;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TermsTableActivity extends AppCompatActivity {
    private static final String TAG = "TermsTableActivity";
    private static final String API_KEY = "AIzaSyCowoJp_uDmAiRabJaYfpC3bCjWI8mcvlo"; // Replace with your actual API key

    private RecyclerView rvTermsTable;
    private TermsAdapter adapter;
    private List<Term> termsList = new ArrayList<>();
    private GeminiApiService geminiApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_table);

        // Initialize views
        rvTermsTable = findViewById(R.id.rvTermsTable);

        // Set up RecyclerView
        rvTermsTable.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TermsAdapter(this, termsList);
        rvTermsTable.setAdapter(adapter);

        // Initialize Gemini API service
        geminiApiService = new GeminiApiService(this, API_KEY);

        // Get text file path from intent
        String textFilePath = getIntent().getStringExtra("TEXT_FILE_PATH");
        if (textFilePath != null) {
            processTextFile(textFilePath);
        } else {
            Toast.makeText(this, "Không tìm thấy file text", Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initialize views
        rvTermsTable = findViewById(R.id.rvTermsTable);

        // Thêm xử lý cho nút Back
        Button btnBack = findViewById(R.id.btnback);
        btnBack.setOnClickListener(v -> {
            // Quay về trang Main
            finish();
        });
    }

    private void processTextFile(String filePath) {
        // Read text from file
        StringBuilder text = new StringBuilder();
        try {
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line).append("\n");
            }
            br.close();

            // Extract terms using Gemini API
            extractTermsFromText(text.toString());

        } catch (IOException e) {
            Log.e(TAG, "Error reading text file", e);
            Toast.makeText(this, "Lỗi khi đọc file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void extractTermsFromText(String text) {
        // Show loading indicator or message
        Toast.makeText(this, "Đang phân tích văn bản...", Toast.LENGTH_SHORT).show();

        // Call Gemini API to extract terms
        geminiApiService.extractTermsFromText(text, new GeminiApiService.TermExtractionCallback() {
            @Override
            public void onSuccess(List<Term> terms) {
                // Update UI on main thread
                runOnUiThread(() -> {
                    termsList.clear();
                    termsList.addAll(terms);
                    adapter.notifyDataSetChanged();

                    if (terms.isEmpty()) {
                        Toast.makeText(TermsTableActivity.this,
                                "Không tìm thấy thuật ngữ chuyên ngành nào",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TermsTableActivity.this,
                                "Đã tìm thấy " + terms.size() + " thuật ngữ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Show error on main thread
                runOnUiThread(() -> {
                    Toast.makeText(TermsTableActivity.this,
                            "Lỗi: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}