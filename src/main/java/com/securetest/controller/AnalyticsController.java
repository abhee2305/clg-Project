package com.securetest.controller;

import com.securetest.model.User;
import com.securetest.repository.UserRepository;
import com.securetest.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('TEACHER')")
public class AnalyticsController {

    @Autowired private AnalyticsService analyticsService;
    @Autowired private UserRepository userRepository;

    private String getTeacherId(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).map(User::getId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<?> getExamAnalytics(@PathVariable String examId, Authentication auth) {
        try { return ResponseEntity.ok(analyticsService.getExamAnalytics(examId, getTeacherId(auth))); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(Authentication auth) {
        try { return ResponseEntity.ok(analyticsService.getTeacherDashboardAnalytics(getTeacherId(auth))); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("message",e.getMessage())); }
    }
}
