package com.project.back_end.controllers;

import jakarta.validation.Valid;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Admin;
import com.project.back_end.services.ServiceClass;

/**
 * REST Controller that handles HTTP requests related to admin operations.
 *
 * <p>This controller exposes endpoints for admin authentication.
 * It is the entry point for all requests starting with the configured API path
 * followed by {@code /admin}.</p>
 *
 * <p>{@code @RestController} marks this class as a REST controller that automatically
 * serializes return values into JSON responses.</p>
 *
 * <p>{@code @RequestMapping("${api.path}admin")} sets the base URL for all
 * endpoints in this controller, where {@code ${api.path}} is a configurable 
 * prefix from application properties (e.g., {@code /api/v1/}).</p>
 */
@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    /** Shared service class used for admin login validation. */
    private final ServiceClass service;

    /**
     * Constructor that injects the shared service dependency.
     *
     * @param service the shared {@link ServiceClass} for handling admin validation
     */
    @Autowired
    public AdminController(ServiceClass service) {
        this.service = service;
    }

    /**
     * Handles HTTP POST requests for admin login.
     *
     * <p>Accepts an {@link Admin} object in the request body containing
     * the admin's {@code username} and {@code password}. The credentials are validated
     * against the database, and if correct, a JWT token is returned.</p>
     *
     * <p>Endpoint: {@code POST /admin/login}</p>
     *
     * @param admin the request body containing the admin's username and password.
     *              The {@code @Valid} annotation ensures the fields are validated
     *              according to constraints in the {@link Admin} model.
     * @return a {@link ResponseEntity} with a JWT token on successful login,
     *         or an error message if the credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@Valid @RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }
}
