package com.clinique.gestion.service;

import com.clinique.gestion.dto.TicketDTO;
import com.clinique.gestion.entity.Patient;
import com.clinique.gestion.entity.Ticket;
import com.clinique.gestion.enums.StatutTicket;
import com.clinique.gestion.exception.ResourceNotFoundException;
import com.clinique.gestion.repository.PatientRepository;
import com.clinique.gestion.repository.TicketRepository;
import com.clinique.gestion.util.NumeroGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des tickets
 */
@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private NumeroGenerator numeroGenerator;

    @Autowired
    private AuditService auditService;

    /**
     * Crée un nouveau ticket pour un patient
     */
    public TicketDTO createTicket(Long patientId, String service, HttpServletRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        // Générer le numéro de ticket
        String numero = numeroGenerator.generateTicketNumero(service);
        while (ticketRepository.existsByNumero(numero)) {
            numero = numeroGenerator.generateTicketNumero(service);
        }

        Ticket ticket = new Ticket();
        ticket.setNumero(numero);
        ticket.setPatient(patient);
        ticket.setService(service);
        ticket.setStatut(StatutTicket.EN_ATTENTE);

        ticket = ticketRepository.save(ticket);

        // Audit
        auditService.logAction("CREATE", "Ticket", ticket.getId(),
                "Création du ticket " + ticket.getNumero() + " pour le service " + service, request);

        return entityToDTO(ticket);
    }

    /**
     * Met à jour le statut d'un ticket
     */
    public TicketDTO updateTicketStatut(Long ticketId, StatutTicket statut, HttpServletRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        ticket.setStatut(statut);
        ticket = ticketRepository.save(ticket);

        // Audit
        auditService.logAction("UPDATE", "Ticket", ticket.getId(),
                "Mise à jour du statut du ticket " + ticket.getNumero() + " vers " + statut, request);

        return entityToDTO(ticket);
    }

    /**
     * Récupère un ticket par son ID
     */
    @Transactional(readOnly = true)
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
        return entityToDTO(ticket);
    }

    /**
     * Récupère la file d'attente d'un service (tickets EN_ATTENTE)
     */
    @Transactional(readOnly = true)
    public List<TicketDTO> getQueueByService(String service) {
        return ticketRepository.findQueueByService(service, StatutTicket.EN_ATTENTE)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les tickets d'un service
     */
    @Transactional(readOnly = true)
    public List<TicketDTO> getTicketsByService(String service) {
        return ticketRepository.findByService(service)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les tickets d'un patient
     */
    @Transactional(readOnly = true)
    public List<TicketDTO> getTicketsByPatient(Long patientId) {
        return ticketRepository.findByPatientId(patientId)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les tickets par statut
     */
    @Transactional(readOnly = true)
    public List<TicketDTO> getTicketsByStatut(StatutTicket statut) {
        return ticketRepository.findByStatut(statut)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère le prochain ticket en attente pour un service
     */
    @Transactional(readOnly = true)
    public TicketDTO getNextTicket(String service) {
        List<Ticket> queue = ticketRepository.findQueueByService(service, StatutTicket.EN_ATTENTE);
        if (queue.isEmpty()) {
            throw new ResourceNotFoundException("Aucun ticket en attente pour le service: " + service);
        }
        return entityToDTO(queue.get(0));
    }

    /**
     * Convertit une entité Ticket en DTO
     */
    private TicketDTO entityToDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setNumero(ticket.getNumero());
        dto.setPatientId(ticket.getPatient().getId());
        dto.setPatientNom(ticket.getPatient().getNom());
        dto.setPatientPrenom(ticket.getPatient().getPrenom());
        dto.setService(ticket.getService());
        dto.setStatut(ticket.getStatut());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        return dto;
    }
}
