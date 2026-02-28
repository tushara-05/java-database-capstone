Patient Service
The PatientService class handles various operations related to patients, such as creating a patient, fetching their appointments, and filtering those appointments based on specific conditions (for example, past, future, by doctor).

Here's a detailed breakdown of the methods in the PatientService class:

Open the PatientService.java file.
Open PatientService.java in IDE

1. createPatient(Patient patient)
Purpose
Saves a new patient to the database
Parameters
Patient patient – The patient object to be saved
Return Type
int – Returns 1 on success, and 0 on failure (for example, exception)
Explanation
This method saves the patient to the database and handles exceptions that may arise during the save process.
2. getPatientAppointment(Long id, String token)
Purpose
Retrieves a list of appointments for a specific patient
Parameters
Long id – The patient's ID
String token – The JWT token containing the email
Return Type
ResponseEntity<Map<String, Object>> – Returns a response containing a list of appointments or an error message.
Explanation
The method checks if the provided patient ID matches the one decoded from the token (by email). If there's a mismatch, it returns an Unauthorized status.
If the IDs match, it retrieves the patient's appointments and returns them as a list of AppointmentDTO objects.
3. filterByCondition(String condition, Long id)
Purpose
Filters appointments by condition (past or future) for a specific patient
Parameters
String condition – The condition to filter by (past or future)
Long id – The patient’s ID
Return Type
ResponseEntity<Map<String, Object>> – Returns the filtered appointments or an error message
Explanation
The method checks the condition value (past or future) and filters appointments accordingly. It uses the status (1 for past and 0 for future) to determine the filtering criteria.
4. filterByDoctor(String name, Long patientId)
Purpose
Filters the patient's appointments by doctor's name
Parameters
String name – The name of the doctor
Long patientId – The ID of the patient
Return Type
ResponseEntity<Map<String, Object>> – Returns the filtered appointments or an error message
Explanation
The method fetches appointments where the doctor's name matches the provided name and the patient ID matches the given patientId.
5. filterByDoctorAndCondition(String condition, String name, long patientId)
Purpose
Filters the patient's appointments by doctor's name and appointment condition (past or future)
Parameters
String condition – The condition to filter by (past or future)
String name – The name of the doctor
long patientId – The ID of the patient
Return Type
ResponseEntity<Map<String, Object>> – Returns the filtered appointments or an error message
Explanation
The method combines the filtering criteria of both the doctor's name and the condition (past or future).
6. getPatientDetails(String token)
Purpose
Fetches the patient's details based on the provided JWT token
Parameters
String token – The JWT token containing the email
Return Type
ResponseEntity<Map<String, Object>> – Returns the patient's details or an error message
Explanation
The method extracts the email from the token and retrieves the corresponding patient from the database. The patient details are then returned as part of the response.
Helper Methods
AppointmentDTO: The data transfer object (DTO) that represents an appointment. It is used to send appointment data in a simplified format, without including sensitive data.
TokenService: Used to extract the email from the JWT token, which helps in ensuring the patient is authorized to access their information.

package com.project.back_end.services;

public class PatientService {
// 1. **Add @Service Annotation**:
//    - The `@Service` annotation is used to mark this class as a Spring service component. 
//    - It will be managed by Spring's container and used for business logic related to patients and appointments.
//    - Instruction: Ensure that the `@Service` annotation is applied above the class declaration.

// 2. **Constructor Injection for Dependencies**:
//    - The `PatientService` class has dependencies on `PatientRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies are injected via the constructor to maintain good practices of dependency injection and testing.
//    - Instruction: Ensure constructor injection is used for all the required dependencies.

// 3. **createPatient Method**:
//    - Creates a new patient in the database. It saves the patient object using the `PatientRepository`.
//    - If the patient is successfully saved, the method returns `1`; otherwise, it logs the error and returns `0`.
//    - Instruction: Ensure that error handling is done properly and exceptions are caught and logged appropriately.

// 4. **getPatientAppointment Method**:
//    - Retrieves a list of appointments for a specific patient, based on their ID.
//    - The appointments are then converted into `AppointmentDTO` objects for easier consumption by the API client.
//    - This method is marked as `@Transactional` to ensure database consistency during the transaction.
//    - Instruction: Ensure that appointment data is properly converted into DTOs and the method handles errors gracefully.

// 5. **filterByCondition Method**:
//    - Filters appointments for a patient based on the condition (e.g., "past" or "future").
//    - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
//    - Converts the appointments into `AppointmentDTO` and returns them in the response.
//    - Instruction: Ensure the method correctly handles "past" and "future" conditions, and that invalid conditions are caught and returned as errors.

// 6. **filterByDoctor Method**:
//    - Filters appointments for a patient based on the doctor's name.
//    - It retrieves appointments where the doctor’s name matches the given value, and the patient ID matches the provided ID.
//    - Instruction: Ensure that the method correctly filters by doctor's name and patient ID and handles any errors or invalid cases.

// 7. **filterByDoctorAndCondition Method**:
//    - Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
//    - This method combines filtering by doctor name and appointment status (past or future).
//    - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
//    - Instruction: Ensure that the filter handles both doctor name and condition properly, and catches errors for invalid input.

// 8. **getPatientDetails Method**:
//    - Retrieves patient details using the `tokenService` to extract the patient's email from the provided token.
//    - Once the email is extracted, it fetches the corresponding patient from the `patientRepository`.
//    - It returns the patient's information in the response body.
    //    - Instruction: Make sure that the token extraction process works correctly and patient details are fetched properly based on the extracted email.

// 9. **Handling Exceptions and Errors**:
//    - The service methods handle exceptions using try-catch blocks and log any issues that occur. If an error occurs during database operations, the service responds with appropriate HTTP status codes (e.g., `500 Internal Server Error`).
//    - Instruction: Ensure that error handling is consistent across the service, with proper logging and meaningful error messages returned to the client.

// 10. **Use of DTOs (Data Transfer Objects)**:
//    - The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures that sensitive or unnecessary data (e.g., password or private patient information) is not exposed in the response.
//    - Instruction: Ensure that DTOs are used appropriately to limit the exposure of internal data and only send the relevant fields to the client.



}
