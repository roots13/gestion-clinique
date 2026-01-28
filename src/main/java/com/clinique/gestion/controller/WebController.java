package com.clinique.gestion.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller pour les pages web (Thymeleaf)
 */
@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset-password";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/patients")
    public String patients() {
        return "patients";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/patient-dossier")
    public String patientDossier() {
        return "patient-dossier";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/tickets")
    public String tickets() {
        return "tickets";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CAISSIER')")
    @GetMapping("/caisse")
    public String caisse() {
        return "caisse";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    @GetMapping("/consultations")
    public String consultations() {
        return "consultations";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String users() {
        return "users";
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }
}
