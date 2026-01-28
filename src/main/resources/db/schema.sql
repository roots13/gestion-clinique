-- Script de création du schéma PostgreSQL pour la gestion clinique

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'ACCUEIL', 'MEDECIN', 'CAISSIER')),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Table des patients
CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(20) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE NOT NULL,
    telephone VARCHAR(20),
    adresse TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_patients_numero ON patients(numero);
CREATE INDEX IF NOT EXISTS idx_patients_nom ON patients(nom);
CREATE INDEX IF NOT EXISTS idx_patients_prenom ON patients(prenom);

-- Table des tickets
CREATE TABLE IF NOT EXISTS tickets (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(20) UNIQUE NOT NULL,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    service VARCHAR(50) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE' 
        CHECK (statut IN ('EN_ATTENTE', 'EN_COURS', 'TERMINE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tickets_numero ON tickets(numero);
CREATE INDEX IF NOT EXISTS idx_tickets_patient ON tickets(patient_id);
CREATE INDEX IF NOT EXISTS idx_tickets_service ON tickets(service);
CREATE INDEX IF NOT EXISTS idx_tickets_statut ON tickets(statut);
CREATE INDEX IF NOT EXISTS idx_tickets_created ON tickets(created_at);

-- Table des consultations
CREATE TABLE IF NOT EXISTS consultations (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    medecin_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    date TIMESTAMP NOT NULL,
    motif TEXT,
    diagnostic TEXT,
    prescription TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_consultations_patient ON consultations(patient_id);
CREATE INDEX IF NOT EXISTS idx_consultations_medecin ON consultations(medecin_id);
CREATE INDEX IF NOT EXISTS idx_consultations_date ON consultations(date);

-- Table des paiements
CREATE TABLE IF NOT EXISTS paiements (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    ticket_id BIGINT REFERENCES tickets(id) ON DELETE SET NULL,
    montant DECIMAL(10, 2) NOT NULL CHECK (montant > 0),
    mode_paiement VARCHAR(20) NOT NULL 
        CHECK (mode_paiement IN ('ESPECES', 'CARTE', 'CHEQUE', 'VIREMENT')),
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    caissier_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    numero_recu VARCHAR(30) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_paiements_patient ON paiements(patient_id);
CREATE INDEX IF NOT EXISTS idx_paiements_ticket ON paiements(ticket_id);
CREATE INDEX IF NOT EXISTS idx_paiements_caissier ON paiements(caissier_id);
CREATE INDEX IF NOT EXISTS idx_paiements_date ON paiements(date);
CREATE INDEX IF NOT EXISTS idx_paiements_numero_recu ON paiements(numero_recu);

-- Table des logs d'audit
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    details TEXT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);

-- Table des tokens de réinitialisation de mot de passe
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_reset_token ON password_reset_tokens(token);
CREATE INDEX IF NOT EXISTS idx_reset_user ON password_reset_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_reset_expiry ON password_reset_tokens(expiry_date);

-- Séquenceurs pour numéros uniques
CREATE SEQUENCE IF NOT EXISTS seq_patient_numero START 1;
CREATE SEQUENCE IF NOT EXISTS seq_ticket_numero START 1;
CREATE SEQUENCE IF NOT EXISTS seq_recu_numero START 1;
