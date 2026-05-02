package com.securetest.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "exam_submissions")
public class ExamSubmission {
    @Id private String id;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String examId;
    private String examTitle;
    private Map<String, String> answers;
    private int score;
    private int totalMarks;
    private double percentage;
    private LocalDateTime submittedAt;
    private boolean autoSubmitted = false;

    public ExamSubmission() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String v) { this.studentId = v; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String v) { this.studentName = v; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String v) { this.studentEmail = v; }
    public String getExamId() { return examId; }
    public void setExamId(String v) { this.examId = v; }
    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String v) { this.examTitle = v; }
    public Map<String, String> getAnswers() { return answers; }
    public void setAnswers(Map<String, String> v) { this.answers = v; }
    public int getScore() { return score; }
    public void setScore(int v) { this.score = v; }
    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int v) { this.totalMarks = v; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double v) { this.percentage = v; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime v) { this.submittedAt = v; }
    public boolean isAutoSubmitted() { return autoSubmitted; }
    public void setAutoSubmitted(boolean v) { this.autoSubmitted = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final ExamSubmission s = new ExamSubmission();
        public Builder studentId(String v) { s.studentId = v; return this; }
        public Builder studentName(String v) { s.studentName = v; return this; }
        public Builder studentEmail(String v) { s.studentEmail = v; return this; }
        public Builder examId(String v) { s.examId = v; return this; }
        public Builder examTitle(String v) { s.examTitle = v; return this; }
        public Builder answers(Map<String, String> v) { s.answers = v; return this; }
        public Builder score(int v) { s.score = v; return this; }
        public Builder totalMarks(int v) { s.totalMarks = v; return this; }
        public Builder percentage(double v) { s.percentage = v; return this; }
        public Builder submittedAt(LocalDateTime v) { s.submittedAt = v; return this; }
        public Builder autoSubmitted(boolean v) { s.autoSubmitted = v; return this; }
        public ExamSubmission build() { return s; }
    }
}
