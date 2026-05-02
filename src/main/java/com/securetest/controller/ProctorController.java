package com.securetest.controller;

import com.securetest.dto.LogIncidentRequest;
import com.securetest.model.*;
import com.securetest.repository.UserRepository;
import com.securetest.service.ProctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/proctor")
public class ProctorController {

    @Autowired private ProctorService proctorService;
    @Autowired private UserRepository userRepository;

    private String getUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/log")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> logIncident(@Valid @RequestBody LogIncidentRequest req, Authentication auth) {
        try { CheatLog log = proctorService.logIncident(req, getUserId(auth));
              return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Logged.","logId",log.getId())); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    }

    @GetMapping("/exam/{examId}/logs")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getExamLogs(@PathVariable String examId, Authentication auth) {
        try { List<CheatLog> logs = proctorService.getExamCheatLogs(examId, getUserId(auth));
              return ResponseEntity.ok(Map.of("totalViolations",logs.size(),"logs",logs)); }
        catch (RuntimeException e) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message",e.getMessage())); }
    }

    @GetMapping("/exam/{examId}/logs/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getStudentLogs(@PathVariable String examId, @PathVariable String studentId, Authentication auth) {
        try { List<CheatLog> logs = proctorService.getStudentLogsInExam(examId, studentId, getUserId(auth));
              return ResponseEntity.ok(Map.of("totalViolations",logs.size(),"logs",logs)); }
        catch (RuntimeException e) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message",e.getMessage())); }
    }

    @GetMapping("/exam/{examId}/summary")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getSummary(@PathVariable String examId, Authentication auth) {
        try { return ResponseEntity.ok(proctorService.getExamProctoringsummary(examId, getUserId(auth))); }
        catch (RuntimeException e) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message",e.getMessage())); }
    }
}
