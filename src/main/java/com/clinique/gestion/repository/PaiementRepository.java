package com.clinique.gestion.repository;

import com.clinique.gestion.entity.Paiement;
import com.clinique.gestion.enums.ModePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Paiement
 */
@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    /**
     * Trouve un paiement par son numéro de reçu
     */
    Optional<Paiement> findByNumeroRecu(String numeroRecu);

    /**
     * Vérifie si un numéro de reçu existe
     */
    boolean existsByNumeroRecu(String numeroRecu);

    /**
     * Trouve tous les paiements d'un patient
     */
    List<Paiement> findByPatientId(Long patientId);

    /**
     * Trouve tous les paiements d'un caissier
     */
    List<Paiement> findByCaissierId(Long caissierId);

    /**
     * Trouve tous les paiements d'un ticket
     */
    List<Paiement> findByTicketId(Long ticketId);

    /**
     * Trouve tous les paiements dans une période
     */
    @Query("SELECT p FROM Paiement p WHERE p.date BETWEEN :start AND :end " +
           "ORDER BY p.date DESC")
    List<Paiement> findPaiementsBetweenDates(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    /**
     * Calcule le total des paiements dans une période
     */
    @Query("SELECT SUM(p.montant) FROM Paiement p WHERE p.date BETWEEN :start AND :end")
    BigDecimal calculateTotalBetweenDates(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    /**
     * Calcule le total des paiements par mode de paiement dans une période
     */
    @Query("SELECT p.modePaiement, SUM(p.montant) FROM Paiement p " +
           "WHERE p.date BETWEEN :start AND :end GROUP BY p.modePaiement")
    List<Object[]> calculateTotalByModePaiement(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);

    /**
     * Trouve tous les paiements par mode de paiement dans une période
     */
    List<Paiement> findByModePaiementAndDateBetween(ModePaiement modePaiement,
                                                    LocalDateTime start,
                                                    LocalDateTime end);
}
