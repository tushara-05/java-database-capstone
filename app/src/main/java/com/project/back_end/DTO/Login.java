Login DTO
you'll create a DTO to handle user login requests. This class will encapsulate user credentials submitted from the frontend.

Let’s define a simple DTO class to receive the user’s identifier (which can be an email or username depending on the user type) and password during login operations.

Open the Login.java file.
Open Login.java in IDE

Create a DTO class to represent login request data. This class will be used to receive login credentials from the client.

Add the following fields:

identifier: String – The unique identifier of the user attempting to log in (email for Doctor/Patient, username for Admin)
password: String – The password provided by the user
Add standard getter and setter methods for both fields to enable deserialization of the login request body.

Hint:

This class is typically used in @RequestBody parameters inside controller methods.
Do not add any persistence annotations (@Entity, @Id, and so on).
This DTO is used only for authentication input and is not stored in the database.
package com.project.back_end.DTO;

public class Login {
    
// 1. 'email' field:
//    - Type: private String
//    - Description:
//      - Represents the email address used for logging into the system.
//      - The email field is expected to contain a valid email address for user authentication purposes.

// 2. 'password' field:
//    - Type: private String
//    - Description:
//      - Represents the password associated with the email address.
//      - The password field is used for verifying the user's identity during login.
//      - It is generally hashed before being stored and compared during authentication.

// 3. Constructor:
//    - No explicit constructor is defined for this class, as it relies on the default constructor provided by Java.
//    - This class can be initialized with setters or directly via reflection, as per the application's needs.

// 4. Getters and Setters:
//    - Standard getter and setter methods are provided for both 'email' and 'password' fields.
//    - The 'getEmail()' method allows access to the email value.
//    - The 'setEmail(String email)' method sets the email value.
//    - The 'getPassword()' method allows access to the password value.
//    - The 'setPassword(String password)' method sets the password value.


}
