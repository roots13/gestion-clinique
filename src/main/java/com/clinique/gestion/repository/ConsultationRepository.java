package com.clinique.gestion.repository;

import com.clinique.gestion.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'entité Consultation
 */
@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    /**
     * Trouve toutes les consultations d'un patient
     */
    List<Consultation> findByPatientId(Long patientId);

    /**
     * Trouve toutes les consultations d'un médecin
     */
    List<Consultation> findByMedecinId(Long medecinId);

    /**
     * Trouve toutes les consultations d'un patient triées par date (plus récentes en premier)
     */
    @Query("SELECT c FROM Consultation c WHERE c.patient.id = :patientId " +
           "ORDER BY c.date DESC")
    List<Consultation> findConsultationsByPatientOrderByDateDesc(@Param("patientId") Long patientId);

    /**
     * Trouve toutes les consultations d'un médecin dans une période
     */
    @Query("SELECT c FROM Consultation c WHERE c.medecin.id = :medecinId " +
           "AND c.date BETWEEN :start AND :end ORDER BY c.date DESC")
    List<Consultation> findConsultationsByMedecinBetweenDates(
            @Param("medecinId") Long medecinId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Trouve toutes les consultations dans une période
     */
    @Query("SELECT c FROM Consultation c WHERE c.date BETWEEN :start AND :end " +
           "ORDER BY c.date DESC")
    List<Consultation> findConsultationsBetweenDates(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);
}
