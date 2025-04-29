package com.example.gg_ai_gg;

public class RecentDocument {
    private String name;
    private String path;
    private String textPath;
    private long timestamp;

    public RecentDocument(String name, String path, String textPath, long timestamp) {
        this.name = name;
        this.path = path;
        this.textPath = textPath;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getTextPath() {
        return textPath;
    }

    public long getTimestamp() {
        return timestamp;
    }
}