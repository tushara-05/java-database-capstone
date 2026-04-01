package com.project.back_end.services;

import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Service component responsible for creating, parsing, and validating JWT tokens.
 *
 * <p>JWT (JSON Web Token) is a compact, self-contained way of securely transmitting information
 * between the client and server. In this application, JWT tokens are used to authenticate users
 * (admins, doctors, and patients) after they log in.</p>
 *
 * <p>This class is annotated with {@code @Component} instead of {@code @Service} because it
 * is a lower-level utility component. Spring still manages it as a bean and it can be
 * injected anywhere in the application.</p>
 *
 * <p>The signing secret is read from the application configuration using {@code @Value("${jwt.secret}")}.</p>
 */
@Component
public class TokenService {

    /** Repository used to look up admin users during token validation. */
    private final AdminRepository adminRepository;

    /** Repository used to look up doctor users during token validation. */
    private final DoctorRepository doctorRepository;

    /** Repository used to look up patient users during token validation. */
    private final PatientRepository patientRepository;

    /**
     * The secret key string used to sign JWT tokens.
     * Loaded from the application configuration (e.g., application.properties or environment variable).
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Constructor that injects all repository dependencies.
     *
     * <p>These repositories are used in {@link #validateToken(String, String)} to verify
     * that the user identified in the token actually exists in the database.</p>
     *
     * @param adminRepository   the repository for admin data
     * @param doctorRepository  the repository for doctor data
     * @param patientRepository the repository for patient data
     */
    @Autowired
    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Creates a cryptographic signing key from the secret string.
     *
     * <p>Uses HMAC-SHA algorithm to create a {@link SecretKey} from the configured
     * secret string. This key is used to both sign (when generating) and verify (when parsing)
     * JWT tokens, ensuring they haven't been tampered with.</p>
     *
     * @return the {@link SecretKey} used for signing and verifying JWT tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a signed JWT token for a given user identifier.
     *
     * <p>The token contains:</p>
     * <ul>
     *   <li><b>Subject</b>: the user's identifier (email for doctors/patients, username for admins)</li>
     *   <li><b>Issued At</b>: the current date and time</li>
     *   <li><b>Expiration</b>: 7 days from now</li>
     *   <li><b>Signature</b>: signed with the HMAC-SHA key to prevent tampering</li>
     * </ul>
     *
     * <p>This token is returned to the client after a successful login and must be
     * included in subsequent API requests that require authentication.</p>
     *
     * @param identifier the unique identifier of the user (email or username)
     * @return a signed JWT token string that is valid for 7 days
     */
    public String generateToken(String identifier) {
        Instant now = Instant.now();
        Instant expiry = now.plus(7, ChronoUnit.DAYS); // 7 days

        return Jwts.builder()
                .subject(identifier)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the user identifier (subject) from a JWT token.
     *
     * <p>This method parses and verifies the token using the signing key.
     * If the token is valid, it returns the subject — which is:</p>
     * <ul>
     *   <li>The <b>email address</b> for doctors and patients</li>
     *   <li>The <b>username</b> for admins</li>
     * </ul>
     *
     * <p>If the token is expired or has been tampered with, an exception will be thrown.</p>
     *
     * @param token the JWT token string to parse
     * @return the user's identifier (email or username) stored in the token
     * @throws io.jsonwebtoken.JwtException if the token is invalid, expired, or malformed
     */
    public String extractIdentifier(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates a JWT token for a specific user role.
     *
     * <p>First extracts the identifier from the token, then checks the appropriate
     * database repository based on the provided role:</p>
     * <ul>
     *   <li>{@code "admin"} → looks up by username in the admin repository</li>
     *   <li>{@code "doctor"} → looks up by email in the doctor repository</li>
     *   <li>{@code "patient"} → looks up by email in the patient repository</li>
     * </ul>
     *
     * <p>Returns {@code true} only if the token is valid AND the user exists in the database
     * for the specified role. Returns {@code false} for any other case, including
     * expired tokens, tampered tokens, or unknown roles.</p>
     *
     * @param token the JWT token string to validate
     * @param user  the expected role of the user: "admin", "doctor", or "patient"
     * @return {@code true} if the token is valid and belongs to an existing user with the given role,
     *         {@code false} otherwise
     */
    public boolean validateToken(String token, String user) {
        try {
            String identifier = extractIdentifier(token);

            switch (user.toLowerCase()) {
                case "admin":
                    Admin admin = adminRepository.findByUsername(identifier);
                    return admin != null;
                case "doctor":
                    Doctor doctor = doctorRepository.findByEmail(identifier);
                    return doctor != null;
                case "patient":
                    Patient patient = patientRepository.findByEmail(identifier);
                    return patient != null;
                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
