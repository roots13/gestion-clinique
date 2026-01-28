package com.clinique.gestion.controller;

import com.clinique.gestion.dto.PatientDTO;
import com.clinique.gestion.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.io.ByteArrayOutputStream;

/**
 * Controller REST pour la gestion des patients
 */
@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * Crée un nouveau patient (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(
            @Valid @RequestBody PatientDTO patientDTO,
            HttpServletRequest request) {
        PatientDTO created = patientService.createPatient(patientDTO, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Met à jour un patient (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO patientDTO,
            HttpServletRequest request) {
        PatientDTO updated = patientService.updatePatient(id, patientDTO, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Récupère un patient par son ID (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    /**
     * Récupère un patient par son numéro (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/numero/{numero}")
    public ResponseEntity<PatientDTO> getPatientByNumero(@PathVariable String numero) {
        PatientDTO patient = patientService.getPatientByNumero(numero);
        return ResponseEntity.ok(patient);
    }

    /**
     * Recherche des patients (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/search")
    public ResponseEntity<List<PatientDTO>> searchPatients(@RequestParam String q) {
        List<PatientDTO> patients = patientService.searchPatients(q);
        return ResponseEntity.ok(patients);
    }

    /**
     * Récupère tous les patients (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    /**
     * Supprime un patient (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id, HttpServletRequest request) {
        patientService.deletePatient(id, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Télécharge le dossier patient en PDF (ADMIN, ACCUEIL, MEDECIN)
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCUEIL', 'MEDECIN')")
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPatientDossier(@PathVariable Long id) {
        try {
            byte[] pdfBytes = patientService.generatePatientDossierPDF(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(org.springframework.http.ContentDisposition
                    .attachment()
                    .filename("dossier_patient_" + id + ".pdf")
                    .build());
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
