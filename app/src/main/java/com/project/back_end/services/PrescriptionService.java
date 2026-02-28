Prescription Service
The PrescriptionService class handles the creation and retrieval of prescriptions. It provides two key functionalities: saving a new prescription and retrieving an existing prescription based on an appointment ID.

Here's a breakdown of the methods in this service:

Open the PrescriptionService.java file.
Open PrescriptionService.java in IDE

1. savePrescription(Prescription prescription)
Purpose
Saves a prescription to the database
Parameters
Prescription prescription – The prescription object to be saved
Return Type
ResponseEntity<Map<String, String>> – Returns a response with a message indicating the result of the save operation
Explanation
The method attempts to save the prescription to the database using the prescriptionRepository. If successful, it returns a 201 Created status with a message "Prescription saved". If there is an error, it returns a 500 Internal Server Error with a generic error message.
2. getPrescription(Long appointmentId)
Purpose
Retrieves the prescription associated with a specific appointment ID
Parameters
Long appointmentId – The appointment ID whose associated prescription is to be retrieved
Return Type
ResponseEntity<Map<String, Object>> – Returns a response containing the prescription details or an error message
Explanation
The method attempts to fetch the prescription from the database using the prescriptionRepository.findByAppointmentId(appointmentId) method. If successful, it returns the prescription as part of the response with a 200 OK status. If there is an error, it returns a 500 Internal Server Error with an error message.

package com.project.back_end.services;

public class PrescriptionService {
    
 // 1. **Add @Service Annotation**:
//    - The `@Service` annotation marks this class as a Spring service component, allowing Spring's container to manage it.
//    - This class contains the business logic related to managing prescriptions in the healthcare system.
//    - Instruction: Ensure the `@Service` annotation is applied to mark this class as a Spring-managed service.

// 2. **Constructor Injection for Dependencies**:
//    - The `PrescriptionService` class depends on the `PrescriptionRepository` to interact with the database.
//    - It is injected through the constructor, ensuring proper dependency management and enabling testing.
//    - Instruction: Constructor injection is a good practice, ensuring that all necessary dependencies are available at the time of service initialization.

// 3. **savePrescription Method**:
//    - This method saves a new prescription to the database.
//    - Before saving, it checks if a prescription already exists for the same appointment (using the appointment ID).
//    - If a prescription exists, it returns a `400 Bad Request` with a message stating the prescription already exists.
//    - If no prescription exists, it saves the new prescription and returns a `201 Created` status with a success message.
//    - Instruction: Handle errors by providing appropriate status codes and messages, ensuring that multiple prescriptions for the same appointment are not saved.

// 4. **getPrescription Method**:
//    - Retrieves a prescription associated with a specific appointment based on the `appointmentId`.
//    - If a prescription is found, it returns it within a map wrapped in a `200 OK` status.
//    - If there is an error while fetching the prescription, it logs the error and returns a `500 Internal Server Error` status with an error message.
//    - Instruction: Ensure that this method handles edge cases, such as no prescriptions found for the given appointment, by returning meaningful responses.

// 5. **Exception Handling and Error Responses**:
//    - Both methods (`savePrescription` and `getPrescription`) contain try-catch blocks to handle exceptions that may occur during database interaction.
//    - If an error occurs, the method logs the error and returns an HTTP `500 Internal Server Error` response with a corresponding error message.
//    - Instruction: Ensure that all potential exceptions are handled properly, and meaningful responses are returned to the client.


}
