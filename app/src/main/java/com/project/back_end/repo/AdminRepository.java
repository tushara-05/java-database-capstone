package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Admin;

/**
 * Repository interface for performing database operations on {@link Admin} entities.
 *
 * <p>This interface extends {@link JpaRepository}, which provides built-in methods
 * for common database operations such as save, findById, findAll, and delete —
 * without needing to write any SQL queries manually.</p>
 *
 * <p>Spring Data JPA automatically provides a working implementation of this interface
 * at runtime, so no concrete class is needed.</p>
 *
 * <p>The Admin data is stored in the MySQL (relational) database.</p>
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Finds an admin by their username.
     *
     * <p>Spring Data JPA automatically generates the query based on the method name.
     * It looks for an admin record where the {@code username} field matches the given value.</p>
     *
     * @param username the username to search for
     * @return the {@link Admin} with the given username, or {@code null} if not found
     */
    Admin findByUsername(String username);

}
