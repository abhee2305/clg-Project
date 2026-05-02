package com.securetest.repository;

import com.securetest.model.Exam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ExamRepository - Data Access for Exams
 *
 * Spring Data MongoDB auto-implements these methods from the method names.
 * No @Query or SQL needed.
 */
@Repository
public interface ExamRepository extends MongoRepository<Exam, String> {

    /**
     * Get all exams created by a specific teacher.
     * Used in teacher dashboard to show "my exams".
     *
     * MongoDB query: db.exams.find({ createdBy: teacherId })
     */
    List<Exam> findByCreatedBy(String teacherId);

    /**
     * Get all active exams — for the student exam listing page.
     *
     * MongoDB query: db.exams.find({ isActive: true })
     */
    List<Exam> findByIsActiveTrue();

    /**
     * Check if a student has already submitted a specific exam.
     * (Useful to prevent re-attempts)
     */
    boolean existsByIdAndCreatedBy(String examId, String teacherId);
}
