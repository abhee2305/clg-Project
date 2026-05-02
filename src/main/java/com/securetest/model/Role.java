package com.securetest.model;

/**
 * Role enum defines the two types of users in SecureTest:
 *
 * - STUDENT : Can view and attempt exams. Monitored by AI proctoring.
 * - TEACHER : Can create exams, add questions, and view cheat logs.
 *
 * This is stored as a String in MongoDB using @Enumerated(EnumType.STRING).
 * In Spring Security, roles are prefixed with "ROLE_" automatically
 * (e.g., ROLE_STUDENT, ROLE_TEACHER).
 */
public enum Role {
    STUDENT,
    TEACHER
}
