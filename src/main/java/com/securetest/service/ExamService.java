package com.securetest.service;

import com.securetest.dto.AddQuestionRequest;
import com.securetest.dto.CreateExamRequest;
import com.securetest.dto.SubmitExamRequest;
import com.securetest.model.*;
import com.securetest.repository.ExamRepository;
import com.securetest.repository.ExamSubmissionRepository;
import com.securetest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {

    @Autowired private ExamRepository examRepository;
    @Autowired private ExamSubmissionRepository submissionRepository;
    @Autowired private UserRepository userRepository;

    public Exam createExam(CreateExamRequest request, String teacherId) {
        String teacherName = userRepository.findById(teacherId).map(User::getName).orElse("Unknown");
        Exam exam = Exam.builder()
                .title(request.getTitle()).description(request.getDescription())
                .duration(request.getDuration()).createdBy(teacherId)
                .createdByName(teacherName).isActive(true)
                .totalMarks(0).questions(new ArrayList<>())
                .createdAt(LocalDateTime.now()).build();
        return examRepository.save(exam);
    }

    public Exam addQuestion(String examId, AddQuestionRequest request, String teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.getCreatedBy().equals(teacherId))
            throw new RuntimeException("Access denied.");
        if (!request.getOptions().contains(request.getCorrectAnswer()))
            throw new RuntimeException("Correct answer must be one of the options.");
        Question question = Question.builder()
                .id(UUID.randomUUID().toString())
                .questionText(request.getQuestionText())
                .options(request.getOptions())
                .correctAnswer(request.getCorrectAnswer())
                .marks(request.getMarks()).build();
        exam.getQuestions().add(question);
        exam.setTotalMarks(exam.getTotalMarks() + request.getMarks());
        return examRepository.save(exam);
    }

    public List<Exam> getTeacherExams(String teacherId) {
        return examRepository.findByCreatedBy(teacherId);
    }

    public Exam getExamForTeacher(String examId, String teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.getCreatedBy().equals(teacherId)) throw new RuntimeException("Access denied.");
        return exam;
    }

    public void deleteExam(String examId, String teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.getCreatedBy().equals(teacherId)) throw new RuntimeException("Access denied.");
        examRepository.deleteById(examId);
    }

    public Exam toggleExamStatus(String examId, String teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.getCreatedBy().equals(teacherId)) throw new RuntimeException("Access denied.");
        exam.setActive(!exam.isActive());
        return examRepository.save(exam);
    }

    public Exam deleteQuestion(String examId, String questionId, String teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.getCreatedBy().equals(teacherId)) throw new RuntimeException("Access denied.");
        Optional<Question> toRemove = exam.getQuestions().stream()
                .filter(q -> q.getId().equals(questionId)).findFirst();
        if (toRemove.isEmpty()) throw new RuntimeException("Question not found: " + questionId);
        exam.setTotalMarks(exam.getTotalMarks() - toRemove.get().getMarks());
        exam.getQuestions().removeIf(q -> q.getId().equals(questionId));
        return examRepository.save(exam);
    }

    public List<ExamSubmission> getExamSubmissions(String examId, String teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.getCreatedBy().equals(teacherId)) throw new RuntimeException("Access denied.");
        return submissionRepository.findByExamId(examId);
    }

    public List<Map<String, Object>> getActiveExamsForStudent() {
        return examRepository.findByIsActiveTrue().stream()
                .map(this::stripCorrectAnswers).collect(Collectors.toList());
    }

    public Map<String, Object> getExamForStudent(String examId, String studentId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.isActive()) throw new RuntimeException("This exam is not active.");
        if (submissionRepository.existsByStudentIdAndExamId(studentId, examId))
            throw new RuntimeException("You have already submitted this exam.");
        return stripCorrectAnswers(exam);
    }

    public ExamSubmission submitExam(String examId, SubmitExamRequest request, String studentId) {
        if (submissionRepository.existsByStudentIdAndExamId(studentId, examId))
            throw new RuntimeException("You have already submitted this exam.");
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        if (!exam.isActive()) throw new RuntimeException("Exam is not active.");
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        int score = 0;
        Map<String, String> studentAnswers = request.getAnswers();
        for (Question q : exam.getQuestions()) {
            String ans = studentAnswers.get(q.getId());
            if (ans != null && ans.equals(q.getCorrectAnswer())) score += q.getMarks();
        }
        int totalMarks = exam.getTotalMarks();
        double percentage = totalMarks > 0
                ? Math.round((double) score / totalMarks * 10000.0) / 100.0 : 0.0;
        ExamSubmission submission = ExamSubmission.builder()
                .studentId(studentId).studentName(student.getName())
                .studentEmail(student.getEmail()).examId(examId)
                .examTitle(exam.getTitle()).answers(studentAnswers)
                .score(score).totalMarks(totalMarks).percentage(percentage)
                .submittedAt(LocalDateTime.now())
                .autoSubmitted(request.isAutoSubmitted()).build();
        return submissionRepository.save(submission);
    }

    public List<ExamSubmission> getStudentResults(String studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    private Map<String, Object> stripCorrectAnswers(Exam exam) {
        List<Map<String, Object>> safeQuestions = exam.getQuestions().stream().map(q -> {
            Map<String, Object> sq = new LinkedHashMap<>();
            sq.put("id", q.getId());
            sq.put("questionText", q.getQuestionText());
            sq.put("options", q.getOptions());
            sq.put("marks", q.getMarks());
            return sq;
        }).collect(Collectors.toList());
        Map<String, Object> safeExam = new LinkedHashMap<>();
        safeExam.put("id", exam.getId());
        safeExam.put("title", exam.getTitle());
        safeExam.put("description", exam.getDescription());
        safeExam.put("duration", exam.getDuration());
        safeExam.put("createdByName", exam.getCreatedByName());
        safeExam.put("totalMarks", exam.getTotalMarks());
        safeExam.put("questionCount", exam.getQuestions().size());
        safeExam.put("questions", safeQuestions);
        return safeExam;
    }
}
