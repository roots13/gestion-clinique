package com.clinique.gestion.controller;

import com.clinique.gestion.dto.PatientDTO;
import com.clinique.gestion.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * Crée un nouveau patient
     */
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(
            @Valid @RequestBody PatientDTO patientDTO,
            HttpServletRequest request) {
        PatientDTO created = patientService.createPatient(patientDTO, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Met à jour un patient
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO patientDTO,
            HttpServletRequest request) {
        PatientDTO updated = patientService.updatePatient(id, patientDTO, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Récupère un patient par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    /**
     * Récupère un patient par son numéro
     */
    @GetMapping("/numero/{numero}")
    public ResponseEntity<PatientDTO> getPatientByNumero(@PathVariable String numero) {
        PatientDTO patient = patientService.getPatientByNumero(numero);
        return ResponseEntity.ok(patient);
    }

    /**
     * Recherche des patients
     */
    @GetMapping("/search")
    public ResponseEntity<List<PatientDTO>> searchPatients(@RequestParam String q) {
        List<PatientDTO> patients = patientService.searchPatients(q);
        return ResponseEntity.ok(patients);
    }

    /**
     * Récupère tous les patients
     */
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    /**
     * Supprime un patient
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id, HttpServletRequest request) {
        patientService.deletePatient(id, request);
        return ResponseEntity.noContent().build();
    }
}
