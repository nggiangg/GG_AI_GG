package com.example.gg_ai_gg;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentDocumentsManager {
    private static final String PREFS_NAME = "RecentDocumentsPrefs";
    private static final String KEY_DOCUMENTS = "recent_documents";
    private static final int MAX_DOCUMENTS = 10;

    private final SharedPreferences prefs;

    public RecentDocumentsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void addDocument(String name, String path, String textPath) {
        List<RecentDocument> documents = getRecentDocuments();

        // Kiểm tra xem tài liệu đã tồn tại chưa
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getPath().equals(path)) {
                documents.remove(i);
                break;
            }
        }

        // Thêm tài liệu mới
        documents.add(0, new RecentDocument(name, path, textPath, System.currentTimeMillis()));

        // Giới hạn số lượng tài liệu
        if (documents.size() > MAX_DOCUMENTS) {
            documents = documents.subList(0, MAX_DOCUMENTS);
        }

        saveDocuments(documents);
    }

    public List<RecentDocument> getRecentDocuments() {
        String json = prefs.getString(KEY_DOCUMENTS, "[]");
        List<RecentDocument> documents = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");
                String path = obj.getString("path");
                String textPath = obj.getString("textPath");
                long timestamp = obj.getLong("timestamp");

                documents.add(new RecentDocument(name, path, textPath, timestamp));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return documents;
    }

    private void saveDocuments(List<RecentDocument> documents) {
        JSONArray array = new JSONArray();

        try {
            for (RecentDocument doc : documents) {
                JSONObject obj = new JSONObject();
                obj.put("name", doc.getName());
                obj.put("path", doc.getPath());
                obj.put("textPath", doc.getTextPath());
                obj.put("timestamp", doc.getTimestamp());
                array.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        prefs.edit().putString(KEY_DOCUMENTS, array.toString()).apply();
    }

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
}