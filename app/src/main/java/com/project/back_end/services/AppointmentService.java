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
// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.
@Service
public class AppointmentService {
// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final ServiceClass service;
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
// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.
// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.
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
// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.
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
// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.
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
// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.
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
// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.
    @Transactional
    public void changeStatus(long appointmentId, int status) {
        appointmentRepository.updateStatus(appointmentId, status);
    }
    
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
