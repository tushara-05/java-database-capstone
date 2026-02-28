Service Class
Here, you'll create a central service class that combines multiple functionalities: authentication, validation, and coordination across entities.

Open the ServiceClass.java file.
Open ServiceClass.java in IDE

Create a service class to handle authentication, doctor and patient management, and appointment validation.

Hint: Add @Service annotation above the class definition.
Declare the necessary services and repositories to be used as private.

Hint
private final TokenService tokenService;
private final AdminRepository adminRepository;
private final DoctorRepository doctorRepository;
private final PatientRepository patientRepository;
private final DoctorService doctorService;
private final PatientService patientService;

Add the following logic:

validateToken: This method checks the validity of a token for a given user.

Parameters

String token: The token to be validated
String user: The user to whom the token belongs
Return Type

ResponseEntity<Map<String, String>>: Returns an error message if the token is invalid or expired
Hint: Use tokenService.validateToken() to check if the token is valid. Return an Unauthorized response if the token is invalid or expired.

validateAdmin: This method validates the login credentials of an admin.

Parameters

Admin receivedAdmin: The admin credentials (username and password) to be validated
Return Type

ResponseEntity<Map<String, String>>: Returns a generated token if the admin is authenticated
Hint: Use adminRepository.findByUsername() to check if the admin exists and compare the password. If valid, generate a token with tokenService.generateToken().

filterDoctor: This method filters doctors based on name, specialty, and available time.

Parameters

String name: The name of the doctor
String specialty: The specialty of the doctor
String time: The available time of the doctor
Return Type

Map<String, Object>: Returns a list of doctors that match the filtering criteria
Hint: Use doctorService methods like filterDoctorsByNameSpecilityandTime() to perform filtering. Return doctors that match the provided criteria.

validateAppointment: This method validates whether an appointment is available based on the doctor's schedule.

Parameters

Appointment appointment: The appointment to validate
Return Type

int:
1 if the appointment time is valid
0 if the time is unavailable
-1 if the doctor doesn't exist
Hint: Use doctorRepository.findById() to find the doctor and doctorService.getDoctorAvailability() to check available time slots for that doctor.

validatePatient: This method checks whether a patient exists based on their email or phone number.

Parameters

Patient patient: The patient to validate
Return Type

boolean:
true if the patient does not exist
false if the patient exists already
Hint: Use patientRepository.findByEmailOrPhone() to check if the patient exists. If the patient is found, return false.

validatePatientLogin: This method validates a patient's login credentials (email and password).

Parameters

Login login: The login credentials of the patient (email and password)
Return Type

ResponseEntity<Map<String, String>>: Returns a generated token if the login is valid
Hint: Use patientRepository.findByEmail() to check if the email exists. Compare the password, and if valid, generate a token with tokenService.generateToken().

filterPatient: This method filters patient appointments based on certain criteria, such as condition and doctor name.

Parameters

String condition: The medical condition to filter appointments by
String name: The doctor's name to filter appointments by
String token: The authentication token to identify the patient
Return Type

ResponseEntity<Map<String, Object>>: Returns the filtered list of patient appointments based on the criteria
Hint: Use patientService methods like filterByCondition(), filterByDoctor(), or filterByDoctorAndCondition() to apply the necessary filters based on the input.

Additional Hints
validateToken Method

This method ensures that the provided token is valid. It's essential for authenticating user requests.
validateAdmin Method

This method is critical for admin authentication. Only valid admins will be granted access, and an authentication token will be returned for subsequent requests.
filterDoctor Method

This method helps in filtering doctors based on different criteria. If none of the filters are provided, it defaults to returning all available doctors.
validateAppointment Method

The method checks if an appointment time matches the available slots for a doctor. It's important for ensuring patients are scheduled only during valid time slots.
validatePatient Method

This validation method ensures there are no duplicate patient records based on either email or phone number. It's helpful in preventing duplicate registrations.
validatePatientLogin Method

This method checks patient credentials during login. If the login is successful, the method returns a token, which can be used for authentication in future requests.
filterPatient Method

This method allows filtering of patient appointments based on specific conditions or doctors. It's crucial for managing patient appointment data effectively.

package com.project.back_end.services;

public class ServiceClass {
// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.

// 2. **Constructor Injection for Dependencies**
// The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
// and ensures that all required dependencies are provided at object creation time.

// 3. **validateToken Method**
// This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
// If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
// unauthorized access to protected resources.

// 4. **validateAdmin Method**
// This method validates the login credentials for an admin user.
// - It first searches the admin repository using the provided username.
// - If an admin is found, it checks if the password matches.
// - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
// - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
// - If no admin is found, it also returns a 401 Unauthorized.
// - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
// This method ensures that only valid admin users can access secured parts of the system.

// 5. **filterDoctor Method**
// This method provides filtering functionality for doctors based on name, specialty, and available time slots.
// - It supports various combinations of the three filters.
// - If none of the filters are provided, it returns all available doctors.
// This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.

// 6. **validateAppointment Method**
// This method validates if the requested appointment time for a doctor is available.
// - It first checks if the doctor exists in the repository.
// - Then, it retrieves the list of available time slots for the doctor on the specified date.
// - It compares the requested appointment time with the start times of these slots.
// - If a match is found, it returns 1 (valid appointment time).
// - If no matching time slot is found, it returns 0 (invalid).
// - If the doctor doesn’t exist, it returns -1.
// This logic prevents overlapping or invalid appointment bookings.

// 7. **validatePatient Method**
// This method checks whether a patient with the same email or phone number already exists in the system.
// - If a match is found, it returns false (indicating the patient is not valid for new registration).
// - If no match is found, it returns true.
// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.

// 8. **validatePatientLogin Method**
// This method handles login validation for patient users.
// - It looks up the patient by email.
// - If found, it checks whether the provided password matches the stored one.
// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
// - If an exception occurs, it returns a 500 Internal Server Error.
// This method ensures only legitimate patients can log in and access their data securely.

// 9. **filterPatient Method**
// This method filters a patient's appointment history based on condition and doctor name.
// - It extracts the email from the JWT token to identify the patient.
// - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
// - If no filters are provided, it retrieves all appointments for the patient.
// This flexible method supports patient-specific querying and enhances user experience on the client side.


}
