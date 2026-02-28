Appointment model
The Appointment model represents a scheduled meeting between a doctor and a patient. It includes metadata such as the date, time, status of the appointment, and helper methods to extract specific time-based information. This model links together the Doctor and Patient entities to form the core of the clinic's scheduling system.

Open the Appointment.java file src/main/java/com/project/back_end/models/Appointment.java
Open Appointment.java in IDE

Add the following attributes along with getters and setters:
id: private Long – Auto-incremented primary key
doctor: private Doctor – The doctor assigned to the appointment (required)
patient: private Patient – The patient assigned to the appointment (required)
appointmentTime: private LocalDateTime – The date and time of the appointment (must be in the future)
status: private int – Status of the appointment (0 for Scheduled, 1 for Completed) (required)
Add helper methods:
getEndTime(): Returns the end time of the appointment (1 hour after start time)
getAppointmentDate(): Returns only the date portion of the appointment
getAppointmentTimeOnly(): Returns only the time portion of the appointment

Hints
Annotate the class with @Entity to indicate that it should be mapped to a JPA table.
Mark id with:
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)

Use @ManyToOne and @NotNull to define relationships to both Doctor and Patient.
Apply @Future on appointmentTime:
@Future(message = "Appointment time must be in the future")

Mark the helper methods with @Transient so they aren't persisted in the database.
Add standard getters and setters for all fields.

Tasks
Establish proper relationships between entities
Implement custom methods to support UI display logic
Validate appointment timing to prevent past scheduling

package com.project.back_end.models;

public class Appointment {

// @Entity annotation:
//    - Marks the class as a JPA entity, meaning it represents a table in the database.
//    - Required for persistence frameworks (e.g., Hibernate) to map the class to a database table.

// 1. 'id' field:
//    - Type: private Long
//    - Description:
//      - Represents the unique identifier for each appointment.
//      - The @Id annotation marks it as the primary key.
//      - The @GeneratedValue(strategy = GenerationType.IDENTITY) annotation auto-generates the ID value when a new record is inserted into the database.

// 2. 'doctor' field:
//    - Type: private Doctor
//    - Description:
//      - Represents the doctor assigned to this appointment.
//      - The @ManyToOne annotation defines the relationship, indicating many appointments can be linked to one doctor.
//      - The @NotNull annotation ensures that an appointment must be associated with a doctor when created.

// 3. 'patient' field:
//    - Type: private Patient
//    - Description:
//      - Represents the patient assigned to this appointment.
//      - The @ManyToOne annotation defines the relationship, indicating many appointments can be linked to one patient.
//      - The @NotNull annotation ensures that an appointment must be associated with a patient when created.

// 4. 'appointmentTime' field:
//    - Type: private LocalDateTime
//    - Description:
//      - Represents the date and time when the appointment is scheduled to occur.
//      - The @Future annotation ensures that the appointment time is always in the future when the appointment is created.
//      - It uses LocalDateTime, which includes both the date and time for the appointment.

// 5. 'status' field:
//    - Type: private int
//    - Description:
//      - Represents the current status of the appointment. It is an integer where:
//        - 0 means the appointment is scheduled.
//        - 1 means the appointment has been completed.
//      - The @NotNull annotation ensures that the status field is not null.

// 6. 'getEndTime' method:
//    - Type: private LocalDateTime
//    - Description:
//      - This method is a transient field (not persisted in the database).
//      - It calculates the end time of the appointment by adding one hour to the start time (appointmentTime).
//      - It is used to get an estimated appointment end time for display purposes.

// 7. 'getAppointmentDate' method:
//    - Type: private LocalDate
//    - Description:
//      - This method extracts only the date part from the appointmentTime field.
//      - It returns a LocalDate object representing just the date (without the time) of the scheduled appointment.

// 8. 'getAppointmentTimeOnly' method:
//    - Type: private LocalTime
//    - Description:
//      - This method extracts only the time part from the appointmentTime field.
//      - It returns a LocalTime object representing just the time (without the date) of the scheduled appointment.

// 9. Constructor(s):
//    - A no-argument constructor is implicitly provided by JPA for entity creation.
//    - A parameterized constructor can be added as needed to initialize fields.

// 10. Getters and Setters:
//    - Standard getter and setter methods are provided for accessing and modifying the fields: id, doctor, patient, appointmentTime, status, etc.

}

