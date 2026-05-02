package com.securetest.controller;

import com.securetest.dto.*;
import com.securetest.model.*;
import com.securetest.repository.UserRepository;
import com.securetest.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    @Autowired private ExamService examService;
    @Autowired private UserRepository userRepository;

    private String getTeacherId(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).map(User::getId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    @PostMapping("/exams")
    public ResponseEntity<?> createExam(@Valid @RequestBody CreateExamRequest req, Authentication auth) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(examService.createExam(req, getTeacherId(auth))); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error","Failed","message",e.getMessage())); }
    }

    @GetMapping("/exams")
    public ResponseEntity<?> getMyExams(Authentication auth) {
        return ResponseEntity.ok(examService.getTeacherExams(getTeacherId(auth)));
    }

    @GetMapping("/exams/{examId}")
    public ResponseEntity<?> getExamDetail(@PathVariable String examId, Authentication auth) {
        try { return ResponseEntity.ok(examService.getExamForTeacher(examId, getTeacherId(auth))); }
        catch (RuntimeException e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message",e.getMessage())); }
    }

    @DeleteMapping("/exams/{examId}")
    public ResponseEntity<?> deleteExam(@PathVariable String examId, Authentication auth) {
        try { examService.deleteExam(examId, getTeacherId(auth)); return ResponseEntity.ok(Map.of("message","Deleted.")); }
        catch (RuntimeException e) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message",e.getMessage())); }
    }

    @PatchMapping("/exams/{examId}/toggle")
    public ResponseEntity<?> toggleExam(@PathVariable String examId, Authentication auth) {
        try { Exam e = examService.toggleExamStatus(examId, getTeacherId(auth));
              return ResponseEntity.ok(Map.of("message", e.isActive()?"Active":"Hidden", "exam", e)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    }

    @PostMapping("/exams/{examId}/questions")
    public ResponseEntity<?> addQuestion(@PathVariable String examId, @Valid @RequestBody AddQuestionRequest req, Authentication auth) {
        try { Exam e = examService.addQuestion(examId, req, getTeacherId(auth));
              return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Question added.","exam",e)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    }

    @DeleteMapping("/exams/{examId}/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String examId, @PathVariable String questionId, Authentication auth) {
        try { Exam e = examService.deleteQuestion(examId, questionId, getTeacherId(auth));
              return ResponseEntity.ok(Map.of("message","Question deleted.","totalQuestions",e.getQuestions().size())); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    }

    @GetMapping("/exams/{examId}/submissions")
    public ResponseEntity<?> getSubmissions(@PathVariable String examId, Authentication auth) {
        try { List<ExamSubmission> subs = examService.getExamSubmissions(examId, getTeacherId(auth));
              return ResponseEntity.ok(Map.of("totalSubmissions",subs.size(),"submissions",subs)); }
        catch (RuntimeException e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message",e.getMessage())); }
    }
}
