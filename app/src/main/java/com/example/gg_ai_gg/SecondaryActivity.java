package com.example.gg_ai_gg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SecondaryActivity extends AppCompatActivity {
    private TextView tvDocumentContent;
    private Button btnAnalyze;
    private String textFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_secondary);

        // Initialize views
        tvDocumentContent = findViewById(R.id.tvDocumentContent);
        btnAnalyze = findViewById(R.id.button3);

        // Get text file path from intent
        textFilePath = getIntent().getStringExtra("TEXT_FILE_PATH");

        if (textFilePath != null) {
            displayTextContent(textFilePath);
        }

        // Set up analyze button
        btnAnalyze.setOnClickListener(v -> {
            if (textFilePath != null) {
                Intent intent = new Intent(SecondaryActivity.this, TermsTableActivity.class);
                intent.putExtra("TEXT_FILE_PATH", textFilePath);
                startActivity(intent);
            }
        });
    }

    private void displayTextContent(String filePath) {
        try {
            File file = new File(filePath);
            StringBuilder text = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line).append("\n");
            }
            br.close();

            tvDocumentContent.setText(text.toString());

        } catch (IOException e) {
            tvDocumentContent.setText("Lỗi khi đọc file: " + e.getMessage());
        }
    }
}