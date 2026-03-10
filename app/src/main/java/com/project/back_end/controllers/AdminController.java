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
 * Admin Controller
 * The admin controller will handle login operations, validating credentials and issuing tokens to authorized users.
 */
@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final ServiceClass serviceClass;

    @Autowired
    public AdminController(ServiceClass serviceClass) {
        this.serviceClass = serviceClass;
    }

    /**
     * Admin login endpoint.
     * Accepts admin credentials in the request body and validates them.
     * Returns a token if credentials are valid, or an error message otherwise.
     * 
     * @param admin Admin object containing username and password
     * @return ResponseEntity with token on success or error message on failure
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@Valid @RequestBody Admin admin) {
        return serviceClass.validateAdmin(admin);
    }
}

