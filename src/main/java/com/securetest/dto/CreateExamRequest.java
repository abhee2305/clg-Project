package com.securetest.dto;
import jakarta.validation.constraints.*;

public class CreateExamRequest {
    @NotBlank(message = "Title is required") private String title;
    private String description;
    @Min(1) @Max(300) private int duration = 60;

    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public int getDuration() { return duration; }
    public void setDuration(int v) { this.duration = v; }
}
