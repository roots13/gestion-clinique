package com.clinique.gestion.dto;

import com.clinique.gestion.enums.StatutTicket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO pour l'entité Ticket
 */
public class TicketDTO {

    private Long id;

    @NotBlank(message = "Le numéro de ticket est obligatoire")
    @Size(max = 20)
    private String numero;

    @NotNull(message = "Le patient est obligatoire")
    private Long patientId;

    private String patientNom;
    private String patientPrenom;

    @NotBlank(message = "Le service est obligatoire")
    @Size(max = 50)
    private String service;

    @NotNull(message = "Le statut est obligatoire")
    private StatutTicket statut;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TicketDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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

    public String getPatientPrenom() {
        return patientPrenom;
    }

    public void setPatientPrenom(String patientPrenom) {
        this.patientPrenom = patientPrenom;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public StatutTicket getStatut() {
        return statut;
    }

    public void setStatut(StatutTicket statut) {
        this.statut = statut;
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
