package com.securetest.repository;

import com.securetest.model.CheatLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CheatLogRepository - Data Access for Cheat Logs
 *
 * Spring Data MongoDB auto-generates all queries from method names.
 */
@Repository
public interface CheatLogRepository extends MongoRepository<CheatLog, String> {

    /**
     * Get all cheat logs for a specific exam.
     * Used by teachers to review proctoring incidents for their exam.
     *
     * MongoDB: db.cheat_logs.find({ examId: examId }).sort({ timestamp: -1 })
     */
    List<CheatLog> findByExamIdOrderByTimestampDesc(String examId);

    /**
     * Get all cheat logs for a specific student in a specific exam.
     * Used to see all incidents by one student during one exam.
     */
    List<CheatLog> findByStudentIdAndExamIdOrderByTimestampDesc(String studentId, String examId);

    /**
     * Get all cheat logs by a student across all exams.
     */
    List<CheatLog> findByStudentIdOrderByTimestampDesc(String studentId);

    /**
     * Count how many incidents a student has in a given exam.
     * Useful for showing a quick "violation count" badge.
     */
    long countByStudentIdAndExamId(String studentId, String examId);

    /**
     * Count incidents grouped by type for a specific exam.
     * (Raw count by incidentType for a teacher summary)
     */
    long countByExamIdAndIncidentType(String examId, String incidentType);
}
