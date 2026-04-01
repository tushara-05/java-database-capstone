package com.project.back_end.DTO;

/**
 * Data Transfer Object (DTO) for handling login requests from users.
 *
 * <p>A DTO is a simple object used to carry data between the client (frontend)
 * and the server without exposing the full entity models.</p>
 *
 * <p>This class is used when any user — Admin, Doctor, or Patient — attempts to log in.
 * The login form sends an {@code identifier} (username or email) and a {@code password}.</p>
 *
 * <ul>
 *   <li>For <b>Admin</b>: {@code identifier} is the admin's <b>username</b></li>
 *   <li>For <b>Doctor</b> or <b>Patient</b>: {@code identifier} is their <b>email address</b></li>
 * </ul>
 */
public class Login {

    /**
     * The login identifier used to find the user in the system.
     *
     * <ul>
     *   <li>For Admins: this is their username</li>
     *   <li>For Doctors and Patients: this is their email address</li>
     * </ul>
     */
    private String identifier;

    /**
     * The password provided by the user during login.
     * This is matched against the stored password to verify identity.
     * Passwords should be kept secret and never exposed in responses.
     */
    private String password;

    /**
     * No-argument (default) constructor.
     * Required for JSON deserialization — Spring uses this to create the object
     * from the incoming JSON request body.
     */
    public Login() {
    }

    /**
     * Returns the login identifier (username or email).
     *
     * @return the identifier string provided by the user
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the login identifier.
     *
     * @param identifier the username (for admin) or email (for doctor/patient)
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the password provided during login.
     *
     * @return the raw password string
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for this login request.
     *
     * @param password the user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
