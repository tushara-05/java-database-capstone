package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointment")
public class Appointment {

    // -------------------- Fields --------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many appointments can be linked to one doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor cannot be null")
    private Doctor doctor;

    // Many appointments can be linked to one patient
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient cannot be null")
    private Patient patient;

    // Appointment date and time, must be in the future
    @NotNull(message = "Appointment time cannot be null")
    @Future(message = "Appointment time must be in the future")
    @Column(name = "appointment_time", nullable = false)
    private LocalDateTime appointmentTime;

    // Status: 0 = Scheduled, 1 = Completed
    @NotNull(message = "Status cannot be null")
    @Column(nullable = false)
    private int status;

    // -------------------- Constructors --------------------

    public Appointment() {
        // No-args constructor required by JPA
    }

    public Appointment(Doctor doctor, Patient patient, LocalDateTime appointmentTime, int status) {
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // -------------------- Getters & Setters --------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // -------------------- Helper Methods --------------------

    // Returns the end time (1 hour after start)
    @Transient
    public LocalDateTime getEndTime() {
        return this.appointmentTime != null ? this.appointmentTime.plusHours(1) : null;
    }

    // Returns only the date part
    @Transient
    public LocalDate getAppointmentDate() {
        return this.appointmentTime != null ? this.appointmentTime.toLocalDate() : null;
    }

    // Returns only the time part
    @Transient
    public LocalTime getAppointmentTimeOnly() {
        return this.appointmentTime != null ? this.appointmentTime.toLocalTime() : null;
    }
}
