package com.example.gg_ai_gg;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
// Import for initialization
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PdfProcessor {
    private static final String TAG = "PdfProcessor";
    private final Context context;

    public PdfProcessor(Context context) {
        this.context = context;
        // Initialize the PdfBox library - fixed import
        PDFBoxResourceLoader.init(context);
    }

    /**
     * Process a PDF file from a Uri and convert it to text
     * @param pdfUri The Uri of the PDF file
     * @return The path to the generated text file, or null if processing failed
     */
    public String processPdfToText(Uri pdfUri) {
        String extractedText = null;
        File outputFile = null;

        try {
            // Extract text from PDF
            extractedText = extractTextFromPdf(pdfUri);

            if (extractedText != null && !extractedText.isEmpty()) {
                // Save extracted text to file
                outputFile = saveTextToFile(extractedText);
                return outputFile.getAbsolutePath();
            } else {
                Log.e(TAG, "No text could be extracted from the PDF");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error processing PDF: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Extract text from a PDF file
     * @param pdfUri The Uri of the PDF file
     * @return The extracted text
     * @throws IOException If there's an error processing the PDF
     */
    private String extractTextFromPdf(Uri pdfUri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(pdfUri);
        if (inputStream == null) {
            throw new IOException("Could not open input stream for PDF");
        }

        PDDocument document = null;
        try {
            document = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } finally {
            if (document != null) {
                document.close();
            }
            inputStream.close();
        }
    }

    /**
     * Save extracted text to a file
     * @param text The text to save
     * @return The created file
     * @throws IOException If there's an error saving the file
     */
    private File saveTextToFile(String text) throws IOException {
        // Create a unique filename based on timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "pdf_text_" + timestamp + ".txt";

        // Get the Documents directory
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        // Create app-specific folder
        File appFolder = new File(directory, "GG_AI_GG");
        if (!appFolder.exists()) {
            if (!appFolder.mkdirs()) {
                throw new IOException("Could not create directory: " + appFolder.getAbsolutePath());
            }
        }

        File outputFile = new File(appFolder, fileName);

        // Write text to file
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
            writer.write(text);
            Log.i(TAG, "Text file saved successfully at: " + outputFile.getAbsolutePath());
        }

        return outputFile;
    }
}