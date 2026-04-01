package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Patient;

/**
 * Repository interface for performing database operations on {@link Patient} entities.
 *
 * <p>This interface extends {@link JpaRepository}, which provides built-in methods
 * for common database operations such as save, findById, findAll, and delete —
 * without any manual SQL.</p>
 *
 * <p>Spring Data JPA automatically creates a working implementation of this interface
 * at runtime. Patient data is stored in the MySQL (relational) database.</p>
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Finds a patient by their email address.
     *
     * <p>Spring Data JPA generates the query automatically from the method name.
     * Used during patient login to look up the patient record by email.</p>
     *
     * @param email the patient's email address
     * @return the {@link Patient} with the given email, or {@code null} if not found
     */
    Patient findByEmail(String email);

    /**
     * Finds a patient by either their email address or phone number.
     *
     * <p>Used to check for duplicate registrations — to ensure that no two patients
     * share the same email or phone number in the system.</p>
     *
     * @param email the email address to check
     * @param phone the phone number to check
     * @return the {@link Patient} matching either value, or {@code null} if none found
     */
    Patient findByEmailOrPhone(String email, String phone);
}
