package com.clinique.gestion.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utilitaire pour générer des numéros uniques (patients, tickets, reçus)
 */
@Component
public class NumeroGenerator {

    private static final AtomicLong patientCounter = new AtomicLong(1);
    private static final AtomicLong ticketCounter = new AtomicLong(1);
    private static final AtomicLong recuCounter = new AtomicLong(1);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Génère un numéro unique pour un patient
     * Format: PAT-YYYYMMDD-XXXXX
     */
    public String generatePatientNumero() {
        String date = LocalDate.now().format(DATE_FORMATTER);
        long counter = patientCounter.getAndIncrement();
        return String.format("PAT-%s-%05d", date, counter);
    }

    /**
     * Génère un numéro unique pour un ticket
     * Format: TKT-SERVICE-YYYYMMDD-XXXXX
     */
    public String generateTicketNumero(String service) {
        String date = LocalDate.now().format(DATE_FORMATTER);
        long counter = ticketCounter.getAndIncrement();
        String serviceCode = service.toUpperCase().substring(0, Math.min(service.length(), 3));
        return String.format("TKT-%s-%s-%05d", serviceCode, date, counter);
    }

    /**
     * Génère un numéro unique pour un reçu
     * Format: REC-YYYYMMDD-XXXXX
     */
    public String generateRecuNumero() {
        String date = LocalDate.now().format(DATE_FORMATTER);
        long counter = recuCounter.getAndIncrement();
        return String.format("REC-%s-%05d", date, counter);
    }
}
