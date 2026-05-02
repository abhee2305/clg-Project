package com.securetest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SecureTest - Online AI Proctoring System
 * Main entry point for the Spring Boot application.
 *
 * Run this class to start the backend server on port 8080.
 * Make sure MongoDB is running before starting.
 */
@SpringBootApplication
public class SecureTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureTestApplication.class, args);
        System.out.println("\n==========================================");
        System.out.println("  SecureTest Backend Started Successfully!");
        System.out.println("  API Base URL: http://localhost:8080/api");
        System.out.println("==========================================\n");
    }
}
