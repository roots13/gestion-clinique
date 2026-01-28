package com.clinique.gestion.service;

import com.clinique.gestion.dto.PatientDTO;
import com.clinique.gestion.entity.Patient;
import com.clinique.gestion.exception.ResourceNotFoundException;
import com.clinique.gestion.repository.PatientRepository;
import com.clinique.gestion.util.NumeroGenerator;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
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

    /**
     * Génère un PDF du dossier patient
     */
    public byte[] generatePatientDossierPDF(Long patientId) throws Exception {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Titre
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("DOSSIER PATIENT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));

        // Informations personnelles
        Font headingFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph heading = new Paragraph("Informations Personnelles", headingFont);
        document.add(heading);

        // Tableau des informations
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        // Ajouter les lignes du tableau
        addTableRow(table, "Numéro Patient:", patient.getNumero());
        addTableRow(table, "Nom:", patient.getNom());
        addTableRow(table, "Prénom:", patient.getPrenom());
        addTableRow(table, "Date de Naissance:", patient.getDateNaissance() != null ? patient.getDateNaissance().toString() : "");
        addTableRow(table, "Téléphone:", patient.getTelephone() != null ? patient.getTelephone() : "-");
        addTableRow(table, "Adresse:", patient.getAdresse() != null ? patient.getAdresse() : "-");

        document.add(table);
        document.add(new Paragraph(" "));

        // Date de génération
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
        Paragraph footer = new Paragraph("Document généré le: " + LocalDateTime.now(), smallFont);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    /**
     * Ajoute une ligne dans le tableau PDF
     */
    private void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell cellLabel = new PdfPCell(new Paragraph(label));
        cellLabel.setPadding(5);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Paragraph(value != null ? value : ""));
        cellValue.setPadding(5);
        table.addCell(cellValue);
    }
}
