package com.clinique.gestion.service;

import com.clinique.gestion.dto.JwtResponse;
import com.clinique.gestion.dto.LoginRequest;
import com.clinique.gestion.entity.User;
import com.clinique.gestion.exception.UnauthorizedException;
import com.clinique.gestion.repository.UserRepository;
import com.clinique.gestion.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service pour l'authentification
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditService auditService;

    /**
     * Authentifie un utilisateur et retourne un token JWT
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

            // Audit
            auditService.logAction("LOGIN", "User", user.getId(),
                    "Connexion de l'utilisateur: " + user.getUsername(), request);

            return new JwtResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), user.getRole());
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Nom d'utilisateur ou mot de passe incorrect");
        }
    }
}
