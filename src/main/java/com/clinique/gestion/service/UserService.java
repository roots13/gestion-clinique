package com.clinique.gestion.service;

import com.clinique.gestion.dto.UserDTO;
import com.clinique.gestion.entity.User;
import com.clinique.gestion.enums.Role;
import com.clinique.gestion.exception.BadRequestException;
import com.clinique.gestion.exception.ResourceNotFoundException;
import com.clinique.gestion.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des utilisateurs
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditService auditService;

    /**
     * Crée un nouvel utilisateur
     */
    public UserDTO createUser(UserDTO userDTO, String password, HttpServletRequest request) {
        // Vérifier que l'utilisateur n'existe pas déjà
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BadRequestException("Un utilisateur avec ce nom d'utilisateur existe déjà");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException("Un utilisateur avec cet email existe déjà");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setNom(userDTO.getNom());
        user.setPrenom(userDTO.getPrenom());
        user.setRole(userDTO.getRole());
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        user = userRepository.save(user);

        // Audit
        auditService.logAction("CREATE", "User", user.getId(),
                "Création de l'utilisateur: " + user.getUsername(), request);

        return entityToDTO(user);
    }

    /**
     * Met à jour un utilisateur
     */
    public UserDTO updateUser(Long id, UserDTO userDTO, HttpServletRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Vérifier l'unicité de l'email si modifié
        if (!user.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException("Un utilisateur avec cet email existe déjà");
        }

        user.setEmail(userDTO.getEmail());
        user.setNom(userDTO.getNom());
        user.setPrenom(userDTO.getPrenom());
        user.setRole(userDTO.getRole());
        // Ne pas modifier 'enabled' s'il vient du formulaire vide
        // Car le formulaire de modification n'envoie pas ce champ
        // user.setEnabled(userDTO.isEnabled());

        user = userRepository.save(user);

        // Audit
        auditService.logAction("UPDATE", "User", user.getId(),
                "Modification de l'utilisateur: " + user.getUsername(), request);

        return entityToDTO(user);
    }

    /**
     * Change le mot de passe d'un utilisateur
     */
    public void changePassword(Long id, String newPassword, HttpServletRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Audit
        auditService.logAction("UPDATE", "User", user.getId(),
                "Changement du mot de passe de l'utilisateur: " + user.getUsername(), request);
    }

    /**
     * Change le mot de passe de l'utilisateur actuellement connecté
     */
    public void changeOwnPassword(String username, String oldPassword, String newPassword, HttpServletRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Vérifier que l'ancien mot de passe est correct
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("L'ancien mot de passe est incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Audit
        auditService.logAction("UPDATE", "User", user.getId(),
                "L'utilisateur a changé son propre mot de passe", request);
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return entityToDTO(user);
    }

    /**
     * Récupère un utilisateur par son nom d'utilisateur
     */
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return entityToDTO(user);
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les utilisateurs par rôle
     */
    public List<UserDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les utilisateurs actifs
     */
    public List<UserDTO> getActiveUsers() {
        return userRepository.findAllEnabled()
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Désactive un utilisateur
     */
    public void deactivateUser(Long id, HttpServletRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setEnabled(false);
        userRepository.save(user);

        // Audit
        auditService.logAction("UPDATE", "User", user.getId(),
                "Désactivation de l'utilisateur: " + user.getUsername(), request);
    }

    /**
     * Réactive un utilisateur
     */
    public void enableUser(Long id, HttpServletRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setEnabled(true);
        userRepository.save(user);

        // Audit
        auditService.logAction("UPDATE", "User", user.getId(),
                "Réactivation de l'utilisateur: " + user.getUsername(), request);
    }

    /**
     * Supprime un utilisateur
     */
    public void deleteUser(Long id, HttpServletRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRepository.deleteById(id);

        // Audit
        auditService.logAction("DELETE", "User", id,
                "Suppression de l'utilisateur: " + user.getUsername(), request);
    }

    /**
     * Convertit une entité User en DTO
     */
    private UserDTO entityToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNom(),
                user.getPrenom(),
                user.getRole(),
                user.getEnabled()
        );
    }
}
