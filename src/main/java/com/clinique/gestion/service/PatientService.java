package com.clinique.gestion.service;

import com.clinique.gestion.dto.PatientDTO;
import com.clinique.gestion.entity.Patient;
import com.clinique.gestion.exception.ResourceNotFoundException;
import com.clinique.gestion.repository.PatientRepository;
import com.clinique.gestion.util.NumeroGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des patients
 */
@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private NumeroGenerator numeroGenerator;

    @Autowired
    private AuditService auditService;

    /**
     * Crée un nouveau patient
     */
    public PatientDTO createPatient(PatientDTO patientDTO, HttpServletRequest request) {
        // Générer le numéro patient si non fourni
        if (patientDTO.getNumero() == null || patientDTO.getNumero().isEmpty()) {
            String numero = numeroGenerator.generatePatientNumero();
            // Vérifier l'unicité
            while (patientRepository.existsByNumero(numero)) {
                numero = numeroGenerator.generatePatientNumero();
            }
            patientDTO.setNumero(numero);
        } else {
            // Vérifier que le numéro n'existe pas déjà
            if (patientRepository.existsByNumero(patientDTO.getNumero())) {
                throw new com.clinique.gestion.exception.BadRequestException(
                        "Un patient avec ce numéro existe déjà");
            }
        }

        Patient patient = dtoToEntity(patientDTO);
        patient = patientRepository.save(patient);

        // Audit
        auditService.logAction("CREATE", "Patient", patient.getId(),
                "Création du patient: " + patient.getNom() + " " + patient.getPrenom(), request);

        return entityToDTO(patient);
    }

    /**
     * Met à jour un patient existant
     */
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO, HttpServletRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));

        // Vérifier l'unicité du numéro si modifié
        if (!patient.getNumero().equals(patientDTO.getNumero()) &&
                patientRepository.existsByNumero(patientDTO.getNumero())) {
            throw new com.clinique.gestion.exception.BadRequestException(
                    "Un patient avec ce numéro existe déjà");
        }

        patient.setNumero(patientDTO.getNumero());
        patient.setNom(patientDTO.getNom());
        patient.setPrenom(patientDTO.getPrenom());
        patient.setDateNaissance(patientDTO.getDateNaissance());
        patient.setTelephone(patientDTO.getTelephone());
        patient.setAdresse(patientDTO.getAdresse());

        patient = patientRepository.save(patient);

        // Audit
        auditService.logAction("UPDATE", "Patient", patient.getId(),
                "Modification du patient: " + patient.getNom() + " " + patient.getPrenom(), request);

        return entityToDTO(patient);
    }

    /**
     * Récupère un patient par son ID
     */
    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        return entityToDTO(patient);
    }

    /**
     * Récupère un patient par son numéro
     */
    @Transactional(readOnly = true)
    public PatientDTO getPatientByNumero(String numero) {
        Patient patient = patientRepository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "numero", numero));
        return entityToDTO(patient);
    }

    /**
     * Recherche des patients
     */
    @Transactional(readOnly = true)
    public List<PatientDTO> searchPatients(String search) {
        return patientRepository.searchPatients(search)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les patients
     */
    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Supprime un patient
     */
    public void deletePatient(Long id, HttpServletRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));

        // Audit avant suppression
        auditService.logAction("DELETE", "Patient", patient.getId(),
                "Suppression du patient: " + patient.getNom() + " " + patient.getPrenom(), request);

        patientRepository.delete(patient);
    }

    /**
     * Convertit une entité Patient en DTO
     */
    private PatientDTO entityToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setNumero(patient.getNumero());
        dto.setNom(patient.getNom());
        dto.setPrenom(patient.getPrenom());
        dto.setDateNaissance(patient.getDateNaissance());
        dto.setTelephone(patient.getTelephone());
        dto.setAdresse(patient.getAdresse());
        return dto;
    }

    /**
     * Convertit un DTO en entité Patient
     */
    private Patient dtoToEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setNumero(dto.getNumero());
        patient.setNom(dto.getNom());
        patient.setPrenom(dto.getPrenom());
        patient.setDateNaissance(dto.getDateNaissance());
        patient.setTelephone(dto.getTelephone());
        patient.setAdresse(dto.getAdresse());
        return patient;
    }
}
