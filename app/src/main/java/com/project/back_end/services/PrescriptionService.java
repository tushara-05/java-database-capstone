package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    // -------------------- Save Prescription --------------------
    @Transactional
    public boolean savePrescription(Prescription prescription) {
        try {
            // Check if prescription already exists
            List<Prescription> existing =
                    prescriptionRepository.findByAppointmentId(
                            prescription.getAppointmentId());

            if (!existing.isEmpty()) {
                return false; // already exists
            }

            prescriptionRepository.save(prescription);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // -------------------- Get Prescription By Appointment ID --------------------
    @Transactional(readOnly = true)
    public Prescription getPrescription(Long appointmentId) {

        List<Prescription> prescriptions =
                prescriptionRepository.findByAppointmentId(appointmentId);

        if (prescriptions.isEmpty()) {
            return null;
        }

        // assuming one prescription per appointment
        return prescriptions.get(0);
    }
}
