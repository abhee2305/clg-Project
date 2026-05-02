package com.securetest.dto;
import jakarta.validation.constraints.*;
import java.util.List;

public class AddQuestionRequest {
    @NotBlank(message = "Question text is required") private String questionText;
    @NotNull @Size(min=2, max=4) private List<String> options;
    @NotBlank(message = "Correct answer is required") private String correctAnswer;
    @Min(1) private int marks = 1;

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String v) { this.questionText = v; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> v) { this.options = v; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String v) { this.correctAnswer = v; }
    public int getMarks() { return marks; }
    public void setMarks(int v) { this.marks = v; }
}
