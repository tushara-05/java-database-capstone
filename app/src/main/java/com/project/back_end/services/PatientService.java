package com.project.back_end.services;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          DoctorRepository doctorRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    /** Create a new patient */
    @Transactional
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** Get all appointments for a patient by ID and token */
    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(email);
            
            // The method checks if the provided patient ID matches the one decoded from the token
            if (patient == null || !patient.getId().equals(id)) {
                response.put("error", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }

            List<Appointment> appointments = appointmentRepository.findByPatientId(id);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            response.put("count", appointmentDTOs.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error fetching appointments");
            return ResponseEntity.status(500).body(response);
        }
    }

    /** Filter appointments by condition (past/future) based on actual appointment date */
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments;
            // The method uses the status (1 for past and 0 for future) to determine the filtering criteria.
            if ("past".equalsIgnoreCase(condition)) {
                appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, 1);
            } else if ("future".equalsIgnoreCase(condition)) {
                appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, 0);
            } else {
                response.put("error", "Invalid condition. Use 'past' or 'future'.");
                return ResponseEntity.badRequest().body(response);
            }

            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());
            response.put("appointments", dtoList);
            response.put("count", dtoList.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error filtering appointments");
            return ResponseEntity.status(500).body(response);
        }
    }

    /** Filter appointments by doctor name */
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);
            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());
            response.put("appointments", dtoList);
            response.put("count", dtoList.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error filtering by doctor");
            return ResponseEntity.status(500).body(response);
        }
    }

    /** Filter appointments by doctor name and condition (past/future) based on actual appointment date */
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments;
            // Combines the filtering criteria of both the doctor's name and the condition (past or future).
            // Uses status 1 for past and 0 for future as per requirements.
            if ("past".equalsIgnoreCase(condition)) {
                appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, 1);
            } else if ("future".equalsIgnoreCase(condition)) {
                appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, 0);
            } else {
                response.put("error", "Invalid condition. Use 'past' or 'future'.");
                return ResponseEntity.badRequest().body(response);
            }

            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());
            response.put("appointments", dtoList);
            response.put("count", dtoList.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error filtering by doctor and condition");
            return ResponseEntity.status(500).body(response);
        }
    }

    /** Get patient details by token */
    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(404).body(response);
            }
            response.put("patient", patient);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error fetching patient details");
            return ResponseEntity.status(500).body(response);
        }
    }
}
