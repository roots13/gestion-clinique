package com.clinique.gestion.controller;

import com.clinique.gestion.dto.PaiementDTO;
import com.clinique.gestion.service.CaisseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller REST pour la gestion de la caisse
 */
@RestController
@RequestMapping("/api/paiements")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CaisseController {

    @Autowired
    private CaisseService caisseService;

    /**
     * Enregistre un nouveau paiement (ADMIN, CAISSIER)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'CAISSIER')")
    @PostMapping
    public ResponseEntity<PaiementDTO> enregistrerPaiement(
            @Valid @RequestBody PaiementDTO paiementDTO,
            HttpServletRequest request) {
        PaiementDTO created = caisseService.enregistrerPaiement(paiementDTO, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Récupère un paiement par son ID (ADMIN, CAISSIER)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'CAISSIER')")
    @GetMapping("/{id}")
    public ResponseEntity<PaiementDTO> getPaiementById(@PathVariable Long id) {
        PaiementDTO paiement = caisseService.getPaiementById(id);
        return ResponseEntity.ok(paiement);
    }

    /**
     * Récupère un paiement par son numéro de reçu (ADMIN, CAISSIER)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'CAISSIER')")
    @GetMapping("/recu/{numeroRecu}")
    public ResponseEntity<PaiementDTO> getPaiementByNumeroRecu(@PathVariable String numeroRecu) {
        PaiementDTO paiement = caisseService.getPaiementByNumeroRecu(numeroRecu);
        return ResponseEntity.ok(paiement);
    }

    /**
     * Récupère tous les paiements d'un patient (ADMIN, CAISSIER)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'CAISSIER')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PaiementDTO>> getPaiementsByPatient(@PathVariable Long patientId) {
        List<PaiementDTO> paiements = caisseService.getPaiementsByPatient(patientId);
        return ResponseEntity.ok(paiements);
    }

    /**
     * Calcule le total des recettes pour une période (ADMIN, CAISSIER)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'CAISSIER')")
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalRecettes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        BigDecimal total = caisseService.getTotalRecettes(start, end);
        return ResponseEntity.ok(total);
    }

    /**
     * Récupère les paiements dans une période (ADMIN, CAISSIER)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'CAISSIER')")
    @GetMapping("/periode")
    public ResponseEntity<List<PaiementDTO>> getPaiementsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<PaiementDTO> paiements = caisseService.getPaiementsBetweenDates(start, end);
        return ResponseEntity.ok(paiements);
    }
}
