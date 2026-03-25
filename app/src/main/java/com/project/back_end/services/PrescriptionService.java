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

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /** Save a new prescription */
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

    /** Get prescription by appointment ID */
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
