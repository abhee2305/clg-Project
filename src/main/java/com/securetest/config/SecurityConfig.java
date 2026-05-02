package com.securetest.config;

import com.securetest.security.JwtAuthFilter;
import com.securetest.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig - Spring Security Configuration
 *
 * This is the heart of the security setup. It:
 *
 *  1. Disables CSRF (not needed for JWT-based REST APIs)
 *  2. Configures CORS (allows React frontend on port 3000 to call the API)
 *  3. Sets up public routes (/api/auth/**) vs protected routes
 *  4. Makes the app stateless (no server-side sessions — JWT handles state)
 *  5. Wires in our JwtAuthFilter
 *  6. Configures BCrypt for password hashing
 *
 * Equivalent to all the middleware setup in server.js + protect.js in Node.js.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // Enables @PreAuthorize on controller methods
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * SecurityFilterChain — defines which routes are public and which require auth.
     *
     * Public routes (no token needed):
     *   POST /api/auth/register → anyone can register
     *   POST /api/auth/login    → anyone can log in
     *
     * Protected routes (token required):
     *   Everything else → must have a valid JWT in Authorization header
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ── Disable CSRF ──
            // CSRF protection is for cookie-based auth. We use JWT, so we don't need it.
            .csrf(AbstractHttpConfigurer::disable)

            // ── Configure CORS ──
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ── Route Authorization Rules ──
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/", "/index.html", "/student.html", "/teacher.html", "/exam.html", "/*.js", "/*.css", "/*.ico", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
            )

            // ── Stateless Session ──
            // Don't create/use server-side HTTP sessions.
            // Each request must carry its own JWT token.
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ── Wire in our custom components ──
            .authenticationProvider(authenticationProvider())

            // ── Add JWT filter BEFORE Spring's default login filter ──
            // This ensures JWT is checked on every request
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS Configuration
     *
     * Allows the React frontend (localhost:3000) to call this API.
     * Without this, browsers would block cross-origin requests.
     *
     * Equivalent to the cors() middleware in Express.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // When origin is "*", we can't use allowCredentials=true
        // So we use allowedOriginPatterns instead which supports wildcards
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);    // Apply to all routes
        return source;
    }

    /**
     * AuthenticationProvider — wires together:
     *  - Our UserDetailsService (loads user from MongoDB)
     *  - BCryptPasswordEncoder (verifies hashed password)
     *
     * Spring Security uses this during login to verify credentials.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager — used in AuthService to trigger the login flow.
     * Spring Security calls this with the raw email + password, and it handles
     * finding the user and checking the password.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * PasswordEncoder — BCrypt hashing (equivalent to bcryptjs in Node.js)
     *
     * BCrypt is a one-way hash — you can't reverse it.
     * To verify: BCrypt.checkpw(rawPassword, storedHash)
     *
     * Strength = 10 (same as bcryptjs default saltRounds: 10)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
