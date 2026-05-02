package com.securetest.model;
import java.util.List;
import java.util.UUID;

public class Question {
    private String id = UUID.randomUUID().toString();
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private int marks = 1;

    public Question() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String v) { this.questionText = v; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> v) { this.options = v; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String v) { this.correctAnswer = v; }
    public int getMarks() { return marks; }
    public void setMarks(int v) { this.marks = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Question q = new Question();
        public Builder id(String v) { q.id = v; return this; }
        public Builder questionText(String v) { q.questionText = v; return this; }
        public Builder options(List<String> v) { q.options = v; return this; }
        public Builder correctAnswer(String v) { q.correctAnswer = v; return this; }
        public Builder marks(int v) { q.marks = v; return this; }
        public Question build() { return q; }
    }
}
