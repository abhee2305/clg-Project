package com.securetest.dto;
import jakarta.validation.constraints.NotBlank;

public class LogIncidentRequest {
    @NotBlank private String examId;
    @NotBlank private String incidentType;
    private String description;
    private String severity = "MEDIUM";

    public String getExamId() { return examId; }
    public void setExamId(String v) { this.examId = v; }
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String v) { this.incidentType = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getSeverity() { return severity; }
    public void setSeverity(String v) { this.severity = v; }
}
