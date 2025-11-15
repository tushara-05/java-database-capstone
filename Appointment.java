package com.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Doctor doctor;

    @ManyToOne
    @NotNull
    private Patient patient;

    @NotNull
    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;

    @NotNull
    private Integer status;  // 0 = Scheduled, 1 = Completed

    @Size(max = 255)
    private String reasonForVisit;

    @Size(max = 255)
    private String notes;

    @Transient
    public LocalDateTime getEndTime() { return this.appointmentTime.plusHours(1); }

    @Transient
    public String getAppointmentDate() { return this.appointmentTime.toLocalDate().toString(); }

    @Transient
    public String getAppointmentTimeOnly() { return this.appointmentTime.toLocalTime().toString(); }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getReasonForVisit() { return reasonForVisit; }
    public void setReasonForVisit(String reasonForVisit) { this.reasonForVisit = reasonForVisit; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
