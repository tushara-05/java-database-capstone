package com.project.back_end.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.back_end.models.Doctor;

/**
 * Repository interface for performing database operations on {@link Doctor} entities.
 *
 * <p>This interface extends {@link JpaRepository}, giving it built-in CRUD operations
 * like save, findById, findAll, and delete. Custom queries are added to support
 * filtering doctors by name and speciality.</p>
 *
 * <p>Doctor data is stored in the MySQL (relational) database.</p>
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Finds a doctor by their exact email address.
     *
     * <p>Used during doctor login to retrieve the doctor's record
     * for credential verification.</p>
     *
     * @param email the email address to search
     * @return the {@link Doctor} with the given email, or {@code null} if not found
     */
    Doctor findByEmail(String email);

    /**
     * Finds all doctors whose name contains the given keyword (case-sensitive partial match).
     *
     * <p>Uses a JPQL query with a {@code LIKE} clause to match any doctor name
     * that includes the given string anywhere within it.</p>
     *
     * <p>Example: searching "car" would match "Dr. Richard Carver".</p>
     *
     * @param name the keyword to search for within doctor names
     * @return a list of {@link Doctor} objects whose names contain the given keyword
     */
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(@Param("name") String name);

    /**
     * Finds all doctors whose name contains a given keyword AND whose speciality matches exactly,
     * both in a case-insensitive way.
     *
     * <p>Useful for filtered searches — for example, finding all "cardiologists" named "Smith".</p>
     *
     * @param name      the keyword to search within doctor names (case-insensitive partial match)
     * @param speciality the speciality to match exactly (case-insensitive)
     * @return a list of {@link Doctor} objects that match both the name and speciality criteria
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND LOWER(d.speciality) = LOWER(:speciality)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(@Param("name") String name, @Param("speciality") String speciality);

    /**
     * Finds all doctors that belong to a specific speciality, ignoring letter case.
     *
     * <p>For example, searching "cardiology" would match doctors with speciality
     * "Cardiology", "CARDIOLOGY", or "cardiology".</p>
     *
     * @param speciality the medical speciality to filter by (case-insensitive)
     * @return a list of {@link Doctor} objects with the given speciality
     */
    List<Doctor> findBySpecialityIgnoreCase(String speciality);

}
