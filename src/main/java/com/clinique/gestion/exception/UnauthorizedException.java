package com.clinique.gestion.exception;

/**
 * Exception levée pour une authentification non autorisée
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
