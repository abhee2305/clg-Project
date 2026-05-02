package com.securetest.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "exams")
public class Exam {
    @Id private String id;
    private String title;
    private String description;
    private int duration;
    private String createdBy;
    private String createdByName;
    private boolean isActive = true;
    private int totalMarks = 0;
    private List<Question> questions = new ArrayList<>();
    private LocalDateTime createdAt;

    public Exam() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public int getDuration() { return duration; }
    public void setDuration(int v) { this.duration = v; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String v) { this.createdBy = v; }
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String v) { this.createdByName = v; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean v) { this.isActive = v; }
    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int v) { this.totalMarks = v; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> v) { this.questions = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Exam e = new Exam();
        public Builder id(String v) { e.id = v; return this; }
        public Builder title(String v) { e.title = v; return this; }
        public Builder description(String v) { e.description = v; return this; }
        public Builder duration(int v) { e.duration = v; return this; }
        public Builder createdBy(String v) { e.createdBy = v; return this; }
        public Builder createdByName(String v) { e.createdByName = v; return this; }
        public Builder isActive(boolean v) { e.isActive = v; return this; }
        public Builder totalMarks(int v) { e.totalMarks = v; return this; }
        public Builder questions(List<Question> v) { e.questions = v; return this; }
        public Builder createdAt(LocalDateTime v) { e.createdAt = v; return this; }
        public Exam build() { return e; }
    }
}
