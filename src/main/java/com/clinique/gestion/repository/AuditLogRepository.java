package com.clinique.gestion.repository;

import com.clinique.gestion.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'entité AuditLog
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Trouve tous les logs d'un utilisateur
     */
    List<AuditLog> findByUserId(Long userId);

    /**
     * Trouve tous les logs d'une entité
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.entityId = :entityId " +
           "ORDER BY a.timestamp DESC")
    List<AuditLog> findByEntity(@Param("entityType") String entityType,
                                @Param("entityId") Long entityId);

    /**
     * Trouve tous les logs d'une action
     */
    List<AuditLog> findByAction(String action);

    /**
     * Trouve tous les logs dans une période
     */
    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :start AND :end " +
           "ORDER BY a.timestamp DESC")
    List<AuditLog> findLogsBetweenDates(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    /**
     * Trouve tous les logs d'un utilisateur dans une période
     */
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = :userId " +
           "AND a.timestamp BETWEEN :start AND :end ORDER BY a.timestamp DESC")
    List<AuditLog> findLogsByUserBetweenDates(@Param("userId") Long userId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);
}
