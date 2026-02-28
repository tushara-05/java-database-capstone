Doctor Service
You'll now implement logic for managing doctor entities, including saving, updating, searching, and validating doctor information.

Open the DoctorService.java file.
Open DoctorService.java in IDE

Create a service class to manage operations related to doctors, including retrieving availability, saving, updating, deleting, and validating doctors.

Hint: Add @Service annotation above the class definition.
Declare necessary repositories to be used as private:

DoctorRepository for accessing doctor data
AppointmentRepository for accessing appointment data
TokenService for generating and validating tokens
Add the following methods:

getDoctorAvailability: This method fetches the available slots for a specific doctor on a given date.

Parameters: Long doctorId (The ID of the doctor), LocalDate date (The date for which availability is needed)
Return Type: List<String> (A list of available time slots for the doctor on the specified date)
Hint: Fetch appointments for the doctor on the specified date and filter out the booked slots from the available slots.
saveDoctor: This method saves a new doctor to the database.

Parameters: Doctor doctor (The doctor object you want to save)
Return Type: int (Returns 1 for success, -1 if the doctor already exists, 0 for internal errors)
Hint: Check if the doctor already exists by email before saving it.
updateDoctor: This method updates the details of an existing doctor.

Parameters: Doctor doctor (The doctor object with updated details)
Return Type: int (Returns 1 for success, -1 if doctor not found, 0 for internal errors)
Hint: Check if the doctor exists by ID before updating.
getDoctors: This method retrieves a list of all doctors.

Return Type: List<Doctor> (A list of all doctors)
Hint: Use doctorRepository.findAll() to fetch all doctors.
deleteDoctor: This method deletes a doctor by ID.

Parameters: long id (The ID of the doctor to be deleted)
Return Type: int (Returns 1 for success, -1 if doctor not found, 0 for internal errors)
Hint: Use appointmentRepository.deleteAllByDoctorId() to delete all associated appointments before deleting the doctor.
validateDoctor: This method validates a doctor's login credentials.

Parameters: Login login (The login object containing email and password)
Return Type: ResponseEntity<Map<String, String>> (Returns a response with a token if valid, or an error message if not)
Hint: Use doctorRepository.findByEmail() to find the doctor by email and verify the password.
findDoctorByName: This method finds doctors by their name.

Parameters: String name (The name of the doctor to search for)
Return Type: Map<String, Object> (Returns a map with the list of doctors matching the name)
Hint: Use doctorRepository.findByNameLike() to search by partial name match.
filterDoctorsByNameSpecilityandTime: This method filters doctors by name, specialty, and availability during AM/PM.

Parameters: String name (Doctor's name), String specialty (Doctor's specialty), String amOrPm (Time of day: AM/PM)
Return Type: Map<String, Object> (Returns a map with the filtered list of doctors)
Hint: Use doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase() to filter by name and specialty, then filter the results by time.
filterDoctorByNameAndTime: This method filters doctors by name and their availability during AM/PM.

