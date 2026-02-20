package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.ServiceClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final ServiceClass service;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  ServiceClass service,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    // -------------------- Save Prescription --------------------
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @RequestBody Prescription prescription,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "doctor");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        boolean saved = prescriptionService.savePrescription(prescription);

        if (!saved) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to save prescription"));
        }

        boolean updated = appointmentService
                .changeStatus(prescription.getAppointmentId(), 1);

        if (!updated) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message",
                            "Prescription saved but failed to update appointment status"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Prescription saved successfully"));
    }

    // -------------------- Get Prescription by Appointment ID --------------------
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tokenValidation =
                service.validateToken(token, "doctor");

        if (!tokenValidation.getStatusCode().is2xxSuccessful()) {
            return tokenValidation;
        }

        Prescription prescription =
                prescriptionService.getPrescription(appointmentId);

        if (prescription == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message",
                            "No prescription found for this appointment"));
        }

        return ResponseEntity.ok(Map.of("prescription", prescription));
    }
}
