Prescription model
The Prescription model is a MongoDB document that stores medication instructions issued during appointments. It includes the patient's name, the referenced appointment, prescribed drugs, dosage, and optional notes from the doctor.

Open the Prescription.java file
src/main/java/com/project/back_end/models/Prescription.java
Open Prescription.java in IDE

Add the following attributes along with getters and setters:
id: private String – Unique identifier for the prescription (MongoDB ID, auto-generated)
patientName: private String – Patient's full name (required, 3–100 characters)
appointmentId: private Long – Reference to the appointment entity's ID (required, must be a valid Long)
medication: private String – Name of the medication (required, 3–100 characters)
dosage: private String – Dosage details (required, 3–20 characters)
doctorNotes: private String – Optional field for any notes from the doctor (max 200 characters)

Hints
Annotate the class with:
@Document(collection = "prescriptions")
Use @Id to mark the MongoDB _id field
Use @NotNull and @Size on all required fields to enforce string length and non-null constraints:
@Size(min = 3, max = 100)
@Size(min = 3, max = 20)
@Size(max = 200)
Implement a constructor to initialize the most important fields for easy object creation
Add standard getters and setters

Tasks
Design a MongoDB-compatible class with strict validation
Define a flexible schema using Spring Data MongoDB
Include metadata relevant to prescriptions, while keeping doctor notes optional
package com.project.back_end.models;

public class Prescription {

  // @Document annotation:
//    - Marks the class as a MongoDB document (a collection in MongoDB).
//    - The collection name is specified as "prescriptions" to map this class to the "prescriptions" collection in MongoDB.

// 1. 'id' field:
//    - Type: private String
//    - Description:
//      - Represents the unique identifier for each prescription.
//      - The @Id annotation marks it as the primary key in the MongoDB collection.
//      - The id is of type String, which is commonly used for MongoDB's ObjectId as it stores IDs as strings in the database.

// 2. 'patientName' field:
//    - Type: private String
//    - Description:
//      - Represents the name of the patient receiving the prescription.
//      - The @NotNull annotation ensures that the patient name is required.
//      - The @Size(min = 3, max = 100) annotation ensures that the name length is between 3 and 100 characters, ensuring a reasonable name length.

// 3. 'appointmentId' field:
//    - Type: private Long
//    - Description:
//      - Represents the ID of the associated appointment where the prescription was given.
//      - The @NotNull annotation ensures that the appointment ID is required for the prescription.

// 4. 'medication' field:
//    - Type: private String
//    - Description:
//      - Represents the medication prescribed to the patient.
//      - The @NotNull annotation ensures that the medication name is required.
//      - The @Size(min = 3, max = 100) annotation ensures that the medication name is between 3 and 100 characters, which ensures meaningful medication names.

// 5. 'dosage' field:
//    - Type: private String
//    - Description:
//      - Represents the dosage information for the prescribed medication.
//      - The @NotNull annotation ensures that the dosage information is provided.

// 6. 'doctorNotes' field:
//    - Type: private String
//    - Description:
//      - Represents any additional notes or instructions from the doctor regarding the prescription.
//      - The @Size(max = 200) annotation ensures that the doctor's notes do not exceed 200 characters, providing a reasonable limit for additional notes.

// 7. Constructors:
//    - The class includes a no-argument constructor (default constructor) and a parameterized constructor that initializes the fields: patientName, medication, dosage, doctorNotes, and appointmentId.

// 8. Getters and Setters:
//    - Standard getter and setter methods are provided for all fields: id, patientName, medication, dosage, doctorNotes, and appointmentId.
//    - These methods allow access and modification of the fields of the Prescription class.


}
