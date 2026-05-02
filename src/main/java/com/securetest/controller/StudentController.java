package com.securetest.controller;

import com.securetest.dto.SubmitExamRequest;
import com.securetest.model.*;
import com.securetest.repository.UserRepository;
import com.securetest.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    @Autowired private ExamService examService;
    @Autowired private UserRepository userRepository;

    private String getStudentId(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).map(User::getId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @GetMapping("/exams")
    public ResponseEntity<?> getExams() {
        List<Map<String, Object>> exams = examService.getActiveExamsForStudent();
        return ResponseEntity.ok(Map.of("totalExams", exams.size(), "exams", exams));
    }

    @GetMapping("/exams/{examId}")
    public ResponseEntity<?> getExam(@PathVariable String examId, Authentication auth) {
        try { return ResponseEntity.ok(examService.getExamForStudent(examId, getStudentId(auth))); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    }

    @PostMapping("/exams/{examId}/submit")
    public ResponseEntity<?> submitExam(@PathVariable String examId, @Valid @RequestBody SubmitExamRequest req, Authentication auth) {
        try {
            ExamSubmission result = examService.submitExam(examId, req, getStudentId(auth));
            return ResponseEntity.ok(Map.of(
                "message","Submitted!","submissionId",result.getId(),
                "score",result.getScore(),"totalMarks",result.getTotalMarks(),
                "percentage",result.getPercentage(),"autoSubmitted",result.isAutoSubmitted()));
        } catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    }

    @GetMapping("/results")
    public ResponseEntity<?> getResults(Authentication auth) {
        List<ExamSubmission> results = examService.getStudentResults(getStudentId(auth));
        return ResponseEntity.ok(Map.of("totalExamsTaken", results.size(), "results", results));
    }
}
