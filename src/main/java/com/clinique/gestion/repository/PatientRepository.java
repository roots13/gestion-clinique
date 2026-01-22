package com.clinique.gestion.repository;

import com.clinique.gestion.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Patient
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Trouve un patient par son numéro
     */
    Optional<Patient> findByNumero(String numero);

    /**
     * Vérifie si un numéro patient existe
     */
    boolean existsByNumero(String numero);

    /**
     * Recherche de patients par nom ou prénom (insensible à la casse)
     */
    @Query("SELECT p FROM Patient p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.prenom) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR p.numero LIKE CONCAT('%', :search, '%')")
    List<Patient> searchPatients(@Param("search") String search);

    /**
     * Recherche de patients par nom
     */
    List<Patient> findByNomContainingIgnoreCase(String nom);

    /**
     * Recherche de patients par prénom
     */
    List<Patient> findByPrenomContainingIgnoreCase(String prenom);

    /**
     * Recherche de patients par téléphone
     */
    List<Patient> findByTelephoneContaining(String telephone);
}
