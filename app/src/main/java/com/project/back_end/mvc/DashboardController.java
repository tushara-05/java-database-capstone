package com.project.back_end.mvc;

import com.project.back_end.services.ServiceClass;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DashboardController {

    private final ServiceClass service;

    public DashboardController(ServiceClass service) {
        this.service = service;
    }

    // 1. Admin Dashboard
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        Map<String, String> validation = service.validateToken(token, "admin").getBody();
        // If validation map is empty, token is valid
        if (validation == null || validation.isEmpty()) {
            return "admin/adminDashboard";
        } else {
            return "redirect:/"; // Redirect to login page
        }
    }

    // 2. Doctor Dashboard
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        Map<String, String> validation = service.validateToken(token, "doctor").getBody();
        if (validation == null || validation.isEmpty()) {
            return "doctor/doctorDashboard";
        } else {
            return "redirect:/"; // Redirect to login page
        }
    }
}
