package com.clinique.gestion.service;

import com.clinique.gestion.entity.AuditLog;
import com.clinique.gestion.entity.User;
import com.clinique.gestion.repository.AuditLogRepository;
import com.clinique.gestion.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service pour la journalisation des actions (audit)
 */
@Service
@Transactional
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Enregistre une action dans le journal d'audit
     */
    public void logAction(String action, String entityType, Long entityId, String details, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog();
        
        // Récupérer l'utilisateur actuel
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            auditLog.setUser(user);
        }

        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        
        if (request != null) {
            auditLog.setIpAddress(getClientIpAddress(request));
        }

        auditLogRepository.save(auditLog);
    }

    /**
     * Enregistre une action dans le journal d'audit (sans requête HTTP)
     */
    public void logAction(String action, String entityType, Long entityId, String details) {
        logAction(action, entityType, entityId, details, null);
    }

    /**
     * Extrait l'adresse IP du client depuis la requête
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
