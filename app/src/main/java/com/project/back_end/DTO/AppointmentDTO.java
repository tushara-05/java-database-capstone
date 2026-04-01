package com.project.back_end.DTO;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import com.project.back_end.models.Appointment;

/**
 * Data Transfer Object (DTO) for sending appointment data to the frontend (client).
 *
 * <p>A DTO (Data Transfer Object) is a plain object that carries only the data
 * the client needs — without the full JPA entity relationships or sensitive fields.
 * This avoids issues like circular JSON serialization and over-exposure of data.</p>
 *
 * <p>Instead of sending the raw {@link Appointment} entity (which contains nested
 * Doctor and Patient objects), this DTO flattens the structure into simple fields
 * like {@code doctorName}, {@code patientEmail}, etc.</p>
 *
 * <p>It also includes derived/computed fields like:
 * <ul>
 *   <li>{@code appointmentDate} — just the date (no time)</li>
 *   <li>{@code appointmentTimeOnly} — just the time (no date)</li>
 *   <li>{@code endTime} — automatically set to 1 hour after the start time</li>
 * </ul>
 * </p>
 */
public class AppointmentDTO {

    /** The unique ID of the appointment. */
    private Long id;

    /** The ID of the doctor assigned to this appointment. */
    private Long doctorId;

    /** The full name of the doctor. */
    private String doctorName;

    /** The ID of the patient who booked this appointment. */
    private Long patientId;

    /** The full name of the patient. */
    private String patientName;

    /** The email address of the patient. */
    private String patientEmail;

    /** The phone number of the patient. */
    private String patientPhone;

    /** The home address of the patient. */
    private String patientAddress;

    /** The full date and time of the appointment (e.g., 2025-06-15T10:00:00). */
    private LocalDateTime appointmentTime;

    /**
     * The status of the appointment.
     * 0 = Scheduled, 1 = Completed.
     */
    private int status;

    /** The date portion of the appointment (derived from {@code appointmentTime}). */
    private LocalDate appointmentDate;

    /** The time portion of the appointment (derived from {@code appointmentTime}). */
    private LocalTime appointmentTimeOnly;

    /** The end time of the appointment — always 1 hour after the start time. */
    private LocalDateTime endTime;

    /**
     * Default no-argument constructor.
     * Required for JSON serialization/deserialization.
     */
    public AppointmentDTO() {
    }

    /**
     * Creates a fully populated {@code AppointmentDTO} with all appointment details.
     *
     * <p>Derived fields ({@code appointmentDate}, {@code appointmentTimeOnly}, {@code endTime})
     * are calculated automatically from the {@code appointmentTime} parameter.</p>
     *
     * @param id              the appointment ID
     * @param doctorId        the doctor's ID
     * @param doctorName      the doctor's full name
     * @param patientId       the patient's ID
     * @param patientName     the patient's full name
     * @param patientEmail    the patient's email address
     * @param patientPhone    the patient's phone number
     * @param patientAddress  the patient's home address
     * @param appointmentTime the date and time of the appointment
     * @param status          the appointment status (0 = Scheduled, 1 = Completed)
     */
    public AppointmentDTO(Long id,
                          Long doctorId,
                          String doctorName,
                          Long patientId,
                          String patientName,
                          String patientEmail,
                          String patientPhone,
                          String patientAddress,
                          LocalDateTime appointmentTime,
                          int status) {

        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;

        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientPhone = patientPhone;
        this.patientAddress = patientAddress;

        this.appointmentTime = appointmentTime;
        this.status = status;

        // Automatically compute derived fields from the given appointment time
        if (appointmentTime != null) {
            this.appointmentDate = appointmentTime.toLocalDate();
            this.appointmentTimeOnly = appointmentTime.toLocalTime();
            this.endTime = appointmentTime.plusHours(1);
        }
    }

