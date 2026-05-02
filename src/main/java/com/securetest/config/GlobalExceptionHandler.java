package com.securetest.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - Centralized Error Handling
 *
 * This class intercepts exceptions thrown anywhere in the application
 * and converts them into clean, consistent JSON error responses.
 *
 * Without this, Spring would return ugly HTML error pages or raw stack traces.
 *
 * @RestControllerAdvice = applies to all @RestController classes globally
 *
 * Types of errors handled:
 *  1. Validation errors (@Valid fails) → 400 Bad Request
 *  2. Access denied (wrong role)       → 403 Forbidden
 *  3. Generic runtime exceptions       → 500 Internal Server Error
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors from @Valid annotations.
     *
     * Example: User sends { "email": "not-an-email" }
     * This returns:
     * {
     *   "errors": {
     *     "email": "Please provide a valid email address",
     *     "password": "Password must be at least 6 characters long"
     *   }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        // Collect all field-level validation errors
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation Failed");
        response.put("message", "Please check your input and try again.");
        response.put("errors", fieldErrors);

        return ResponseEntity.badRequest().body(response);  // 400
    }

    /**
     * Handle access denied errors.
     *
     * Example: A STUDENT tries to access /api/teacher/create-exam
     * This returns:
     * {
     *   "error": "Access Denied",
     *   "message": "You don't have permission to access this resource."
     * }
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)  // 403
                .body(Map.of(
                        "error", "Access Denied",
                        "message", "You don't have permission to access this resource."
                ));
    }

    /**
     * Handle any other unhandled exception.
     * Acts as a safety net to prevent raw error messages from reaching the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
                .body(Map.of(
                        "error", "Internal Server Error",
                        "message", "Something went wrong. Please try again later."
                ));
    }
}
