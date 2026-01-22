package com.clinique.gestion.entity;

import com.clinique.gestion.enums.ModePaiement;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité représentant un paiement
 */
@Entity
@Table(name = "paiements", indexes = {
    @Index(name = "idx_paiements_patient", columnList = "patient_id"),
    @Index(name = "idx_paiements_ticket", columnList = "ticket_id"),
    @Index(name = "idx_paiements_caissier", columnList = "caissier_id"),
    @Index(name = "idx_paiements_date", columnList = "date"),
    @Index(name = "idx_paiements_numero_recu", columnList = "numero_recu")
})
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le patient est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @NotNull(message = "Le mode de paiement est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, length = 20)
    private ModePaiement modePaiement;

    @NotNull(message = "La date est obligatoire")
    @Column(nullable = false)
    private LocalDateTime date;

    @NotNull(message = "Le caissier est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caissier_id", nullable = false)
    private User caissier;

    @NotBlank(message = "Le numéro de reçu est obligatoire")
    @Size(max = 30)
    @Column(name = "numero_recu", unique = true, nullable = false, length = 30)
    private String numeroRecu;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDateTime.now();
        }
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Paiement() {
    }

    public Paiement(Patient patient, BigDecimal montant, ModePaiement modePaiement, User caissier) {
        this.patient = patient;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.caissier = caissier;
        this.date = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
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

    public User getCaissier() {
        return caissier;
    }

    public void setCaissier(User caissier) {
        this.caissier = caissier;
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
