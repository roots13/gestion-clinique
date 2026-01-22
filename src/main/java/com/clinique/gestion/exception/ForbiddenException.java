package com.clinique.gestion.exception;

/**
 * Exception levée pour un accès interdit (permissions insuffisantes)
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
