package com.project.back_end.mvc;

import java.util.Map;

import com.project.back_end.services.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * MVC Controller responsible for handling dashboard navigation and view routing.
 * This controller ensures that users have valid tokens before accessing their respective dashboards.
 */
@Controller
public class DashboardController {

    @Autowired
    private ServiceClass service;

    /**
     * Handles the request to view the admin dashboard.
     * Validates the provided token to ensure the user has 'admin' privileges.
     * If validation is successful, returns the admin dashboard view.
     * Otherwise, redirects the user to the home or login page.
     *
     * @param token The authentication token associated with the admin user session.
     * @return A string representing the view name to render, or a redirect URL.
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode().is2xxSuccessful()) {
            return "admin/adminDashboard";
        }
        return "redirect:/";
    }

    /**
     * Handles the request to view the doctor dashboard.
     * Validates the provided token to ensure the user has 'doctor' privileges.
     * If validation is successful, returns the doctor dashboard view.
     * Otherwise, redirects the user to the home or login page.
     *
     * @param token The authentication token associated with the doctor user session.
     * @return A string representing the view name to render, or a redirect URL.
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation.getStatusCode().is2xxSuccessful()) {
            return "doctor/doctorDashboard";
        }
        return "redirect:/";
    }
}

