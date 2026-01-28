package com.clinique.gestion.repository;

import com.clinique.gestion.entity.PasswordResetToken;
import com.clinique.gestion.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository pour les tokens de réinitialisation de mot de passe
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Trouve un token de réinitialisation par son identifiant
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Trouve les tokens non utilisés d'un utilisateur
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.user = :user AND t.used = false ORDER BY t.createdAt DESC")
    Optional<PasswordResetToken> findLatestUnusedToken(@Param("user") User user);

    /**
     * Supprime les tokens expirés
     */
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
