package com.lifeAIFrontend.LifeAIFrontend.model;

import java.util.List;

public class AnalysisResult {

    private boolean readable;
    private List<TermExplanation> terms;
    private int unexplainedTermsCount;

    public AnalysisResult() {}

    public AnalysisResult(boolean readable, List<TermExplanation> terms, int unexplainedTermsCount) {
        this.readable = readable;
        this.terms = terms;
        this.unexplainedTermsCount = unexplainedTermsCount;
    }

    public boolean isReadable() { return readable; }
    public void setReadable(boolean readable) { this.readable = readable; }

    public List<TermExplanation> getTerms() { return terms; }
    public void setTerms(List<TermExplanation> terms) { this.terms = terms; }

    public int getUnexplainedTermsCount() { return unexplainedTermsCount; }
    public void setUnexplainedTermsCount(int unexplainedTermsCount) {
        this.unexplainedTermsCount = unexplainedTermsCount;
    }

    public static class TermExplanation {
        private String term;
        private String explanation;

        public TermExplanation() {}

        public TermExplanation(String term, String explanation) {
            this.term = term;
            this.explanation = explanation;
        }

        public String getTerm() { return term; }
        public void setTerm(String term) { this.term = term; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }
}
