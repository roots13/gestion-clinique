package com.clinique.gestion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service d'envoi d'email (simple implémentation en logs)
 * À remplacer par une implémentation réelle (JavaMailSender, SendGrid, etc.)
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    /**
     * Envoie un email (actuellement logs seulement)
     * TODO: Intégrer un service d'email réel (SMTP, SendGrid, etc.)
     */
    public void sendEmail(String to, String subject, String message) {
        if (emailEnabled) {
            // Implémentation réelle à faire
            logger.info("Email à envoyer à: {}", to);
            logger.info("Sujet: {}", subject);
            logger.info("Message: {}", message);
        } else {
            logger.info("Service d'email désactivé. Email simulé:");
            logger.info("À: {}", to);
            logger.info("Sujet: {}", subject);
            logger.info("Message: {}", message);
        }
    }
}
