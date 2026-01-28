package com.clinique.gestion.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO pour la demande de réinitialisation de mot de passe
 */
public class PasswordResetRequest {

    @NotBlank(message = "L'email est obligatoire")
    private String email;

    public PasswordResetRequest() {
    }

    public PasswordResetRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
