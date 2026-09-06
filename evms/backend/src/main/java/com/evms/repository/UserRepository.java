package com.evms.repository;

import com.evms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 * <p>
 * Provides standard CRUD operations plus a custom finder method
 * for looking up users by their email address during login.
 *
 * @author EVMS Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address (case-sensitive).
     *
     * @param email the email to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
}
