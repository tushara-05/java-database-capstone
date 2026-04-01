package com.project.back_end.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

/**
 * Service class that handles all business logic related to prescriptions.
 *
 * <p>This class provides operations to save a new prescription and retrieve
 * an existing prescription by its associated appointment ID.</p>
 *
 * <p>Prescriptions are stored in <b>MongoDB</b> (unlike most other entities
 * which use MySQL), so this service interacts with {@link PrescriptionRepository}
 * which extends {@code MongoRepository}.</p>
 *
 * <p>The {@code @Service} annotation tells Spring to automatically manage
 * this class as a Spring bean.</p>
 */
@Service
public class PrescriptionService {

    /** MongoDB repository for saving and retrieving prescription documents. */
    private final PrescriptionRepository prescriptionRepository;

    /**
     * Constructor that injects the prescription repository dependency.
     *
     * @param prescriptionRepository the repository for prescription data
     */
    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * Saves a new prescription to MongoDB.
     *
     * <p>Before saving, this method checks whether a prescription already exists
     * for the given appointment ID. If one already exists, the save is rejected
     * to prevent duplicate prescriptions for the same appointment.</p>
     *
     * @param prescription the prescription to save (must include a valid appointment ID)
     * @return a {@link ResponseEntity} with HTTP 201 and a success message if saved,
     *         HTTP 400 if a prescription already exists for the appointment,
     *         or HTTP 500 if an internal error occurred
     */
    @Transactional
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        try {
            // Check if a prescription already exists for the appointment
            List<Prescription> existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (existing != null && !existing.isEmpty()) {
                response.put("error", "Prescription already exists for this appointment");
                return ResponseEntity.badRequest().body(response);
            }

            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Retrieves the prescription associated with a given appointment ID.
     *
     * <p>Since each appointment should have at most one prescription, only the
     * first result is returned. Returns a 404 error if no prescription is found.</p>
     *
     * @param appointmentId the ID of the appointment whose prescription to retrieve
     * @return a {@link ResponseEntity} with the prescription data if found,
     *         HTTP 404 if no prescription exists for the appointment,
     *         or HTTP 500 if an internal error occurred
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescriptions != null && !prescriptions.isEmpty()) {
                response.put("prescription", prescriptions.get(0));
                return ResponseEntity.ok(response);
            }
            response.put("error", "Prescription not found for this appointment");
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }
}
