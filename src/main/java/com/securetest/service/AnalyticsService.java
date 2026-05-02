package com.securetest.service;

import com.securetest.model.*;
import com.securetest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired private ExamRepository examRepository;
    @Autowired private ExamSubmissionRepository submissionRepository;

    public Map<String, Object> getExamAnalytics(String examId, String teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.getCreatedBy().equals(teacherId)) throw new RuntimeException("Access denied.");
        List<ExamSubmission> submissions = submissionRepository.findByExamId(examId);
        if (submissions.isEmpty()) {
            return Map.of("examId", examId, "examTitle", exam.getTitle(),
                    "totalStudents", 0, "message", "No submissions yet.");
        }
        int totalMarks = exam.getTotalMarks();
        int passThreshold = (int) Math.ceil(totalMarks * 0.4);
        int total = submissions.size();
        double avgScore = submissions.stream().mapToInt(ExamSubmission::getScore).average().orElse(0);
        int highest = submissions.stream().mapToInt(ExamSubmission::getScore).max().orElse(0);
        int lowest  = submissions.stream().mapToInt(ExamSubmission::getScore).min().orElse(0);
        double avgPct = submissions.stream().mapToDouble(ExamSubmission::getPercentage).average().orElse(0);
        long passed = submissions.stream().filter(s -> s.getScore() >= passThreshold).count();
        double passRate = Math.round((double) passed / total * 1000) / 10.0;

        List<Map<String, Object>> leaderboard = submissions.stream()
                .sorted(Comparator.comparingInt(ExamSubmission::getScore).reversed())
                .limit(5).map(s -> {
                    Map<String, Object> e = new LinkedHashMap<>();
                    e.put("studentName", s.getStudentName());
                    e.put("score", s.getScore());
                    e.put("totalMarks", s.getTotalMarks());
                    e.put("percentage", s.getPercentage());
                    return e;
                }).collect(Collectors.toList());

        Map<String, Long> dist = new LinkedHashMap<>();
        dist.put("0-40%",   submissions.stream().filter(s -> s.getPercentage() < 40).count());
        dist.put("40-60%",  submissions.stream().filter(s -> s.getPercentage() >= 40 && s.getPercentage() < 60).count());
        dist.put("60-80%",  submissions.stream().filter(s -> s.getPercentage() >= 60 && s.getPercentage() < 80).count());
        dist.put("80-100%", submissions.stream().filter(s -> s.getPercentage() >= 80).count());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("examId", examId);
        result.put("examTitle", exam.getTitle());
        result.put("totalMarks", totalMarks);
        result.put("totalStudents", total);
        result.put("passCount", passed);
        result.put("failCount", total - passed);
        result.put("passPercentage", passRate);
        result.put("averageScore", Math.round(avgScore * 10) / 10.0);
        result.put("highestScore", highest);
        result.put("lowestScore", lowest);
        result.put("averagePercentage", Math.round(avgPct * 10) / 10.0);
        result.put("leaderboard", leaderboard);
        result.put("scoreDistribution", dist);
        return result;
    }

    public Map<String, Object> getTeacherDashboardAnalytics(String teacherId) {
        List<Exam> exams = examRepository.findByCreatedBy(teacherId);
        List<Map<String, Object>> summaries = new ArrayList<>();
        int totalSubs = 0;
        double totalPass = 0;
        for (Exam exam : exams) {
            List<ExamSubmission> subs = submissionRepository.findByExamId(exam.getId());
            int count = subs.size();
            totalSubs += count;
            int pt = (int) Math.ceil(exam.getTotalMarks() * 0.4);
            long passed = subs.stream().filter(s -> s.getScore() >= pt).count();
            double passRate = count > 0 ? Math.round((double) passed / count * 1000) / 10.0 : 0;
            totalPass += passRate;
            double avg = subs.stream().mapToInt(ExamSubmission::getScore).average().orElse(0);
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("examId", exam.getId());
            s.put("examTitle", exam.getTitle());
            s.put("isActive", exam.isActive());
            s.put("totalMarks", exam.getTotalMarks());
            s.put("questionCount", exam.getQuestions().size());
            s.put("duration", exam.getDuration());
            s.put("submissionCount", count);
            s.put("passRate", passRate);
            s.put("averageScore", Math.round(avg * 10) / 10.0);
            summaries.add(s);
        }
        double overall = exams.isEmpty() ? 0 : Math.round(totalPass / exams.size() * 10) / 10.0;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalExams", exams.size());
        result.put("totalSubmissions", totalSubs);
        result.put("overallPassRate", overall);
        result.put("examSummaries", summaries);
        return result;
    }
}
