package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
 * Service class that contains all the business logic related to appointments.
 *
 * <p>This class handles booking, updating, canceling, and retrieving appointments.
 * It sits between the Controller layer (which receives HTTP requests) and the
 * Repository layer (which talks to the database).</p>
 *
 * <p>The {@code @Service} annotation marks this class as a Spring-managed service bean,
 * meaning Spring will automatically create and manage an instance of this class.</p>
 */
@Service
public class AppointmentService {

    /** Repository used to perform database operations on appointments. */
    private final AppointmentRepository appointmentRepository;

    /** Repository used to look up patient information. */
    private final PatientRepository patientRepository;

    /** Repository used to look up doctor information. */
    private final DoctorRepository doctorRepository;

    /** Service used to generate and extract JWT tokens for authentication. */
    private final TokenService tokenService;

    /** General service class used for shared validation logic. */
    private final ServiceClass service;

    /**
     * Constructor that injects all required dependencies into this service.
     *
     * <p>Spring uses this constructor to automatically provide all the required objects
     * (a process called "dependency injection").</p>
     *
     * @param appointmentRepository the repository for appointment data
     * @param patientRepository     the repository for patient data
     * @param doctorRepository      the repository for doctor data
     * @param tokenService          the service used for token operations
     * @param service               the shared general-purpose service
     */
    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService,
                              ServiceClass service) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    /**
     * Books (saves) a new appointment in the database.
     *
     * <p>Before saving, this method checks if the appointment time is in the past —
     * if so, the booking is rejected. If the save is successful, it returns 1;
     * otherwise it returns 0.</p>
     *
     * @param appointment the appointment to be booked
     * @return {@code 1} if the appointment was booked successfully,
     *         {@code 0} if the appointment time is in the past or an error occurred
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            if (appointment.getAppointmentTime() != null && appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
                return 0; // Appointment time should be in the present or future
            }
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Updates an existing appointment with new details.
     *
     * <p>This method performs several validations before updating:</p>
     * <ul>
     *   <li>Checks if the appointment exists in the database</li>
     *   <li>Ensures the patient ID matches the one on the existing record (security check)</li>
     *   <li>Validates that the new appointment time is not in the past</li>
     *   <li>Confirms the doctor is available at the new time</li>
     * </ul>
     *
     * @param appointment the appointment with updated data (must include the correct ID)
     * @return a {@link ResponseEntity} with a success message if updated,
     *         or an error message explaining what went wrong
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
        if (!existingOpt.isPresent()) {
            response.put("error", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }
        Appointment existing = existingOpt.get();
        if (appointment.getPatient() == null) {
            response.put("error", "Patient cannot be null");
            return ResponseEntity.badRequest().body(response);
        }
        Long existingPatientId = (existing.getPatient() != null) ? existing.getPatient().getId() : null;
        Long incomingPatientId = appointment.getPatient().getId();
        if (existingPatientId == null || !existingPatientId.equals(incomingPatientId)) {
            response.put("error", "Patient ID mismatch");
            return ResponseEntity.badRequest().body(response);
        }
        if (appointment.getAppointmentTime() != null && appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            response.put("error", "Appointment time should be in the present or future");
            return ResponseEntity.badRequest().body(response);
        }
        int validationResult = service.validateAppointment(appointment);
        if (validationResult == -1) {
            response.put("error", "Doctor does not exist");
            return ResponseEntity.badRequest().body(response);
        } else if (validationResult == 0) {
            response.put("error", "Appointment time unavailable");
            return ResponseEntity.badRequest().body(response);
        }
        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());
        existing.setDoctor(appointment.getDoctor());
        try {
            appointmentRepository.save(existing);
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to update appointment");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Cancels (deletes) an appointment.
     *
     * <p>This method first verifies that the patient who owns the token is the same
     * patient who booked the appointment. If not, the cancellation is rejected with
     * an "Unauthorized" error. Otherwise, the appointment is deleted from the database.</p>
     *
     * @param id    the ID of the appointment to cancel
     * @param token the JWT token of the patient requesting the cancellation
     * @return a {@link ResponseEntity} with a success message if canceled,
     *         or an error message if the appointment wasn't found or the user is unauthorized
     */
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> existingOpt = appointmentRepository.findById(id);
        if (!existingOpt.isPresent()) {
            response.put("error", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }
        Appointment appointment = existingOpt.get();
        String email = tokenService.extractIdentifier(token);
        Patient patientSession = patientRepository.findByEmail(email);
        Long patientId = patientSession != null ? patientSession.getId() : null;
        Long appointmentPatientId = (appointment.getPatient() != null) ? appointment.getPatient().getId() : null;
        if (patientId == null || !patientId.equals(appointmentPatientId)) {
            response.put("error", "Unauthorized to cancel this appointment");
            return ResponseEntity.status(401).body(response);
        }
        try {
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment canceled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to cancel appointment");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Retrieves all appointments for a specific doctor on a given date.
     *
     * <p>The doctor is identified from the JWT token. Optionally, results can be
     * filtered by a patient's name. If a patient name is provided, appointments
     * matching that name are returned across all dates; otherwise, all appointments
     * for the given date are returned.</p>
     *
     * <p>Results are returned as a list of {@link AppointmentDTO} objects to avoid
     * exposing sensitive entity data.</p>
     *
     * @param pname the patient's name to filter by (can be null or empty for no filter)
     * @param date  the date for which to retrieve appointments
     * @param token the JWT token of the logged-in doctor
     * @return a map containing a list of {@link AppointmentDTO} objects and a count
     */
    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();
        String email = tokenService.extractIdentifier(token);
        Doctor doctor = doctorRepository.findByEmail(email);
        Long doctorId = doctor != null ? doctor.getId() : null;
        
        if (doctorId == null) {
            result.put("error", "Unauthorized");
            return result;
        }
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        List<Appointment> appointments;
        if (pname != null && !pname.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCase(
                    doctorId, pname);
        } else {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        }
        List<AppointmentDTO> dtoList = (appointments != null) ? 
            appointments.stream().map(AppointmentDTO::fromEntity).collect(Collectors.toList()) : 
            new ArrayList<>();
            
        result.put("appointments", dtoList);
        result.put("count", dtoList.size());
        return result;
    }

    /**
     * Changes the status of an appointment to the given integer value.
     *
     * <p>This is a low-level method that directly updates the status field in the database.
     * Use {@link #updateAppointmentStatus(Long, String)} if you want to use string status names.</p>
     *
     * @param appointmentId the ID of the appointment to update
     * @param status        the new status value (0 = Scheduled, 1 = Completed)
     */
    @Transactional
    public void changeStatus(long appointmentId, int status) {
        appointmentRepository.updateStatus(appointmentId, status);
    }

    /**
     * Updates the status of a specific appointment using a human-readable status string.
     *
     * <p>Accepts the following status strings (case-insensitive):</p>
     * <ul>
     *   <li>{@code "scheduled"} → sets status to 0</li>
     *   <li>{@code "completed"} → sets status to 1</li>
     *   <li>{@code "prescribed"} → sets status to 1 (same as completed)</li>
     *   <li>Any other value → defaults to status 0</li>
     * </ul>
     *
     * @param appointmentId the ID of the appointment to update
     * @param status        a string representing the new status ("scheduled", "completed", "prescribed")
     */
    @Transactional
    public void updateAppointmentStatus(Long appointmentId, String status) {
        int statusValue;
        switch (status.toLowerCase()) {
            case "scheduled":
                statusValue = 0;
                break;
            case "completed":
                statusValue = 1;
                break;
            case "prescribed":
                statusValue = 1; // Completed status covers Prescribed
                break;
            default:
                statusValue = 0;
        }
        appointmentRepository.updateStatus(appointmentId, statusValue);
    }
}
