package com.clinique.gestion.dto;

import com.clinique.gestion.enums.ModePaiement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour l'entité Paiement
 */
public class PaiementDTO {

    private Long id;

    @NotNull(message = "Le patient est obligatoire")
    private Long patientId;

    private String patientNom;
    private String patientPrenom;

    private Long ticketId;
    private String ticketNumero;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal montant;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    @NotNull(message = "La date est obligatoire")
    private LocalDateTime date;

    @NotNull(message = "Le caissier est obligatoire")
    private Long caissierId;

    private String caissierNom;

    @Size(max = 30)
    private String numeroRecu;

    private LocalDateTime createdAt;

    public PaiementDTO() {
    }

    // Getters and Setters
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

    public String getPatientPrenom() {
        return patientPrenom;
    }

    public void setPatientPrenom(String patientPrenom) {
        this.patientPrenom = patientPrenom;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketNumero() {
        return ticketNumero;
    }

    public void setTicketNumero(String ticketNumero) {
        this.ticketNumero = ticketNumero;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public ModePaiement getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(ModePaiement modePaiement) {
        this.modePaiement = modePaiement;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getCaissierId() {
        return caissierId;
    }

    public void setCaissierId(Long caissierId) {
        this.caissierId = caissierId;
    }

    public String getCaissierNom() {
        return caissierNom;
    }

    public void setCaissierNom(String caissierNom) {
        this.caissierNom = caissierNom;
    }

    public String getNumeroRecu() {
        return numeroRecu;
    }

    public void setNumeroRecu(String numeroRecu) {
        this.numeroRecu = numeroRecu;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
