package com.example.gg_ai_gg;

import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeminiApiService {
    private static final String TAG = "GeminiApiService";
    private final String apiKey;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public GeminiApiService(Context context, String apiKey) {
        this.apiKey = apiKey;
    }

    public interface TermExtractionCallback {
        void onSuccess(List<Term> terms);
        void onFailure(String errorMessage);
    }

    public void extractTermsFromText(String text, TermExtractionCallback callback) {
        // Tạo prompt cho Gemini API bằng tiếng Việt
        String prompt = "Trích xuất các thuật ngữ học thuật và chuyên ngành từ văn bản sau đây. " +
                "Đối với mỗi thuật ngữ, hãy cung cấp: thuật ngữ, lĩnh vực/ngành mà nó thuộc về, " +
                "định nghĩa ngắn gọn, định nghĩa chi tiết, ngữ cảnh trong văn bản, " +
                "và các thuật ngữ liên quan nếu có. " +
                "Định dạng phản hồi dưới dạng mảng JSON với các đối tượng chứa: " +
                "term, field, shortDefinition, detailedDefinition, context, và relatedTerms (dưới dạng mảng). " +
                "QUAN TRỌNG: Hãy trả lời bằng tiếng Việt cho tất cả các thông tin. " +
                "Xác định TẤT CẢ các thuật ngữ từ văn bản (tối thiểu 20-30 thuật ngữ nếu có thể). " +
                "QUAN TRỌNG: Hãy trích xuất đầy đủ và toàn diện. Dưới đây là văn bản: \n\n" + text;

        // Execute on background thread
        executor.execute(() -> {
            try {
                // Sử dụng phương thức HTTP trực tiếp - đơn giản và ổn định nhất
                String responseText = callGeminiApi(prompt);
                Log.d(TAG, "API Response: " + responseText);

                // Process the response
                String jsonStr = extractJsonFromResponse(responseText);
                List<Term> terms = parseTermsFromJson(jsonStr);

                // Return success on main thread
                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(terms));

            } catch (Exception exception) {
                Log.e(TAG, "API call failed", exception);

                // Return failure on main thread
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("Lỗi kết nối API: " + exception.getMessage()));
            }
        });
    }

    // Phương thức HTTP trực tiếp để gọi API Gemini
    private String callGeminiApi(String prompt) throws Exception {
        java.net.URL url = new java.net.URL("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent?key=" + apiKey);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

//        // Thêm timeout dài hơn
//        conn.setConnectTimeout(30000); // 30 giây
//        conn.setReadTimeout(60000);    // 60 giây

        // Tạo JSON request body
        org.json.JSONObject body = new org.json.JSONObject();
        org.json.JSONArray contents = new org.json.JSONArray();
        org.json.JSONObject content = new org.json.JSONObject();
        org.json.JSONArray parts = new org.json.JSONArray();
        org.json.JSONObject part = new org.json.JSONObject();
        part.put("text", prompt);
        parts.put(part);
        content.put("parts", parts);
        contents.put(content);
        body.put("contents", contents);

        // Gửi request
        try (java.io.OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Đọc response
        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(conn.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            // Parse response JSON
            org.json.JSONObject responseJson = new org.json.JSONObject(response.toString());
            return responseJson
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        } finally {
            conn.disconnect();
        }
    }

    private String extractJsonFromResponse(String response) {
        // Sometimes Gemini wraps JSON in code blocks
        if (response.contains("```json")) {
            int start = response.indexOf("```json") + 7;
            int end = response.lastIndexOf("```");
            return response.substring(start, end).trim();
        } else if (response.contains("```")) {
            int start = response.indexOf("```") + 3;
            int end = response.lastIndexOf("```");
            return response.substring(start, end).trim();
        }
        return response;
    }

    private List<Term> parseTermsFromJson(String jsonStr) throws JSONException {
        List<Term> terms = new ArrayList<>();

        // Add error checking for empty or invalid JSON
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return terms;
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonStr);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String term = jsonObject.getString("term");
                String field = jsonObject.optString("field", "");
                String shortDefinition = jsonObject.optString("shortDefinition", "");

                Term termObj = new Term(term, field, shortDefinition);

                // Set optional fields
                if (jsonObject.has("detailedDefinition")) {
                    termObj.setDetailedDefinition(jsonObject.getString("detailedDefinition"));
                }

                if (jsonObject.has("context")) {
                    termObj.setContext(jsonObject.getString("context"));
                }

                if (jsonObject.has("source")) {
                    termObj.setSource(jsonObject.getString("source"));
                }

                if (jsonObject.has("relatedTerms")) {
                    JSONArray relatedTermsJson = jsonObject.getJSONArray("relatedTerms");
                    String[] relatedTerms = new String[relatedTermsJson.length()];

                    for (int j = 0; j < relatedTermsJson.length(); j++) {
                        relatedTerms[j] = relatedTermsJson.getString(j);
                    }

                    termObj.setRelatedTerms(relatedTerms);
                }

                terms.add(termObj);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage() + "\nJSON: " + jsonStr, e);
            throw e;
        }

        return terms;
    }
}