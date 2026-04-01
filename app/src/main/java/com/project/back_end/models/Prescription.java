package com.project.back_end.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a Prescription issued by a doctor to a patient after an appointment.
 *
 * <p>Unlike other entities (Admin, Doctor, Patient, Appointment) which are stored in
 * MySQL (relational database), prescriptions are stored in <b>MongoDB</b> (a NoSQL database).
 * The collection name in MongoDB is "prescriptions".</p>
 *
 * <p>A prescription is always linked to a specific appointment via the {@code appointmentId}.
 * It contains the medication name, dosage instructions, and optional notes from the doctor.</p>
 */
@Document(collection = "prescriptions")
public class Prescription {

    /**
     * The unique identifier for this prescription in MongoDB.
     * MongoDB automatically generates this as a string (ObjectId).
     */
    @Id
    private String id;

    /**
     * The name of the patient who received the prescription.
     * Must be between 3 and 100 characters and cannot be null.
     */
    @NotNull(message = "Patient name cannot be null")
    @Size(min = 3, max = 100, message = "Patient name must be between 3 and 100 characters")
    private String patientName;

    /**
     * The ID of the appointment this prescription is associated with.
     * Links this MongoDB document to the corresponding appointment in the MySQL database.
     * Cannot be null.
     */
    @NotNull(message = "Appointment ID cannot be null")
    private Long appointmentId;

    /**
     * The name of the medication prescribed to the patient.
     * Must be between 3 and 100 characters and cannot be null.
     */
    @NotNull(message = "Medication cannot be null")
    @Size(min = 3, max = 100, message = "Medication name must be between 3 and 100 characters")
    private String medication;

    /**
     * The dosage instructions for the prescribed medication (e.g., "1 tablet twice daily").
     * Must be between 3 and 20 characters and cannot be null.
     */
    @NotNull(message = "Dosage cannot be null")
    @Size(min = 3, max = 20, message = "Dosage must be between 3 and 20 characters")
    private String dosage;

    /**
     * Optional additional notes from the doctor regarding this prescription.
     * For example: "Take after meals" or "Avoid alcohol".
     * Can be null or empty. Maximum 200 characters.
     */
    @Size(max = 200, message = "Doctor notes cannot exceed 200 characters")
    private String doctorNotes;

    /**
     * No-argument constructor required by MongoDB.
     * MongoDB uses this to create Prescription instances when loading documents.
     */
    public Prescription() {
    }

    /**
     * Creates a new Prescription with all the required details.
     *
     * @param patientName   the name of the patient receiving the prescription
     * @param appointmentId the ID of the appointment linked to this prescription
     * @param medication    the name of the prescribed medication
     * @param dosage        the dosage instructions (e.g., "500mg twice a day")
     * @param doctorNotes   optional notes from the doctor (can be null)
     */
    public Prescription(String patientName, Long appointmentId, String medication, String dosage, String doctorNotes) {
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.doctorNotes = doctorNotes;
    }

    /**
     * Returns the unique MongoDB ID of this prescription.
     *
     * @return the prescription's MongoDB document ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique MongoDB ID of this prescription.
     *
     * @param id the ID to assign
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the name of the patient who received this prescription.
     *
     * @return the patient's name
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * Sets the name of the patient who received this prescription.
     *
     * @param patientName the patient name to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * Returns the ID of the appointment linked to this prescription.
     *
     * @return the appointment's ID from the MySQL database
     */
    public Long getAppointmentId() {
        return appointmentId;
    }

    /**
     * Sets the appointment ID for this prescription.
     *
     * @param appointmentId the appointment ID to link
     */
    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * Returns the name of the medication prescribed.
     *
     * @return the medication name
     */
    public String getMedication() {
        return medication;
    }

    /**
     * Sets the medication name for this prescription.
     *
     * @param medication the medication name to set
     */
    public void setMedication(String medication) {
        this.medication = medication;
    }

    /**
     * Returns the dosage instructions for this prescription.
     *
     * @return the dosage string (e.g., "500mg twice daily")
     */
    public String getDosage() {
        return dosage;
    }

    /**
     * Sets the dosage instructions for this prescription.
     *
     * @param dosage the dosage to set
     */
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    /**
     * Returns the optional doctor's notes for this prescription.
     *
     * @return the doctor's notes, or null if none were provided
     */
    public String getDoctorNotes() {
        return doctorNotes;
    }

    /**
     * Sets the doctor's notes for this prescription.
     *
     * @param doctorNotes the notes to set (can be null)
     */
    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}
