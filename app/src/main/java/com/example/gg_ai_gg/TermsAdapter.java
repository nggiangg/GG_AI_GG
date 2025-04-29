package com.example.gg_ai_gg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class TermsAdapter extends RecyclerView.Adapter<TermsAdapter.TermViewHolder> {
    private final Context context;
    private List<Term> terms;

    public TermsAdapter(Context context, List<Term> terms) {
        this.context = context;
        this.terms = terms;
    }

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_term_detailed, parent, false);
        return new TermViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TermViewHolder holder, int position) {
        Term term = terms.get(position);

        holder.tvTerm.setText(term.getTerm());
        holder.tvField.setText(term.getField());
        holder.tvShortDefinition.setText(term.getShortDefinition());

        // Set optional fields if available
        if (term.getDetailedDefinition() != null && !term.getDetailedDefinition().isEmpty()) {
            holder.tvDetailedDefinition.setVisibility(View.VISIBLE);
            holder.tvDetailedDefinitionLabel.setVisibility(View.VISIBLE);
            holder.tvDetailedDefinition.setText(term.getDetailedDefinition());
        } else {
            holder.tvDetailedDefinition.setVisibility(View.GONE);
            holder.tvDetailedDefinitionLabel.setVisibility(View.GONE);
        }

        if (term.getContext() != null && !term.getContext().isEmpty()) {
            holder.tvContext.setVisibility(View.VISIBLE);
            holder.tvContextLabel.setVisibility(View.VISIBLE);
            holder.tvContext.setText(term.getContext());
        } else {
            holder.tvContext.setVisibility(View.GONE);
            holder.tvContextLabel.setVisibility(View.GONE);
        }

        if (term.getSource() != null && !term.getSource().isEmpty()) {
            holder.tvSource.setVisibility(View.VISIBLE);
            holder.tvSourceLabel.setVisibility(View.VISIBLE);
            holder.tvSource.setText(term.getSource());
        } else {
            holder.tvSource.setVisibility(View.GONE);
            holder.tvSourceLabel.setVisibility(View.GONE);
        }

        // Add related terms as chips
        if (term.getRelatedTerms() != null && term.getRelatedTerms().length > 0) {
            holder.cgRelatedTerms.setVisibility(View.VISIBLE);
            holder.tvRelatedTermsLabel.setVisibility(View.VISIBLE);

            holder.cgRelatedTerms.removeAllViews();
            for (String relatedTerm : term.getRelatedTerms()) {
                Chip chip = new Chip(context);
                chip.setText(relatedTerm);
                holder.cgRelatedTerms.addView(chip);
            }
        } else {
            holder.cgRelatedTerms.setVisibility(View.GONE);
            holder.tvRelatedTermsLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public void updateTerms(List<Term> newTerms) {
        this.terms = newTerms;
        notifyDataSetChanged();
    }

    public static class TermViewHolder extends RecyclerView.ViewHolder {
        TextView tvTerm, tvField, tvShortDefinition;
        TextView tvDetailedDefinitionLabel, tvDetailedDefinition;
        TextView tvContextLabel, tvContext;
        TextView tvSourceLabel, tvSource;
        TextView tvRelatedTermsLabel;
        ChipGroup cgRelatedTerms;

        public TermViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTerm = itemView.findViewById(R.id.tvTerm);
            tvField = itemView.findViewById(R.id.tvField);
            tvShortDefinition = itemView.findViewById(R.id.tvShortDefinition);
            tvDetailedDefinitionLabel = itemView.findViewById(R.id.tvDetailedDefinitionLabel);
            tvDetailedDefinition = itemView.findViewById(R.id.tvDetailedDefinition);
            tvContextLabel = itemView.findViewById(R.id.tvContextLabel);
            tvContext = itemView.findViewById(R.id.tvContext);
            tvSourceLabel = itemView.findViewById(R.id.tvSourceLabel);
            tvSource = itemView.findViewById(R.id.tvSource);
            tvRelatedTermsLabel = itemView.findViewById(R.id.tvRelatedTermsLabel);
            cgRelatedTerms = itemView.findViewById(R.id.cgRelatedTerms);
        }
    }
}