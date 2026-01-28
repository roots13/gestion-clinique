package com.clinique.gestion.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité représentant un token de réinitialisation de mot de passe
 */
@Entity
@Table(name = "password_reset_tokens", indexes = {
    @Index(name = "idx_reset_token", columnList = "token"),
    @Index(name = "idx_reset_user", columnList = "user_id"),
    @Index(name = "idx_reset_expiry", columnList = "expiry_date")
})
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "used", nullable = false)
    private Boolean used = false;

    // Constructors
    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, User user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
        this.createdAt = LocalDateTime.now();
        this.used = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    /**
     * Vérifie si le token est expiré
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    /**
     * Vérifie si le token est valide (non expiré et non utilisé)
     */
    public boolean isValid() {
        return !isExpired() && !used;
    }
}
