package com.clinique.gestion.repository;

import com.clinique.gestion.entity.User;
import com.clinique.gestion.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son nom d'utilisateur
     */
    Optional<User> findByUsername(String username);

    /**
     * Trouve un utilisateur par son email
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un nom d'utilisateur existe
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie si un email existe
     */
    boolean existsByEmail(String email);

    /**
     * Trouve tous les utilisateurs par rôle
     */
    List<User> findByRole(Role role);

    /**
     * Trouve tous les utilisateurs actifs
     */
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findAllEnabled();

    /**
     * Trouve tous les médecins actifs
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.enabled = true")
    List<User> findActiveByRole(@Param("role") Role role);
}
