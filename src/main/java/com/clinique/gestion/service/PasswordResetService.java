package com.clinique.gestion.service;

import com.clinique.gestion.entity.PasswordResetToken;
import com.clinique.gestion.entity.User;
import com.clinique.gestion.exception.BadRequestException;
import com.clinique.gestion.exception.ResourceNotFoundException;
import com.clinique.gestion.repository.PasswordResetTokenRepository;
import com.clinique.gestion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service pour la gestion de la réinitialisation de mot de passe
 */
@Service
@Transactional
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private static final int TOKEN_EXPIRY_HOURS = 24;

    /**
     * Génère et envoie un lien de réinitialisation de mot de passe
     */
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur avec l'email " + email + " non trouvé"));

        // Invalider les tokens précédents
        passwordResetTokenRepository.findLatestUnusedToken(user)
                .ifPresent(token -> {
                    token.setUsed(true);
                    passwordResetTokenRepository.save(token);
                });

        // Générer un nouveau token
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS);
        
        PasswordResetToken token = new PasswordResetToken(tokenValue, user, expiryDate);
        passwordResetTokenRepository.save(token);

        // Envoyer l'email avec le lien de réinitialisation
        String resetLink = "http://localhost:8080/reset-password?token=" + tokenValue;
        String subject = "Réinitialisation de votre mot de passe - Gestion Clinique";
        String message = "Bonjour " + user.getPrenom() + " " + user.getNom() + ",\n\n" +
                "Vous avez demandé la réinitialisation de votre mot de passe.\n" +
                "Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n\n" +
                resetLink + "\n\n" +
                "Ce lien expirera dans " + TOKEN_EXPIRY_HOURS + " heures.\n\n" +
                "Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.\n\n" +
                "Cordialement,\nL'équipe Gestion Clinique";

        emailService.sendEmail(email, subject, message);
    }

    /**
     * Réinitialise le mot de passe avec le token fourni
     */
    public void resetPassword(String tokenValue, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("Les mots de passe ne correspondent pas");
        }

        PasswordResetToken token = passwordResetTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BadRequestException("Token de réinitialisation invalide"));

        if (!token.isValid()) {
            throw new BadRequestException("Le token de réinitialisation a expiré ou a déjà été utilisé");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marquer le token comme utilisé
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }

    /**
     * Valide un token de réinitialisation
     */
    public boolean validateToken(String tokenValue) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(tokenValue)
                .orElse(null);
        return token != null && token.isValid();
    }

    /**
     * Nettoie les tokens expirés (à appeler périodiquement)
     */
    public void cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
