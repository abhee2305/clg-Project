package com.securetest.repository;

import com.securetest.model.ExamSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ExamSubmissionRepository - Data Access for Exam Submissions
 */
@Repository
public interface ExamSubmissionRepository extends MongoRepository<ExamSubmission, String> {

    /**
     * Find all submissions for a specific exam (Teacher views results).
     * MongoDB: db.exam_submissions.find({ examId: examId })
     */
    List<ExamSubmission> findByExamId(String examId);

    /**
     * Find all submissions by a specific student (Student views their history).
     */
    List<ExamSubmission> findByStudentId(String studentId);

    /**
     * Check if a student already submitted a specific exam.
     * Prevents duplicate submissions.
     */
    Optional<ExamSubmission> findByStudentIdAndExamId(String studentId, String examId);

    /**
     * Check existence without fetching the whole document.
     */
    boolean existsByStudentIdAndExamId(String studentId, String examId);
}
