package com.example.gg_ai_gg;

public class Term {
    private String term;
    private String field;
    private String shortDefinition;
    private String detailedDefinition;
    private String context;
    private String source;
    private String[] relatedTerms;

    public Term(String term, String field, String shortDefinition) {
        this.term = term;
        this.field = field;
        this.shortDefinition = shortDefinition;
    }

    // Getters and setters
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getShortDefinition() {
        return shortDefinition;
    }

    public void setShortDefinition(String shortDefinition) {
        this.shortDefinition = shortDefinition;
    }

    public String getDetailedDefinition() {
        return detailedDefinition;
    }

    public void setDetailedDefinition(String detailedDefinition) {
        this.detailedDefinition = detailedDefinition;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String[] getRelatedTerms() {
        return relatedTerms;
    }

    public void setRelatedTerms(String[] relatedTerms) {
        this.relatedTerms = relatedTerms;
    }
}