    /**
     * Static factory method that creates an {@code AppointmentDTO} from a raw {@link Appointment} entity.
     *
     * <p>This is the preferred way to convert between the entity and the DTO.
     * It safely handles null doctor/patient references to avoid NullPointerExceptions.</p>
     *
     * @param appointment the {@link Appointment} entity to convert
     * @return a new {@code AppointmentDTO} containing the appointment's data,
     *         or {@code null} if the given appointment is null
     */
    public static AppointmentDTO fromEntity(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        
        return new AppointmentDTO(
            appointment.getId(),
            appointment.getDoctor() != null ? appointment.getDoctor().getId() : null,
            appointment.getDoctor() != null ? appointment.getDoctor().getName() : null,
            appointment.getPatient() != null ? appointment.getPatient().getId() : null,
            appointment.getPatient() != null ? appointment.getPatient().getName() : null,
            appointment.getPatient() != null ? appointment.getPatient().getEmail() : null,
            appointment.getPatient() != null ? appointment.getPatient().getPhone() : null,
            appointment.getPatient() != null ? appointment.getPatient().getAddress() : null,
            appointment.getAppointmentTime(),
            appointment.getStatus()
        );
    }

    // ─── Getters ───────────────────────────────────────────────────────────────

    /**
     * Returns the unique ID of this appointment.
     *
     * @return the appointment ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the ID of the doctor for this appointment.
     *
     * @return the doctor's ID
     */
    public Long getDoctorId() {
        return doctorId;
    }

    /**
     * Returns the name of the doctor for this appointment.
     *
     * @return the doctor's full name
     */
    public String getDoctorName() {
        return doctorName;
    }

    /**
     * Returns the ID of the patient for this appointment.
     *
     * @return the patient's ID
     */
    public Long getPatientId() {
        return patientId;
    }

    /**
     * Returns the name of the patient for this appointment.
     *
     * @return the patient's full name
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * Returns the email address of the patient.
     *
     * @return the patient's email
     */
    public String getPatientEmail() {
        return patientEmail;
    }

    /**
     * Returns the phone number of the patient.
     *
     * @return the patient's phone number
     */
    public String getPatientPhone() {
        return patientPhone;
    }

    /**
     * Returns the home address of the patient.
     *
     * @return the patient's address
     */
    public String getPatientAddress() {
        return patientAddress;
    }

    /**
     * Returns the full date and time of the appointment.
     *
     * @return the appointment's LocalDateTime
     */
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    /**
     * Returns the status of the appointment.
     * 0 = Scheduled, 1 = Completed.
     *
     * @return the status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * Returns the date portion of the appointment (without time).
     * Derived from {@code appointmentTime}.
     *
     * @return the appointment date
     */
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    /**
     * Returns the time portion of the appointment (without date).
     * Derived from {@code appointmentTime}.
     *
     * @return the appointment time
     */
    public LocalTime getAppointmentTimeOnly() {
        return appointmentTimeOnly;
    }

    /**
     * Returns the end time of the appointment.
     * This is always 1 hour after the start time.
     *
     * @return the appointment end time
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    // ─── Setters ───────────────────────────────────────────────────────────────

    /**
     * Sets the appointment ID.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the doctor's ID.
     *
     * @param doctorId the doctor ID to set
     */
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * Sets the doctor's name.
     *
     * @param doctorName the doctor name to set
     */
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    /**
     * Sets the patient's ID.
     *
     * @param patientId the patient ID to set
     */
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    /**
     * Sets the patient's name.
     *
     * @param patientName the patient name to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * Sets the patient's email address.
     *
     * @param patientEmail the email to set
     */
    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    /**
     * Sets the patient's phone number.
     *
     * @param patientPhone the phone number to set
     */
    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    /**
     * Sets the patient's address.
     *
     * @param patientAddress the address to set
     */
    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    /**
     * Sets the full appointment date and time.
     * Also automatically updates the derived fields:
     * {@code appointmentDate}, {@code appointmentTimeOnly}, and {@code endTime}.
     *
     * @param appointmentTime the date and time to set
     */
    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
        // Update derived fields when appointmentTime is set
        if (appointmentTime != null) {
            this.appointmentDate = appointmentTime.toLocalDate();
            this.appointmentTimeOnly = appointmentTime.toLocalTime();
            this.endTime = appointmentTime.plusHours(1);
        }
    }

    /**
     * Sets the appointment status.
     * Use 0 for scheduled and 1 for completed.
     *
     * @param status the status code to set
     */
    public void setStatus(int status) {
        this.status = status;
    }
}
