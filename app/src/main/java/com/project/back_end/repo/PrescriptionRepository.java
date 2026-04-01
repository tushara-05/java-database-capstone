package com.project.back_end.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Prescription;

/**
 * Repository interface for performing database operations on {@link Prescription} documents.
 *
 * <p>Unlike other repositories in this project (which use MySQL), this one
 * extends {@link MongoRepository} — making it a MongoDB repository. Prescriptions
 * are stored as documents in the "prescriptions" collection in MongoDB.</p>
 *
 * <p>Spring Data MongoDB automatically provides a working implementation of this interface
 * at runtime, so no manual implementation is needed.</p>
 */
@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    /**
     * Finds all prescriptions associated with a specific appointment ID.
     *
     * <p>Uses a MongoDB query to search for documents where the {@code appointmentId} field
     * matches the given value. The {@code ?0} placeholder refers to the first method parameter.</p>
     *
     * <p>Each appointment should have at most one prescription, but a list is returned
     * to handle any edge cases gracefully.</p>
     *
     * @param appointmentId the ID of the appointment (from the MySQL database)
     * @return a list of {@link Prescription} documents linked to the given appointment ID
     */
    @org.springframework.data.mongodb.repository.Query("{ 'appointmentId' : ?0 }")
    List<Prescription> findByAppointmentId(Long appointmentId);

}
