package com.project.back_end.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.ServiceClass;

/**
 * REST Controller that handles HTTP requests related to prescription operations.
 *
 * <p>This controller provides endpoints for doctors to issue prescriptions
 * after completing an appointment, and for both doctors and patients to
 * retrieve an existing prescription by appointment ID.</p>
 *
 * <p>When a prescription is saved, the associated appointment's status is
 * automatically updated to "completed" (status 1).</p>
 *
 * <p>All routes are prefixed with the configured API path followed by {@code /prescription}.</p>
 */
@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    /** Service for saving and retrieving prescriptions (stored in MongoDB). */
    private final PrescriptionService prescriptionService;

    /** Shared service for token validation and role-based access control. */
    private final ServiceClass service;

    /** Service used to update the appointment status after a prescription is issued. */
    private final AppointmentService appointmentService;

    /**
     * Constructor that injects all required service dependencies.
     *
     * @param prescriptionService the service for prescription operations
     * @param service             the shared service for token validation
     * @param appointmentService  the service for appointment status updates
     */
    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, ServiceClass service, AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    /**
     * Saves a new prescription for a specific appointment.
     *
     * <p>Endpoint: {@code POST /prescription/{token}}</p>
     *
     * <p>Only accessible by authenticated doctors (doctor token required).
     * After saving the prescription successfully, the appointment's status is automatically
     * updated to 1 (Completed) to indicate that the appointment has been fulfilled.</p>
     *
     * <p>If a prescription already exists for the given appointment, the save is rejected
     * with HTTP 400 (Bad Request) to prevent duplicates.</p>
     *
     * @param prescription the prescription data to save (from the request body);
     *                     must include a valid appointment ID, patient name, medication, and dosage
     * @param token        the doctor's JWT token (as a URL path variable)
     * @return a {@link ResponseEntity} with HTTP 201 and success message if saved,
     *         HTTP 400 if a prescription already exists for that appointment,
     *         HTTP 401 if the token is invalid,
     *         HTTP 500 if an internal error occurred
     */
    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(
            @Valid @RequestBody Prescription prescription,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        ResponseEntity<?> response = prescriptionService.savePrescription(prescription);
        if (response.getStatusCode().is2xxSuccessful()) {
            // Mark the appointment as completed when a prescription is issued
            appointmentService.changeStatus(prescription.getAppointmentId(), 1);
        }
        return response;
    }

    /**
     * Retrieves the prescription associated with a specific appointment.
     *
     * <p>Endpoint: {@code GET /prescription/{appointmentId}/{token}}</p>
     *
     * <p>Accessible by both authenticated doctors and patients.
     * The token is first validated for the "doctor" role; if that fails,
     * it is then validated for the "patient" role. If neither succeeds,
     * the request is rejected with HTTP 401.</p>
     *
     * @param appointmentId the ID of the appointment whose prescription to retrieve (path variable)
     * @param token         the JWT token of the requesting user (doctor or patient)
     * @return a {@link ResponseEntity} with the prescription data if found (HTTP 200),
     *         HTTP 404 if no prescription exists for that appointment,
     *         HTTP 401 if the token is invalid for both roles,
     *         HTTP 500 if an internal error occurred
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            // If not a doctor, check if the token belongs to a patient
            ResponseEntity<Map<String, String>> patientValidation = service.validateToken(token, "patient");
            if (!patientValidation.getStatusCode().is2xxSuccessful()) {
                return validation; // Return the original unauthorized error
            }
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}
