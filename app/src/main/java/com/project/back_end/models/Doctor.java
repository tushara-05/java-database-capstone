Doctor model
The Doctor model stores information about healthcare providers, including their contact details, medical specialty, and availability. This model is crucial for mapping appointments and verifying doctor credentials.

Open the Doctor.java file
src/main/java/com/project/back_end/models/Doctor.java
Open Doctor.java in IDE

Add the following attributes along with getters and setters:
id: private Long – Auto-incremented primary key
name: private String – Doctor's full name (required, 3–100 characters)
specialty: private String – Medical specialty (required, 3–50 characters)
email: private String – Valid email address (required, must match email format)
password: private String – Password (required, at least 6 characters, write-only in JSON)
phone: private String – Phone number (required, must be 10 digits)
availableTimes: private List<String> – List of available time slots (Example: "09:00 -10:00")

Hints
Annotate the class with @Entity.
Use the following annotations for validations:
@NotNull
@Size(min = 3, max = 100)
@Email
@Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")

For password, use:
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)

Use @ElementCollection on availableTimes:
@ElementCollection
private List<String> availableTimes;
Add getters and setters for all fields.

Tasks
Add detailed validations for fields
Ensure that sensitive data is hidden from public APIs
Structure time availability data using proper JPA techniques
package com.project.back_end.models;

public class Doctor {

// @Entity annotation:
//    - Marks the class as a JPA entity, meaning it represents a table in the database.
//    - Required for persistence frameworks (e.g., Hibernate) to map the class to a database table.

// 1. 'id' field:
//    - Type: private Long
//    - Description:
//      - Represents the unique identifier for each doctor.
//      - The @Id annotation marks it as the primary key.
//      - The @GeneratedValue(strategy = GenerationType.IDENTITY) annotation auto-generates the ID value when a new record is inserted into the database.

// 2. 'name' field:
//    - Type: private String
//    - Description:
//      - Represents the doctor's name.
//      - The @NotNull annotation ensures that the doctor's name is required.
//      - The @Size(min = 3, max = 100) annotation ensures that the name length is between 3 and 100 characters. 
//      - Provides validation for correct input and user experience.


// 3. 'specialty' field:
//    - Type: private String
//    - Description:
//      - Represents the medical specialty of the doctor.
//      - The @NotNull annotation ensures that a specialty must be provided.
//      - The @Size(min = 3, max = 50) annotation ensures that the specialty name is between 3 and 50 characters long.

// 4. 'email' field:
//    - Type: private String
//    - Description:
//      - Represents the doctor's email address.
//      - The @NotNull annotation ensures that an email address is required.
//      - The @Email annotation validates that the email address follows a valid email format (e.g., doctor@example.com).

// 5. 'password' field:
//    - Type: private String
//    - Description:
//      - Represents the doctor's password for login authentication.
//      - The @NotNull annotation ensures that a password must be provided.
//      - The @Size(min = 6) annotation ensures that the password must be at least 6 characters long.
//      - The @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) annotation ensures that the password is not serialized in the response (hidden from the frontend).

// 6. 'phone' field:
//    - Type: private String
//    - Description:
//      - Represents the doctor's phone number.
//      - The @NotNull annotation ensures that a phone number must be provided.
//      - The @Pattern(regexp = "^[0-9]{10}$") annotation validates that the phone number must be exactly 10 digits long.

// 7. 'availableTimes' field:
//    - Type: private List<String>
//    - Description:
//      - Represents the available times for the doctor in a list of time slots.
//      - Each time slot is represented as a string (e.g., "09:00-10:00", "10:00-11:00").
//      - The @ElementCollection annotation ensures that the list of time slots is stored as a separate collection in the database.

// 8. Getters and Setters:
//    - Standard getter and setter methods are provided for all fields: id, name, specialty, email, password, phone, and availableTimes.

}

