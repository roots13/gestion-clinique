package com.clinique.gestion.service;

import com.clinique.gestion.dto.ConsultationDTO;
import com.clinique.gestion.entity.Consultation;
import com.clinique.gestion.entity.Patient;
import com.clinique.gestion.entity.User;
import com.clinique.gestion.exception.ResourceNotFoundException;
import com.clinique.gestion.repository.ConsultationRepository;
import com.clinique.gestion.repository.PatientRepository;
import com.clinique.gestion.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des consultations
 */
@Service
@Transactional
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditService auditService;

    /**
     * Crée une nouvelle consultation
     */
    public ConsultationDTO createConsultation(Long patientId, Long medecinId,
                                             String diagnostic, String prescription,
                                             HttpServletRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        User medecin = userRepository.findById(medecinId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin", "id", medecinId));

        Consultation consultation = new Consultation();
        consultation.setPatient(patient);
        consultation.setMedecin(medecin);
        consultation.setDiagnostic(diagnostic);
        consultation.setPrescription(prescription);
        consultation.setDate(LocalDateTime.now());

        consultation = consultationRepository.save(consultation);

        // Audit
        auditService.logAction("CREATE", "Consultation", consultation.getId(),
                "Création de la consultation pour le patient: " + patient.getNom(), request);

        return entityToDTO(consultation);
    }

    /**
     * Met à jour une consultation
     */
    public ConsultationDTO updateConsultation(Long id, String diagnostic, String prescription,
                                             HttpServletRequest request) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation", "id", id));

        consultation.setDiagnostic(diagnostic);
        consultation.setPrescription(prescription);
        consultation.setUpdatedAt(LocalDateTime.now());

        consultation = consultationRepository.save(consultation);

        // Audit
        auditService.logAction("UPDATE", "Consultation", consultation.getId(),
                "Modification de la consultation", request);

        return entityToDTO(consultation);
    }

    /**
     * Récupère une consultation par son ID
     */
    public ConsultationDTO getConsultationById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation", "id", id));
        return entityToDTO(consultation);
    }

    /**
     * Récupère les consultations d'un patient
     */
    public List<ConsultationDTO> getConsultationsByPatient(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        return consultationRepository.findConsultationsByPatientOrderByDateDesc(patientId)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les consultations d'un médecin
     */
    public List<ConsultationDTO> getConsultationsByMedecin(Long medecinId) {
        userRepository.findById(medecinId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin", "id", medecinId));

        return consultationRepository.findByMedecinId(medecinId)
                .stream()
                .sorted((c1, c2) -> c2.getDate().compareTo(c1.getDate()))
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les consultations
     */
    public List<ConsultationDTO> getAllConsultations() {
        return consultationRepository.findAll()
                .stream()
                .sorted((c1, c2) -> c2.getDate().compareTo(c1.getDate()))
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Supprime une consultation
     */
    public void deleteConsultation(Long id, HttpServletRequest request) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation", "id", id));

        consultationRepository.deleteById(id);

        // Audit
        auditService.logAction("DELETE", "Consultation", id,
                "Suppression de la consultation", request);
    }

    /**
     * Convertit une entité Consultation en DTO
     */
    private ConsultationDTO entityToDTO(Consultation consultation) {
        return new ConsultationDTO(
                consultation.getId(),
                consultation.getPatient().getId(),
                consultation.getPatient().getNom() + " " + consultation.getPatient().getPrenom(),
                consultation.getMedecin().getId(),
                consultation.getMedecin().getNom() + " " + consultation.getMedecin().getPrenom(),
                consultation.getDate(),
                consultation.getMotif(),
                consultation.getDiagnostic(),
                consultation.getPrescription(),
                consultation.getCreatedAt(),
                consultation.getUpdatedAt()
        );
    }
}
