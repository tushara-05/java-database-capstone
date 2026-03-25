package com.project.back_end.DTO;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import com.project.back_end.models.Appointment;

public class AppointmentDTO {

    private Long id;

    private Long doctorId;
    private String doctorName;

    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String patientAddress;

    private LocalDateTime appointmentTime;

    private int status;

    private LocalDate appointmentDate;
    private LocalTime appointmentTimeOnly;
    private LocalDateTime endTime;

    // Default constructor
    public AppointmentDTO() {
    }

    // Constructor
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

        // Derived fields
        if (appointmentTime != null) {
            this.appointmentDate = appointmentTime.toLocalDate();
            this.appointmentTimeOnly = appointmentTime.toLocalTime();
            this.endTime = appointmentTime.plusHours(1);
        }
    }

    /**
     * Create an AppointmentDTO from an Appointment entity
     * @param appointment The Appointment entity
     * @return AppointmentDTO instance
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

    // Getters

    public Long getId() {
        return id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTimeOnly() {
        return appointmentTimeOnly;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
        // Update derived fields when appointmentTime is set
        if (appointmentTime != null) {
            this.appointmentDate = appointmentTime.toLocalDate();
            this.appointmentTimeOnly = appointmentTime.toLocalTime();
            this.endTime = appointmentTime.plusHours(1);
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
