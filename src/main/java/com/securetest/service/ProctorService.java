package com.securetest.service;

import com.securetest.dto.LogIncidentRequest;
import com.securetest.model.*;
import com.securetest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProctorService {

    @Autowired private CheatLogRepository cheatLogRepository;
    @Autowired private ExamRepository examRepository;
    @Autowired private UserRepository userRepository;

    public CheatLog logIncident(LogIncidentRequest request, String studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found: " + request.getExamId()));
        String severity = determineSeverity(request.getIncidentType(), request.getSeverity());
        String description = (request.getDescription() != null && !request.getDescription().isBlank())
                ? request.getDescription() : getDefaultDescription(request.getIncidentType());
        CheatLog log = CheatLog.builder()
                .studentId(studentId).studentName(student.getName())
                .examId(request.getExamId()).examTitle(exam.getTitle())
                .incidentType(request.getIncidentType()).description(description)
                .severity(severity).timestamp(LocalDateTime.now()).build();
        return cheatLogRepository.save(log);
    }

    public List<CheatLog> getExamCheatLogs(String examId, String teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.getCreatedBy().equals(teacherId)) throw new RuntimeException("Access denied.");
        return cheatLogRepository.findByExamIdOrderByTimestampDesc(examId);
    }

    public List<CheatLog> getStudentLogsInExam(String examId, String studentId, String teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.getCreatedBy().equals(teacherId)) throw new RuntimeException("Access denied.");
        return cheatLogRepository.findByStudentIdAndExamIdOrderByTimestampDesc(studentId, examId);
    }

    public Map<String, Object> getExamProctoringsummary(String examId, String teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.getCreatedBy().equals(teacherId)) throw new RuntimeException("Access denied.");
        List<CheatLog> allLogs = cheatLogRepository.findByExamIdOrderByTimestampDesc(examId);
        String[] types = {"FACE_NOT_DETECTED","MULTIPLE_FACES","PHONE_DETECTED","TAB_SWITCH","FULL_SCREEN_EXIT"};
        Map<String, Long> byType = new HashMap<>();
        for (String t : types) byType.put(t, allLogs.stream().filter(l -> l.getIncidentType().equals(t)).count());
        Map<String, Long> byStudent = new HashMap<>();
        for (CheatLog l : allLogs) byStudent.merge(l.getStudentName(), 1L, Long::sum);
        Map<String, Object> summary = new HashMap<>();
        summary.put("examId", examId);
        summary.put("examTitle", exam.getTitle());
        summary.put("totalViolations", allLogs.size());
        summary.put("byType", byType);
        summary.put("byStudent", byStudent);
        return summary;
    }

    private String determineSeverity(String type, String provided) {
        if (provided != null && !provided.isBlank()) return provided.toUpperCase();
        return switch (type) {
            case "MULTIPLE_FACES","PHONE_DETECTED" -> "HIGH";
            case "FACE_NOT_DETECTED" -> "MEDIUM";
            default -> "LOW";
        };
    }

    private String getDefaultDescription(String type) {
        return switch (type) {
            case "FACE_NOT_DETECTED"  -> "Student face not detected.";
            case "MULTIPLE_FACES"     -> "Multiple faces detected.";
            case "PHONE_DETECTED"     -> "Mobile phone detected.";
            case "TAB_SWITCH"         -> "Student switched tabs.";
            case "FULL_SCREEN_EXIT"   -> "Exited fullscreen mode.";
            default                   -> "Proctoring violation detected.";
        };
    }
}
