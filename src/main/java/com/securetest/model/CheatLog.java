package com.securetest.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "cheat_logs")
public class CheatLog {
    @Id private String id;
    private String studentId;
    private String studentName;
    private String examId;
    private String examTitle;
    private String incidentType;
    private String description;
    private String severity = "MEDIUM";
    private LocalDateTime timestamp = LocalDateTime.now();

    public CheatLog() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String v) { this.studentId = v; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String v) { this.studentName = v; }
    public String getExamId() { return examId; }
    public void setExamId(String v) { this.examId = v; }
    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String v) { this.examTitle = v; }
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String v) { this.incidentType = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getSeverity() { return severity; }
    public void setSeverity(String v) { this.severity = v; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime v) { this.timestamp = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final CheatLog c = new CheatLog();
        public Builder studentId(String v) { c.studentId = v; return this; }
        public Builder studentName(String v) { c.studentName = v; return this; }
        public Builder examId(String v) { c.examId = v; return this; }
        public Builder examTitle(String v) { c.examTitle = v; return this; }
        public Builder incidentType(String v) { c.incidentType = v; return this; }
        public Builder description(String v) { c.description = v; return this; }
        public Builder severity(String v) { c.severity = v; return this; }
        public Builder timestamp(LocalDateTime v) { c.timestamp = v; return this; }
        public CheatLog build() { return c; }
    }
}
