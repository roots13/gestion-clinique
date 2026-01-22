package com.clinique.gestion.controller;

import com.clinique.gestion.dto.JwtResponse;
import com.clinique.gestion.dto.LoginRequest;
import com.clinique.gestion.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour l'authentification
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

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
}
