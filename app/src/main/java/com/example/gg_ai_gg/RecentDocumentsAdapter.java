package com.example.gg_ai_gg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecentDocumentsAdapter extends RecyclerView.Adapter<RecentDocumentsAdapter.DocumentViewHolder> {
    private final List<RecentDocument> documents;
    private final Context context;
    private final OnDocumentClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnDocumentClickListener {
        void onDocumentClick(RecentDocument document);
    }

    public RecentDocumentsAdapter(Context context, List<RecentDocument> documents, OnDocumentClickListener listener) {
        this.context = context;
        this.documents = documents;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        RecentDocument document = documents.get(position);
        holder.tvDocumentName.setText(document.getName());

        String date = dateFormat.format(new Date(document.getTimestamp()));
        holder.tvDocumentDate.setText(date);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDocumentClick(document);
            }
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocumentName;
        TextView tvDocumentDate;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDocumentName = itemView.findViewById(R.id.tvDocumentName);
            tvDocumentDate = itemView.findViewById(R.id.tvDocumentDate);
        }
    }
}