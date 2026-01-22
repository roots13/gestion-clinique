package com.clinique.gestion.enums;

/**
 * Énumération des rôles utilisateurs dans l'application
 */
public enum Role {
    ADMIN,      // Administrateur - accès total
    ACCUEIL,    // Accueil - gestion patients et tickets
    MEDECIN,    // Médecin - consultations et patients
    CAISSIER    // Caissier - gestion caisse uniquement
}
