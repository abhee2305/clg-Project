package com.securetest.dto;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class SubmitExamRequest {
    @NotNull private Map<String, String> answers;
    private boolean autoSubmitted = false;

    public Map<String, String> getAnswers() { return answers; }
    public void setAnswers(Map<String, String> v) { this.answers = v; }
    public boolean isAutoSubmitted() { return autoSubmitted; }
    public void setAutoSubmitted(boolean v) { this.autoSubmitted = v; }
}
