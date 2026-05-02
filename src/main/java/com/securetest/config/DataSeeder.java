package com.securetest.config;

import com.securetest.model.*;
import com.securetest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private ExamRepository examRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;
        seedUsers();
        seedExams();
    }

    private void seedUsers() {
        userRepository.saveAll(List.of(
            User.builder().name("Prof. Sharma").email("teacher@securetest.com")
                .password(passwordEncoder.encode("teacher123")).role(Role.TEACHER)
                .createdAt(LocalDateTime.now()).build(),
            User.builder().name("Abhee Sharma").email("student1@securetest.com")
                .password(passwordEncoder.encode("student123")).role(Role.STUDENT)
                .createdAt(LocalDateTime.now()).build(),
            User.builder().name("Riya Singh").email("student2@securetest.com")
                .password(passwordEncoder.encode("student123")).role(Role.STUDENT)
                .createdAt(LocalDateTime.now()).build()
        ));
    }

    private void seedExams() {
        User teacher = userRepository.findByEmail("teacher@securetest.com").orElseThrow();
        List<Question> javaQ = new ArrayList<>();
        javaQ.add(Question.builder().id(UUID.randomUUID().toString())
            .questionText("Which keyword is used to inherit a class in Java?")
            .options(List.of("implements","extends","inherits","super"))
            .correctAnswer("extends").marks(5).build());
        javaQ.add(Question.builder().id(UUID.randomUUID().toString())
            .questionText("What is the size of an int in Java?")
            .options(List.of("8 bits","16 bits","32 bits","64 bits"))
            .correctAnswer("32 bits").marks(5).build());
        javaQ.add(Question.builder().id(UUID.randomUUID().toString())
            .questionText("Which is NOT a feature of OOP?")
            .options(List.of("Encapsulation","Polymorphism","Compilation","Inheritance"))
            .correctAnswer("Compilation").marks(5).build());
        javaQ.add(Question.builder().id(UUID.randomUUID().toString())
            .questionText("JVM stands for?")
            .options(List.of("Java Variable Machine","Java Virtual Machine","Java Verified Module","Java Value Manager"))
            .correctAnswer("Java Virtual Machine").marks(5).build());
        javaQ.add(Question.builder().id(UUID.randomUUID().toString())
            .questionText("Entry point of a Java program?")
            .options(List.of("start()","run()","main()","init()"))
            .correctAnswer("main()").marks(5).build());

        List<Question> dsQ = new ArrayList<>();
        dsQ.add(Question.builder().id(UUID.randomUUID().toString())
            .questionText("Which data structure uses LIFO?")
            .options(List.of("Queue","Stack","LinkedList","Tree"))
            .correctAnswer("Stack").marks(4).build());
        dsQ.add(Question.builder().id(UUID.randomUUID().toString())
            .questionText("Time complexity of binary search?")
            .options(List.of("O(n)","O(n²)","O(log n)","O(1)"))
            .correctAnswer("O(log n)").marks(4).build());
        dsQ.add(Question.builder().id(UUID.randomUUID().toString())
            .questionText("Which traversal visits root FIRST?")
            .options(List.of("Inorder","Postorder","Preorder","Level order"))
            .correctAnswer("Preorder").marks(4).build());
        dsQ.add(Question.builder().id(UUID.randomUUID().toString())
            .questionText("Array index in Java starts from?")
            .options(List.of("1","0","-1","Depends"))
            .correctAnswer("0").marks(4).build());
        dsQ.add(Question.builder().id(UUID.randomUUID().toString())
            .questionText("Which is NOT a type of linked list?")
            .options(List.of("Singly","Doubly","Circular","Square"))
            .correctAnswer("Square").marks(4).build());

        examRepository.saveAll(List.of(
            Exam.builder().title("Java Fundamentals").description("Core Java: OOP, data types, JVM.")
                .duration(30).createdBy(teacher.getId()).createdByName(teacher.getName())
                .isActive(true).questions(javaQ).totalMarks(25).createdAt(LocalDateTime.now()).build(),
            Exam.builder().title("Data Structures & Algorithms").description("Arrays, stacks, trees, complexity.")
                .duration(45).createdBy(teacher.getId()).createdByName(teacher.getName())
                .isActive(true).questions(dsQ).totalMarks(20).createdAt(LocalDateTime.now()).build()
        ));
    }
}
