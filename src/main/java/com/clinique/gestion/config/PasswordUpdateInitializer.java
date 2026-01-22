package com.clinique.gestion.config;

import com.clinique.gestion.entity.User;
import com.clinique.gestion.enums.Role;
import com.clinique.gestion.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Composant qui met à jour automatiquement les mots de passe des utilisateurs par défaut
 * au démarrage de l'application si ils existent déjà avec d'anciens hashes
 */
@Component
public class PasswordUpdateInitializer {

    private static final Logger logger = LoggerFactory.getLogger(PasswordUpdateInitializer.class);

    @Autowired
    private UserRepository userRepository;

    // Hashes BCrypt valides pour les mots de passe par défaut
    private static final String ADMIN_PASSWORD_HASH = "$2a$10$aE17PFXtIvlG3rfVcAnrMOW4XC8KmL87ZHgvEYwaFqGAo96R77KZG"; // admin123
    private static final String ACCUEIL_PASSWORD_HASH = "$2a$10$oKqoAY7bEiPORmF/JXpOleIH9Jt8dXegO9QLhIIViBjAEqkVo06pu"; // accueil123
    private static final String MEDECIN_PASSWORD_HASH = "$2a$10$QEE1X7hu3Ay6J8vn4oy8Xu0fzfm1XA7mqBygWRktC6sMc7o9aSGdC"; // medecin123
    private static final String CAISSIER_PASSWORD_HASH = "$2a$10$zCCLBH5u/17xG6d0BQTdYOorKiSGBAX2woxhGhSKM075gHDw8AHUm"; // caissier123

    @PostConstruct
    @Transactional
    public void updateDefaultPasswords() {
        logger.info("Vérification et mise à jour des mots de passe par défaut...");

        // Mise à jour admin
        userRepository.findByUsername("admin").ifPresent(user -> {
            user.setPassword(ADMIN_PASSWORD_HASH);
            user.setEmail("admin@clinique.local");
            user.setRole(Role.ADMIN);
            user.setEnabled(true);
            userRepository.save(user);
            logger.info("Mot de passe admin mis à jour");
        });

        // Mise à jour accueil
        userRepository.findByUsername("accueil").ifPresent(user -> {
            user.setPassword(ACCUEIL_PASSWORD_HASH);
            user.setEmail("accueil@clinique.local");
            user.setRole(Role.ACCUEIL);
            user.setEnabled(true);
            userRepository.save(user);
            logger.info("Mot de passe accueil mis à jour");
        });

        // Mise à jour medecin
        userRepository.findByUsername("medecin").ifPresent(user -> {
            user.setPassword(MEDECIN_PASSWORD_HASH);
            user.setEmail("medecin@clinique.local");
            user.setRole(Role.MEDECIN);
            user.setEnabled(true);
            userRepository.save(user);
            logger.info("Mot de passe medecin mis à jour");
        });

        // Mise à jour caissier
        userRepository.findByUsername("caissier").ifPresent(user -> {
            user.setPassword(CAISSIER_PASSWORD_HASH);
            user.setEmail("caissier@clinique.local");
            user.setRole(Role.CAISSIER);
            user.setEnabled(true);
            userRepository.save(user);
            logger.info("Mot de passe caissier mis à jour");
        });

        // Création des utilisateurs s'ils n'existent pas
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@clinique.local", ADMIN_PASSWORD_HASH, Role.ADMIN);
            userRepository.save(admin);
            logger.info("Utilisateur admin créé");
        }

        if (!userRepository.existsByUsername("accueil")) {
            User accueil = new User("accueil", "accueil@clinique.local", ACCUEIL_PASSWORD_HASH, Role.ACCUEIL);
            userRepository.save(accueil);
            logger.info("Utilisateur accueil créé");
        }

        if (!userRepository.existsByUsername("medecin")) {
            User medecin = new User("medecin", "medecin@clinique.local", MEDECIN_PASSWORD_HASH, Role.MEDECIN);
            userRepository.save(medecin);
            logger.info("Utilisateur medecin créé");
        }

        if (!userRepository.existsByUsername("caissier")) {
            User caissier = new User("caissier", "caissier@clinique.local", CAISSIER_PASSWORD_HASH, Role.CAISSIER);
            userRepository.save(caissier);
            logger.info("Utilisateur caissier créé");
        }

        logger.info("Mise à jour des mots de passe terminée");
    }
}
