package com.clinique.gestion.controller;

import com.clinique.gestion.dto.TicketDTO;
import com.clinique.gestion.enums.StatutTicket;
import com.clinique.gestion.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour la gestion des tickets
 */
@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TicketController {

    @Autowired
    private TicketService ticketService;

    /**
     * Crée un nouveau ticket pour un patient (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @PostMapping
    public ResponseEntity<TicketDTO> createTicket(
            @RequestParam Long patientId,
            @RequestParam String service,
            HttpServletRequest request) {
        TicketDTO created = ticketService.createTicket(patientId, service, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Met à jour le statut d'un ticket (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @PutMapping("/{id}/statut")
    public ResponseEntity<TicketDTO> updateTicketStatut(
            @PathVariable Long id,
            @RequestParam StatutTicket statut,
            HttpServletRequest request) {
        TicketDTO updated = ticketService.updateTicketStatut(id, statut, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Récupère un ticket par son ID (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        TicketDTO ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Récupère la file d'attente d'un service (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/queue/{service}")
    public ResponseEntity<List<TicketDTO>> getQueueByService(@PathVariable String service) {
        List<TicketDTO> queue = ticketService.getQueueByService(service);
        return ResponseEntity.ok(queue);
    }

    /**
     * Récupère tous les tickets d'un service (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/service/{service}")
    public ResponseEntity<List<TicketDTO>> getTicketsByService(@PathVariable String service) {
        List<TicketDTO> tickets = ticketService.getTicketsByService(service);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Récupère tous les tickets d'un patient (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<TicketDTO>> getTicketsByPatient(@PathVariable Long patientId) {
        List<TicketDTO> tickets = ticketService.getTicketsByPatient(patientId);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Récupère tous les tickets par statut (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<TicketDTO>> getTicketsByStatut(@PathVariable StatutTicket statut) {
        List<TicketDTO> tickets = ticketService.getTicketsByStatut(statut);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Récupère le prochain ticket en attente pour un service (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/next/{service}")
    public ResponseEntity<TicketDTO> getNextTicket(@PathVariable String service) {
        TicketDTO ticket = ticketService.getNextTicket(service);
        return ResponseEntity.ok(ticket);
    }
}
