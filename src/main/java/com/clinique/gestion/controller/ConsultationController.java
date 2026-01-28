package com.clinique.gestion.controller;

import com.clinique.gestion.dto.ConsultationDTO;
import com.clinique.gestion.service.ConsultationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST pour la gestion des consultations
 */
@RestController
@RequestMapping("/api/consultations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    /**
     * Crée une nouvelle consultation (ADMIN, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    @PostMapping
    public ResponseEntity<ConsultationDTO> createConsultation(
            @RequestParam Long patientId,
            @RequestParam Long medecinId,
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        String diagnostic = request.get("diagnostic");
        String prescription = request.get("prescription");

        ConsultationDTO created = consultationService.createConsultation(
                patientId, medecinId, diagnostic, prescription, httpRequest);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Met à jour une consultation (ADMIN, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ConsultationDTO> updateConsultation(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        String diagnostic = request.get("diagnostic");
        String prescription = request.get("prescription");

        ConsultationDTO updated = consultationService.updateConsultation(
                id, diagnostic, prescription, httpRequest);
        return ResponseEntity.ok(updated);
    }

    /**
     * Récupère une consultation par son ID (ADMIN, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ConsultationDTO> getConsultationById(@PathVariable Long id) {
        ConsultationDTO consultation = consultationService.getConsultationById(id);
        return ResponseEntity.ok(consultation);
    }

    /**
     * Récupère les consultations d'un patient (ADMIN, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ConsultationDTO>> getConsultationsByPatient(@PathVariable Long patientId) {
        List<ConsultationDTO> consultations = consultationService.getConsultationsByPatient(patientId);
        return ResponseEntity.ok(consultations);
    }

    /**
     * Récupère les consultations d'un médecin (ADMIN, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<ConsultationDTO>> getConsultationsByMedecin(@PathVariable Long medecinId) {
        List<ConsultationDTO> consultations = consultationService.getConsultationsByMedecin(medecinId);
        return ResponseEntity.ok(consultations);
    }

    /**
     * Récupère toutes les consultations (ADMIN, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    @GetMapping
    public ResponseEntity<List<ConsultationDTO>> getAllConsultations() {
        List<ConsultationDTO> consultations = consultationService.getAllConsultations();
        return ResponseEntity.ok(consultations);
    }

    /**
     * Supprime une consultation (ADMIN, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteConsultation(
            @PathVariable Long id,
            HttpServletRequest request) {
        consultationService.deleteConsultation(id, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Consultation supprimée avec succès");
        return ResponseEntity.ok(response);
    }
}
