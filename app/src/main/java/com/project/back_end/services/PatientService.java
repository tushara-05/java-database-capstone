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
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

/**
 * Service class that handles all business logic related to patients.
 *
 * <p>This class is responsible for registering patients, fetching patient details,
 * and retrieving or filtering a patient's appointment history.</p>
 *
 * <p>The {@code @Service} annotation tells Spring to manage this class as a bean,
 * allowing it to be automatically injected wherever it is needed.</p>
 */
@Service
public class PatientService {

    /** Repository for performing CRUD operations on patient records. */
    private final PatientRepository patientRepository;

    /** Repository for querying appointment records. */
    private final AppointmentRepository appointmentRepository;

    /** Repository for looking up doctor records (used for authorization checks). */
    private final DoctorRepository doctorRepository;

    /** Service for generating and extracting JWT tokens. */
    private final TokenService tokenService;

    /**
     * Constructor that injects all required dependencies.
     *
     * @param patientRepository     the repository for patient data
     * @param appointmentRepository the repository for appointment data
     * @param doctorRepository      the repository for doctor data
     * @param tokenService          the service for token operations
     */
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

    /**
     * Registers a new patient by saving them to the database.
     *
     * @param patient the patient to create
     * @return {@code 1} if the patient was created successfully,
     *         {@code 0} if an error occurred during saving
     */
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

    /**
     * Retrieves all appointments for a specific patient, with authorization checks.
     *
     * <p>The token is used to determine who is making the request:</p>
     * <ul>
     *   <li>If the token belongs to a patient, they can only view THEIR OWN appointments
     *       (their ID must match the given {@code id}).</li>
     *   <li>If the token belongs to a doctor, they are allowed to view any patient's appointments.</li>
     * </ul>
     *
     * <p>Results are returned as {@link AppointmentDTO} objects to avoid exposing
     * sensitive entity data like passwords.</p>
     *
     * @param id    the ID of the patient whose appointments should be retrieved
     * @param token the JWT token of the requesting user (patient or doctor)
     * @return a {@link ResponseEntity} with a list of {@link AppointmentDTO} and count,
     *         or an error response if access is denied or an error occurs
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(email);
            Doctor doctor = doctorRepository.findByEmail(email);
            
            // The method checks if the token belongs to the patient OR a valid doctor
            if (doctor == null && (patient == null || !patient.getId().equals(id))) {
                response.put("error", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            List<Appointment> appointments = appointmentRepository.findByPatient_Id(id);
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

    /**
     * Filters a patient's appointments based on a time condition.
     *
     * <p>Supported condition values (case-insensitive):</p>
     * <ul>
     *   <li>{@code "past"} or {@code "completed"} — returns past appointments</li>
     *   <li>{@code "future"}, {@code "upcoming"}, or {@code "scheduled"} — returns upcoming appointments</li>
     *   <li>{@code "all"}, {@code "allAppointments"}, or {@code ""} — returns all appointments</li>
     * </ul>
     *
     * @param condition the filter condition string
     * @param id        the ID of the patient
     * @return a {@link ResponseEntity} with the filtered list of {@link AppointmentDTO} and count,
     *         or an error response if the condition is invalid or an error occurs
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments;
            if ("past".equalsIgnoreCase(condition) || "completed".equalsIgnoreCase(condition)) {
                appointments = appointmentRepository.findPastAppointmentsByPatientId(id);
            } else if ("future".equalsIgnoreCase(condition) || "upcoming".equalsIgnoreCase(condition) || "scheduled".equalsIgnoreCase(condition)) {
                appointments = appointmentRepository.findUpcomingAppointmentsByPatientId(id);
            } else if ("all".equalsIgnoreCase(condition) || "allAppointments".equalsIgnoreCase(condition) || "".equals(condition)) {
                appointments = appointmentRepository.findByPatient_Id(id);
            } else {
                response.put("error", "Invalid condition. Use 'past', 'future', 'upcoming', 'all' or 'allAppointments'.");
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

    /**
     * Filters a patient's appointments by the doctor's name.
     *
     * <p>Performs a case-insensitive partial match on the doctor's name,
     * returning all of the patient's appointments where the doctor's name
     * contains the given keyword.</p>
     *
     * @param name      a partial or full name of the doctor to filter by
     * @param patientId the ID of the patient
     * @return a {@link ResponseEntity} with the filtered list of {@link AppointmentDTO} and count,
     *         or an error response if something goes wrong
     */
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

    /**
     * Filters a patient's appointments by both the doctor's name and a time condition.
     *
     * <p>Combines name filtering and condition filtering (past/future/all).
     * Supported condition values are the same as in {@link #filterByCondition(String, Long)}.</p>
     *
     * @param condition the time-based filter ("past", "future", "all", etc.)
     * @param name      a partial or full name of the doctor to filter by
     * @param patientId the ID of the patient
     * @return a {@link ResponseEntity} with the combined filtered list and count,
     *         or an error response if something goes wrong
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments;
            if ("past".equalsIgnoreCase(condition) || "completed".equalsIgnoreCase(condition)) {
                appointments = appointmentRepository.filterPastByDoctorNameAndPatientId(name, patientId);
            } else if ("future".equalsIgnoreCase(condition) || "upcoming".equalsIgnoreCase(condition) || "scheduled".equalsIgnoreCase(condition)) {
                appointments = appointmentRepository.filterUpcomingByDoctorNameAndPatientId(name, patientId);
            } else if ("all".equalsIgnoreCase(condition) || "allAppointments".equalsIgnoreCase(condition) || "".equals(condition)) {
                appointments = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);
            } else {
                response.put("error", "Invalid condition. Use 'past', 'future', 'upcoming', 'all' or 'allAppointments'.");
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

    /**
     * Retrieves the full profile details of a patient based on their JWT token.
     *
     * <p>Extracts the patient's email from the token, looks them up in the database,
     * and returns their details. Returns a 404 error if the patient is not found.</p>
     *
     * @param token the JWT token of the logged-in patient
     * @return a {@link ResponseEntity} with the patient's information,
     *         or an error response if the patient is not found or an error occurs
     */
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
