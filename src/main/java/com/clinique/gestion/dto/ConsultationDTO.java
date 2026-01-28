package com.clinique.gestion.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO pour les consultations
 */
public class ConsultationDTO implements Serializable {

    private Long id;
    private Long patientId;
    private String patientNom;
    private Long medecinId;
    private String medecinNom;
    private LocalDateTime date;
    private String motif;
    private String diagnostic;
    private String prescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeurs
    public ConsultationDTO() {
    }

    public ConsultationDTO(Long id, Long patientId, String patientNom, Long medecinId,
                          String medecinNom, LocalDateTime date, String motif,
                          String diagnostic, String prescription,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.patientNom = patientNom;
        this.medecinId = medecinId;
        this.medecinNom = medecinNom;
        this.date = date;
        this.motif = motif;
        this.diagnostic = diagnostic;
        this.prescription = prescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientNom() {
        return patientNom;
    }

    public void setPatientNom(String patientNom) {
        this.patientNom = patientNom;
    }

    public Long getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(Long medecinId) {
        this.medecinId = medecinId;
    }

    public String getMedecinNom() {
        return medecinNom;
    }

    public void setMedecinNom(String medecinNom) {
        this.medecinNom = medecinNom;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
