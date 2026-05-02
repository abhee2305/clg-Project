package com.securetest.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtUtil - JWT Token Utility Class
 *
 * This is the Java equivalent of the jsonwebtoken npm package used in Node.js.
 *
 * Responsibilities:
 *  1. generateToken()   → Create a new JWT for a logged-in user
 *  2. validateToken()   → Check if a token is valid and not expired
 *  3. extractEmail()    → Get the user's email from the token payload
 *  4. isTokenExpired()  → Check if the token has expired
 *
 * JWT Structure: header.payload.signature
 *  - Header: algorithm (HS256) and token type
 *  - Payload: claims (email, role, issued at, expiry)
 *  - Signature: HMAC-SHA256 signed with our secret key
 */
@Component  // Marks this as a Spring-managed bean
public class JwtUtil {

    /**
     * Secret key read from application.properties
     * app.jwt.secret=SecureTestSuperSecretKey...
     */
    @Value("${app.jwt.secret}")
    private String secretKey;

    /**
     * Token expiry time in milliseconds (read from application.properties)
     * app.jwt.expiration=86400000  (= 24 hours)
     */
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    // ─────────────────────────────────────────────────
    //  TOKEN GENERATION
    // ─────────────────────────────────────────────────

    /**
     * Generate a JWT token for a user.
     * Called after successful login or registration.
     *
     * @param userDetails the authenticated user (loaded from DB)
     * @return signed JWT string (e.g., "eyJhbGciOiJIUzI1NiJ9...")
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        // We can add extra info into the token payload here
        // e.g., extraClaims.put("role", userDetails.getAuthorities())
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Internal method to actually build the JWT.
     *
     * @param extraClaims additional data to embed in the token
     * @param userDetails the user
     * @param expiration  how long the token is valid (in ms)
     * @return signed JWT string
     */
    private String buildToken(Map<String, Object> extraClaims,
                               UserDetails userDetails,
                               long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())  // "username" = email in our app
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ─────────────────────────────────────────────────
    //  TOKEN VALIDATION
    // ─────────────────────────────────────────────────

    /**
     * Validate a JWT token.
     * Checks:
     *  1. The email in the token matches the logged-in user
     *  2. The token is not expired
     *
     * @param token       the JWT string from the Authorization header
     * @param userDetails the user loaded from DB
     * @return true if valid, false if invalid or expired
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Check if the token has passed its expiry date.
     *
     * @param token JWT string
     * @return true if expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ─────────────────────────────────────────────────
    //  CLAIM EXTRACTION
    // ─────────────────────────────────────────────────

    /**
     * Extract the email (subject) from a JWT token.
     * This is the main identifier we use for looking up users.
     *
     * @param token JWT string
     * @return email stored in the token
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract the expiration date from a JWT token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from the token payload.
     *
     * @param token          JWT string
     * @param claimsResolver function that picks which claim to return
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parse the JWT and return all claims.
     * This is where the signature is verified — invalid tokens throw an exception.
     *
     * @param token JWT string
     * @return Claims object (map-like) with all payload data
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ─────────────────────────────────────────────────
    //  SIGNING KEY
    // ─────────────────────────────────────────────────

    /**
     * Convert the secret string into a cryptographic Key object.
     * JJWT requires a Key, not a raw String, for security.
     *
     * @return HMAC-SHA key derived from our secret
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
            java.util.Base64.getEncoder().encodeToString(secretKey.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