Parameters: String name (Doctor's name), String amOrPm (Time of day: AM/PM)
Return Type: Map<String, Object> (Returns a map with the filtered list of doctors)
Hint: First, filter by name, then filter the result by time.
filterDoctorByNameAndSpecility: This method filters doctors by name and specialty.

Parameters: String name (Doctor's name), String specilty (Doctor's specialty)
Return Type: Map<String, Object> (Returns a map with the filtered list of doctors)
Hint: Use doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase() to filter by name and specialty.
filterDoctorByTimeAndSpecility: This method filters doctors by specialty and their availability during AM/PM.

Parameters: String specilty (Doctor's specialty), String amOrPm (Time of day: AM/PM)
Return Type: Map<String, Object> (Returns a map with the filtered list of doctors)
Hint: Use doctorRepository.findBySpecialtyIgnoreCase() to filter by specialty, then filter the results by time.
filterDoctorBySpecility: This method filters doctors by specialty.

Parameters: String specilty (Doctor's specialty)
Return Type: Map<String, Object> (Returns a map with the filtered list of doctors)
Hint: Use doctorRepository.findBySpecialtyIgnoreCase() to filter by specialty.
filterDoctorsByTime: This method filters doctors by their availability during AM/PM.

Parameters: String amOrPm (Time of day: AM/PM)
Return Type: Map<String, Object> (Returns a map with the filtered list of doctors)
Hint: Use doctorRepository.findAll() to fetch all doctors, then filter by available time.
filterDoctorByTime: This private method filters a list of doctors by their available times (AM/PM).

Parameters: List<Doctor> doctors (The list of doctors to filter), String amOrPm (Time of day: AM/PM)
Return Type: List<Doctor> (Returns a filtered list of doctors)
Hint: Filter doctors based on their available times, comparing the time slots with AM/PM.
Additional Hints
getDoctorAvailability Method

Useful to find available time slots for a specific doctor on a given date. You can return the available slots after filtering out the booked ones
saveDoctor Method

Ensures that no duplicate doctor entries exist by email before saving the doctor
updateDoctor Method

Updates an existing doctor's details if found. If not found, it returns a conflict error
filterDoctorsByNameSpecilityandTime Method

Combines filtering by name, specialty, and time (AM/PM) for searching doctors

package com.project.back_end.services;

public class DoctorService {

// 1. **Add @Service Annotation**:
//    - This class should be annotated with `@Service` to indicate that it is a service layer class.
//    - The `@Service` annotation marks this class as a Spring-managed bean for business logic.
//    - Instruction: Add `@Service` above the class declaration.

// 2. **Constructor Injection for Dependencies**:
//    - The `DoctorService` class depends on `DoctorRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies should be injected via the constructor for proper dependency management.
//    - Instruction: Ensure constructor injection is used for injecting dependencies into the service.

// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

// 4. **getDoctorAvailability Method**:
//    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
//    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
//    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.

// 5. **saveDoctor Method**:
//    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
//    - If a doctor with the same email is found, it returns `-1` to indicate conflict; `1` for success, and `0` for internal errors.
//    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.

// 6. **updateDoctor Method**:
//    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
//    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.

// 7. **getDoctors Method**:
//    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
//    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times). 

// 8. **deleteDoctor Method**:
//    - Deletes a doctor from the system along with all appointments associated with that doctor.
//    - It first checks if the doctor exists. If not, it returns `-1`; otherwise, it deletes the doctor and their appointments.
//    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.

// 9. **validateDoctor Method**:
//    - Validates a doctor's login by checking if the email and password match an existing doctor record.
//    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
//    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.

// 10. **findDoctorByName Method**:
//    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
//    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
//    - Instruction: Ensure that available times are eagerly loaded for the doctors.


// 11. **filterDoctorsByNameSpecilityandTime Method**:
//    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
//    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
//    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.

// 12. **filterDoctorByTime Method**:
//    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
//    - This method processes a list of doctors and their available times to return those that fit the time criteria.
//    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.


// 13. **filterDoctorByNameAndTime Method**:
//    - Filters doctors based on their name and the specified time period (AM/PM).
//    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
//    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).

// 14. **filterDoctorByNameAndSpecility Method**:
//    - Filters doctors by name and specialty.
//    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
//    - Instruction: Ensure that both name and specialty are considered when filtering doctors.


// 15. **filterDoctorByTimeAndSpecility Method**:
//    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
//    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
//    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).

// 16. **filterDoctorBySpecility Method**:
//    - Filters doctors based on their specialty.
//    - This method fetches all doctors matching the specified specialty and returns them.
//    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.

// 17. **filterDoctorsByTime Method**:
//    - Filters all doctors based on their availability during a specific time period (AM/PM).
//    - The method checks all doctors' available times and returns those available during the specified time period.
//    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.

   
}
