package com.project.back_end.DTO;

public class Login {

    // 1. 'identifier' field:
    //    - Type: private String
    //    - Description:
    //      - Represents the unique identifier of the user attempting to log in.
    //      - For Doctor/Patient: this contains the email address
    //      - For Admin: this contains the username

    // 2. 'password' field:
    //    - Type: private String
    //    - Description:
    //      - Represents the password associated with the user credentials.
    //      - The password field is used for verifying the user's identity during login.
    //      - It is generally hashed before being stored and compared during authentication.

    private String identifier;
    private String password;

    // Default constructor
    public Login() {
    }

    // Getter for identifier
    public String getIdentifier() {
        return identifier;
    }

    // Setter for identifier
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }
}
