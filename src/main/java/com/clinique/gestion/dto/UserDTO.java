package com.clinique.gestion.dto;

import com.clinique.gestion.enums.Role;
import java.io.Serializable;

/**
 * DTO pour les utilisateurs
 */
public class UserDTO implements Serializable {

    private Long id;
    private String username;
    private String email;
    private String nom;
    private String prenom;
    private Role role;
    private boolean enabled;

    // Constructeurs
    public UserDTO() {
    }

    public UserDTO(Long id, String username, String email, String nom, String prenom,
                  Role role, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.enabled = enabled;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
