package com.securetest.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfig - API Documentation Setup
 *
 * After running the app, access docs at:
 *   http://localhost:8080/swagger-ui.html
 *
 * Shows all endpoints with:
 *  - Request/response formats
 *  - JWT authentication button (click Authorize → paste token)
 *  - Live "Try it out" testing for every endpoint
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "SecureTest — Online AI Proctoring System",
        version = "1.0.0",
        description = """
            REST API for SecureTest — an AI-powered online exam proctoring system.
            
            **Roles:**
            - TEACHER: Create exams, add questions, view results & cheat logs
            - STUDENT: Attempt exams, submit answers, view results
            
            **Authentication:**
            1. Call POST /api/auth/login to get a JWT token
            2. Click the 'Authorize' button above
            3. Enter: Bearer <your-token>
            4. All protected endpoints will now work
            """,
        contact = @Contact(name = "Abhee Sharma", url = "https://github.com/abhee2305")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development"),
        @Server(url = "https://securetest-backend.onrender.com", description = "Production")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Paste your JWT token here (get it from /api/auth/login)"
)
public class SwaggerConfig {
    // Configuration is done via annotations above
    // Swagger UI auto-discovers all @RestController classes
}
