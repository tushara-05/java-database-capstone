Appointment Repository
Next, you'll define custom query methods in AppointmentRepository to support advanced appointment search and filtering logic.

Open the AppointmentRepository.java file.
Open AppointmentRepository.java in IDE

Create a repository for the Appointment model by extending JpaRepository. This will allow for basic CRUD operations without needing to implement the methods manually.

Add the following methods:

findByDoctorIdAndAppointmentTimeBetween: Retrieve appointments for a doctor within a given time range.

Return type: List<Appointment>
Parameters: Long doctorId, LocalDateTime start, LocalDateTime end
Query: Use @Query with LEFT JOIN FETCH to include doctor and availability info
findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween: Filter appointments by doctor ID, partial patient name (case-insensitive), and time range.

Return type: List<Appointment>
Parameters: Long doctorId, String patientName, LocalDateTime start, LocalDateTime end
Query: Use @Query with LEFT JOIN FETCH to include patient and doctor details
deleteAllByDoctorId: Delete all appointments related to a specific doctor.

Return type: void
Parameter: Long doctorId
Annotations: Use @Modifying and @Transactional to enable delete operation
findByPatientId: Find all appointments for a specific patient.

Return type: List<Appointment>
Parameter: Long patientId
findByPatient_IdAndStatusOrderByAppointmentTimeAsc: Retrieve appointments for a patient by status, ordered by appointment time.

Return type: List<Appointment>
Parameters: Long patientId, int status
filterByDoctorNameAndPatientId: Search appointments by partial doctor name and patient ID.

Return type: List<Appointment>
Parameters: String doctorName, Long patientId
Query: Use @Query with LOWER and CONCAT for case-insensitive partial matching
filterByDoctorNameAndPatientIdAndStatus: Filter appointments by doctor name, patient ID, and status.

Return type: List<Appointment>
Parameters: String doctorName, Long patientId, int status
Query: Use @Query with LOWER, CONCAT, and additional filtering on status

Hint:

Extend JpaRepository<Appointment, Long> for basic CRUD functionality.
Use @Query for custom joins and filtering logic.
Use @Modifying and @Transactional for delete operations.
Leverage method naming conventions (e.g., findBy, filterBy) for additional queries.
Use LOWER, CONCAT, and % for partial, case-insensitive text matches.

package com.project.back_end.repo;

public interface AppointmentRepository  {

   // 1. Extend JpaRepository:
//    - The repository extends JpaRepository<Appointment, Long>, which gives it basic CRUD functionality.
//    - The methods such as save, delete, update, and find are inherited without the need for explicit implementation.
//    - JpaRepository also includes pagination and sorting features.

// Example: public interface AppointmentRepository extends JpaRepository<Appointment, Long> {}

// 2. Custom Query Methods:

//    - **findByDoctorIdAndAppointmentTimeBetween**:
//      - This method retrieves a list of appointments for a specific doctor within a given time range.
//      - The doctor’s available times are eagerly fetched to avoid lazy loading.
//      - Return type: List<Appointment>
//      - Parameters: Long doctorId, LocalDateTime start, LocalDateTime end
//      - It uses a LEFT JOIN to fetch the doctor’s available times along with the appointments.

//    - **findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween**:
//      - This method retrieves appointments for a specific doctor and patient name (ignoring case) within a given time range.
//      - It performs a LEFT JOIN to fetch both the doctor and patient details along with the appointment times.
//      - Return type: List<Appointment>
//      - Parameters: Long doctorId, String patientName, LocalDateTime start, LocalDateTime end

//    - **deleteAllByDoctorId**:
//      - This method deletes all appointments associated with a particular doctor.
//      - It is marked as @Modifying and @Transactional, which makes it a modification query, ensuring that the operation is executed within a transaction.
//      - Return type: void
//      - Parameters: Long doctorId

//    - **findByPatientId**:
//      - This method retrieves all appointments for a specific patient.
//      - Return type: List<Appointment>
//      - Parameters: Long patientId

//    - **findByPatient_IdAndStatusOrderByAppointmentTimeAsc**:
//      - This method retrieves all appointments for a specific patient with a given status, ordered by the appointment time.
//      - Return type: List<Appointment>
//      - Parameters: Long patientId, int status

//    - **filterByDoctorNameAndPatientId**:
//      - This method retrieves appointments based on a doctor’s name (using a LIKE query) and the patient’s ID.
//      - Return type: List<Appointment>
//      - Parameters: String doctorName, Long patientId

//    - **filterByDoctorNameAndPatientIdAndStatus**:
//      - This method retrieves appointments based on a doctor’s name (using a LIKE query), patient’s ID, and a specific appointment status.
//      - Return type: List<Appointment>
//      - Parameters: String doctorName, Long patientId, int status

//    - **updateStatus**:
//      - This method updates the status of a specific appointment based on its ID.
//      - Return type: void
//      - Parameters: int status, long id

// 3. @Modifying and @Transactional annotations:
//    - The @Modifying annotation is used to indicate that the method performs a modification operation (like DELETE or UPDATE).
//    - The @Transactional annotation ensures that the modification is done within a transaction, meaning that if any exception occurs, the changes will be rolled back.

// 4. @Repository annotation:
//    - The @Repository annotation marks this interface as a Spring Data JPA repository.
//    - Spring Data JPA automatically implements this repository, providing the necessary CRUD functionality and custom queries defined in the interface.

}
