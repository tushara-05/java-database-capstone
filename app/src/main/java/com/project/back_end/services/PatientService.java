package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // -------------------- Create Patient --------------------
    @Transactional
    public boolean createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // -------------------- Get All Appointments for a Patient --------------------
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointments(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // -------------------- Filter Appointments by Condition (past/future) --------------------
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterAppointmentsByCondition(String condition, Long patientId) {
        int status;
        if ("past".equalsIgnoreCase(condition)) {
            status = 1;
        } else if ("future".equalsIgnoreCase(condition)) {
            status = 0;
        } else {
            throw new IllegalArgumentException("Invalid condition. Use 'past' or 'future'.");
        }

        List<Appointment> appointments = appointmentRepository
                .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status);

        return appointments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // -------------------- Filter Appointments by Doctor --------------------
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterAppointmentsByDoctor(String doctorName, Long patientId) {
        List<Appointment> appointments = appointmentRepository
                .filterByDoctorNameAndPatientId(doctorName, patientId);

        return appointments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // -------------------- Filter Appointments by Doctor and Condition --------------------
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterAppointmentsByDoctorAndCondition(String condition, String doctorName, Long patientId) {
        int status;
        if ("past".equalsIgnoreCase(condition)) {
            status = 1;
        } else if ("future".equalsIgnoreCase(condition)) {
            status = 0;
        } else {
            throw new IllegalArgumentException("Invalid condition. Use 'past' or 'future'.");
        }

        List<Appointment> appointments = appointmentRepository
                .filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status);

        return appointments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // -------------------- Get Patient Details by Token --------------------
    @Transactional(readOnly = true)
    public Patient getPatientDetails(String token) {
        String email = tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            throw new NoSuchElementException("Patient not found");
        }
        return patient;
    }

    // -------------------- Check if Patient Exists by Email or Phone --------------------
    @Transactional(readOnly = true)
    public boolean patientExists(String email, String phone) {
        return patientRepository.findByEmail(email) != null || patientRepository.findByPhone(phone) != null;
    }

    // -------------------- Helper: Map Appointment to DTO --------------------
    private AppointmentDTO mapToDTO(Appointment a) {
        return new AppointmentDTO(
                a.getId(),
                a.getDoctor().getId(),
                a.getDoctor().getName(),
                a.getPatient().getId(),
                a.getPatient().getName(),
                a.getPatient().getEmail(),
                a.getPatient().getPhone(),
                a.getPatient().getAddress(),
                a.getAppointmentTime(),
                a.getStatus()
        );
    }
}
