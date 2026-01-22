package com.clinique.gestion.repository;

import com.clinique.gestion.entity.Ticket;
import com.clinique.gestion.enums.StatutTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Ticket
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Trouve un ticket par son numéro
     */
    Optional<Ticket> findByNumero(String numero);

    /**
     * Vérifie si un numéro de ticket existe
     */
    boolean existsByNumero(String numero);

    /**
     * Trouve tous les tickets d'un patient
     */
    List<Ticket> findByPatientId(Long patientId);

    /**
     * Trouve tous les tickets par service
     */
    List<Ticket> findByService(String service);

    /**
     * Trouve tous les tickets par statut
     */
    List<Ticket> findByStatut(StatutTicket statut);

    /**
     * Trouve tous les tickets d'un service avec un statut donné
     */
    List<Ticket> findByServiceAndStatut(String service, StatutTicket statut);

    /**
     * Trouve la file d'attente d'un service (tickets EN_ATTENTE triés par date)
     */
    @Query("SELECT t FROM Ticket t WHERE t.service = :service AND t.statut = :statut " +
           "ORDER BY t.createdAt ASC")
    List<Ticket> findQueueByService(@Param("service") String service, 
                                    @Param("statut") StatutTicket statut);

    /**
     * Compte les tickets en attente pour un service
     */
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.service = :service AND t.statut = 'EN_ATTENTE'")
    long countEnAttenteByService(@Param("service") String service);

    /**
     * Trouve les tickets créés dans une période
     */
    @Query("SELECT t FROM Ticket t WHERE t.createdAt BETWEEN :start AND :end")
    List<Ticket> findTicketsBetweenDates(@Param("start") LocalDateTime start, 
                                          @Param("end") LocalDateTime end);
}
