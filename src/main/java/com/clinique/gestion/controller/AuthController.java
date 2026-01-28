package com.clinique.gestion.controller;

import com.clinique.gestion.dto.JwtResponse;
import com.clinique.gestion.dto.LoginRequest;
import com.clinique.gestion.dto.PasswordResetRequest;
import com.clinique.gestion.dto.ResetPasswordRequest;
import com.clinique.gestion.service.AuthService;
import com.clinique.gestion.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller pour l'authentification
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Endpoint de connexion
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        JwtResponse response = authService.authenticateUser(loginRequest, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour demander la réinitialisation du mot de passe
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.sendPasswordResetEmail(request.getEmail());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Un email de réinitialisation a été envoyé à votre adresse email");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour réinitialiser le mot de passe
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Votre mot de passe a été réinitialisé avec succès");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour valider un token de réinitialisation
     */
    @GetMapping("/validate-reset-token/{token}")
    public ResponseEntity<Map<String, Boolean>> validateResetToken(@PathVariable String token) {
        boolean isValid = passwordResetService.validateToken(token);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", isValid);
        return ResponseEntity.ok(response);
    }
}
