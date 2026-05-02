# SecureTest — Online AI Proctoring System
### Backend · Java 17 · Spring Boot 3 · MongoDB · JWT

A full-stack **Automated Exam Proctoring System** built with Spring Boot.
Teachers create exams, students take them, and an AI layer monitors for cheating in real-time.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (JJWT) |
| Database | MongoDB (Spring Data MongoDB) |
| Password Hashing | BCrypt (strength 10) |
| Build Tool | Maven |
| Frontend (separate) | React + TensorFlow.js |

---

## API Endpoints

### Auth (Public)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register as Student or Teacher |
| POST | `/api/auth/login` | Login and receive JWT token |
| GET | `/api/auth/test` | Health check |

### Teacher (Requires `TEACHER` role + JWT)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/teacher/exams` | Create a new exam |
| GET | `/api/teacher/exams` | Get all my exams |
| GET | `/api/teacher/exams/{id}` | Get exam details |
| DELETE | `/api/teacher/exams/{id}` | Delete an exam |
| PATCH | `/api/teacher/exams/{id}/toggle` | Toggle exam active/hidden |
| POST | `/api/teacher/exams/{id}/questions` | Add a question |
| DELETE | `/api/teacher/exams/{id}/questions/{qId}` | Delete a question |
| GET | `/api/teacher/exams/{id}/submissions` | View student results |

### Student (Requires `STUDENT` role + JWT)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/student/exams` | List all active exams |
| GET | `/api/student/exams/{id}` | Get exam to attempt |
| POST | `/api/student/exams/{id}/submit` | Submit exam answers |
| GET | `/api/student/results` | My result history |

### Proctoring (Phase 3 — in progress)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/proctor/log` | Log a cheat incident |
| GET | `/api/teacher/exams/{id}/cheat-logs` | View cheat logs |

---

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- MongoDB (local or Atlas)

### Steps

```bash
# 1. Clone the repo
git clone https://github.com/YOUR_USERNAME/securetest-backend.git
cd securetest-backend

# 2. Configure MongoDB URI in:
#    src/main/resources/application.properties
#    Set: spring.data.mongodb.uri=mongodb://localhost:27017/securetest

# 3. Run the application
mvn spring-boot:run

# Server starts on http://localhost:8080
```

### Test the API

```bash
# Health check
GET http://localhost:8080/api/auth/test

# Register a teacher
POST http://localhost:8080/api/auth/register
Body: { "name": "Prof. Sharma", "email": "teacher@test.com", "password": "pass123", "role": "TEACHER" }

# Login
POST http://localhost:8080/api/auth/login
Body: { "email": "teacher@test.com", "password": "pass123" }
# → Returns JWT token

# Create an exam (use token in Authorization: Bearer <token>)
POST http://localhost:8080/api/teacher/exams
Body: { "title": "Java Exam", "description": "OOP topics", "duration": 60 }
```

---

## Project Structure

```
src/main/java/com/securetest/
├── SecureTestApplication.java     ← Entry point
├── config/
│   ├── SecurityConfig.java        ← JWT, CORS, role-based access
│   └── GlobalExceptionHandler.java
├── controller/
│   ├── AuthController.java        ← /api/auth/**
│   ├── TeacherController.java     ← /api/teacher/**
│   ├── StudentController.java     ← /api/student/**
│   └── ProctorController.java     ← /api/proctor/** (Phase 3)
├── model/
│   ├── User.java
│   ├── Role.java
│   ├── Exam.java
│   ├── Question.java
│   ├── ExamSubmission.java
│   └── CheatLog.java              ← (Phase 3)
├── repository/
│   ├── UserRepository.java
│   ├── ExamRepository.java
│   └── ExamSubmissionRepository.java
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   └── UserDetailsServiceImpl.java
├── service/
│   ├── AuthService.java
│   └── ExamService.java
└── dto/
    ├── RegisterRequest.java
    ├── LoginRequest.java
    ├── AuthResponse.java
    ├── CreateExamRequest.java
    ├── AddQuestionRequest.java
    └── SubmitExamRequest.java
```

---

## Contributors
- Abhee ([@abhee2305](https://github.com/abhee2305))

---

## License
MIT
