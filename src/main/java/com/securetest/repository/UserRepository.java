package com.securetest.repository;

import com.securetest.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Data Access Layer for Users
 *
 * Extends MongoRepository<User, String>:
 *   - User  → the document class
 *   - String → the type of the @Id field
 *
 * Spring Data MongoDB automatically provides:
 *   - save(), findById(), findAll(), deleteById(), etc.
 *
 * We add custom query methods below — Spring generates the SQL/MongoDB query
 * automatically from the method name (no @Query needed for simple cases).
 *
 * Equivalent to Mongoose's User.findOne({ email }) in Node.js.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find a user by their email address.
     * Used during login to look up the user before verifying password.
     *
     * Spring generates query: db.users.findOne({ email: email })
     *
     * @param email the user's email
     * @return Optional<User> - present if found, empty if not
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user with the given email already exists.
     * Used during registration to prevent duplicate accounts.
     *
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}
