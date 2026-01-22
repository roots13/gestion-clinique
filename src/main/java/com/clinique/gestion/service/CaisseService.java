package com.clinique.gestion.service;

import com.clinique.gestion.dto.PaiementDTO;
import com.clinique.gestion.entity.Paiement;
import com.clinique.gestion.entity.Patient;
import com.clinique.gestion.entity.Ticket;
import com.clinique.gestion.entity.User;
import com.clinique.gestion.exception.ResourceNotFoundException;
import com.clinique.gestion.repository.PaiementRepository;
import com.clinique.gestion.repository.PatientRepository;
import com.clinique.gestion.repository.TicketRepository;
import com.clinique.gestion.repository.UserRepository;
import com.clinique.gestion.util.NumeroGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion de la caisse (paiements)
 */
@Service
@Transactional
public class CaisseService {

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NumeroGenerator numeroGenerator;

    @Autowired
    private AuditService auditService;

    /**
     * Enregistre un nouveau paiement
     */
    public PaiementDTO enregistrerPaiement(PaiementDTO paiementDTO, HttpServletRequest request) {
        // Récupérer le patient
        Patient patient = patientRepository.findById(paiementDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", paiementDTO.getPatientId()));

        // Récupérer le ticket si fourni
        Ticket ticket = null;
        if (paiementDTO.getTicketId() != null) {
            ticket = ticketRepository.findById(paiementDTO.getTicketId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", paiementDTO.getTicketId()));
        }

        // Récupérer le caissier (utilisateur actuel)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new com.clinique.gestion.exception.UnauthorizedException("Utilisateur non authentifié");
        }

        String username = authentication.getName();
        User caissier = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Vérifier que l'utilisateur a le rôle CAISSIER ou ADMIN
        if (!caissier.getRole().name().equals("CAISSIER") && !caissier.getRole().name().equals("ADMIN")) {
            throw new com.clinique.gestion.exception.ForbiddenException(
                    "Seuls les caissiers peuvent enregistrer des paiements");
        }

        // Générer le numéro de reçu
        String numeroRecu = numeroGenerator.generateRecuNumero();
        while (paiementRepository.existsByNumeroRecu(numeroRecu)) {
            numeroRecu = numeroGenerator.generateRecuNumero();
        }

        // Créer le paiement
        Paiement paiement = new Paiement();
        paiement.setPatient(patient);
        paiement.setTicket(ticket);
        paiement.setMontant(paiementDTO.getMontant());
        paiement.setModePaiement(paiementDTO.getModePaiement());
        paiement.setDate(paiementDTO.getDate() != null ? paiementDTO.getDate() : LocalDateTime.now());
        paiement.setCaissier(caissier);
        paiement.setNumeroRecu(numeroRecu);

        paiement = paiementRepository.save(paiement);

        // Audit
        auditService.logAction("CREATE", "Paiement", paiement.getId(),
                "Enregistrement du paiement " + paiement.getNumeroRecu() + 
                " - Montant: " + paiement.getMontant() + " - Mode: " + paiement.getModePaiement(), request);

        return entityToDTO(paiement);
    }

    /**
     * Récupère un paiement par son ID
     */
    @Transactional(readOnly = true)
    public PaiementDTO getPaiementById(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", "id", id));
        return entityToDTO(paiement);
    }

    /**
     * Récupère un paiement par son numéro de reçu
     */
    @Transactional(readOnly = true)
    public PaiementDTO getPaiementByNumeroRecu(String numeroRecu) {
        Paiement paiement = paiementRepository.findByNumeroRecu(numeroRecu)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", "numeroRecu", numeroRecu));
        return entityToDTO(paiement);
    }

    /**
     * Récupère tous les paiements d'un patient
     */
    @Transactional(readOnly = true)
    public List<PaiementDTO> getPaiementsByPatient(Long patientId) {
        return paiementRepository.findByPatientId(patientId)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calcule le total des recettes pour une période
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalRecettes(LocalDateTime start, LocalDateTime end) {
        BigDecimal total = paiementRepository.calculateTotalBetweenDates(start, end);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Récupère les paiements dans une période
     */
    @Transactional(readOnly = true)
    public List<PaiementDTO> getPaiementsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return paiementRepository.findPaiementsBetweenDates(start, end)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité Paiement en DTO
     */
    private PaiementDTO entityToDTO(Paiement paiement) {
        PaiementDTO dto = new PaiementDTO();
        dto.setId(paiement.getId());
        dto.setPatientId(paiement.getPatient().getId());
        dto.setPatientNom(paiement.getPatient().getNom());
        dto.setPatientPrenom(paiement.getPatient().getPrenom());
        if (paiement.getTicket() != null) {
            dto.setTicketId(paiement.getTicket().getId());
            dto.setTicketNumero(paiement.getTicket().getNumero());
        }
        dto.setMontant(paiement.getMontant());
        dto.setModePaiement(paiement.getModePaiement());
        dto.setDate(paiement.getDate());
        dto.setCaissierId(paiement.getCaissier().getId());
        dto.setCaissierNom(paiement.getCaissier().getUsername());
        dto.setNumeroRecu(paiement.getNumeroRecu());
        dto.setCreatedAt(paiement.getCreatedAt());
        return dto;
    }
}
