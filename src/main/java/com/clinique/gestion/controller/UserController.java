package com.clinique.gestion.controller;

import com.clinique.gestion.dto.UserDTO;
import com.clinique.gestion.enums.Role;
import com.clinique.gestion.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST pour la gestion des utilisateurs
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Récupère l'utilisateur actuellement connecté
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Change le mot de passe de l'utilisateur actuellement connecté
     */
    @PostMapping("/me/change-password")
    public ResponseEntity<Map<String, String>> changeOwnPassword(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        userService.changeOwnPassword(username, oldPassword, newPassword, httpRequest);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Mot de passe changé avec succès");
        return ResponseEntity.ok(response);
    }

    /**
     * Crée un nouvel utilisateur (Admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        String username = (String) request.get("username");
        String email = (String) request.get("email");
        String nom = (String) request.get("nom");
        String prenom = (String) request.get("prenom");
        String roleStr = (String) request.get("role");
        String password = (String) request.get("password");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setEmail(email);
        userDTO.setNom(nom);
        userDTO.setPrenom(prenom);
        userDTO.setRole(Role.valueOf(roleStr));

        UserDTO created = userService.createUser(userDTO, password, httpRequest);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Met à jour un utilisateur (Admin seulement)
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO,
            HttpServletRequest request) {
        UserDTO updated = userService.updateUser(id, userDTO, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Change le mot de passe d'un utilisateur
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        String newPassword = request.get("newPassword");
        userService.changePassword(id, newPassword, httpRequest);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Mot de passe changé avec succès");
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère un utilisateur par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Récupère un utilisateur par son nom d'utilisateur
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Récupère tous les utilisateurs (Admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Récupère les utilisateurs par rôle (Admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        List<UserDTO> users = userService.getUsersByRole(Role.valueOf(role));
        return ResponseEntity.ok(users);
    }

    /**
     * Récupère les utilisateurs actifs (Admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active/list")
    public ResponseEntity<List<UserDTO>> getActiveUsers() {
        List<UserDTO> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Désactive un utilisateur (Admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateUser(
            @PathVariable Long id,
            HttpServletRequest request) {
        userService.deactivateUser(id, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Utilisateur désactivé avec succès");
        return ResponseEntity.ok(response);
    }

    /**
     * Réactive un utilisateur (Admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/enable")
    public ResponseEntity<Map<String, String>> enableUser(
            @PathVariable Long id,
            HttpServletRequest request) {
        userService.enableUser(id, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Utilisateur réactivé avec succès");
        return ResponseEntity.ok(response);
    }

    /**
     * Supprime un utilisateur (Admin seulement)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long id,
            HttpServletRequest request) {
        userService.deleteUser(id, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Utilisateur supprimé avec succès");
        return ResponseEntity.ok(response);
    }
}
