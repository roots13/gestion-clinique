# Modèle de données - Schéma PostgreSQL

## Tables principales

### 1. users (Utilisateurs)

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'ACCUEIL', 'MEDECIN', 'CAISSIER')),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
```

### 2. patients (Patients)

```sql
CREATE TABLE patients (
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

CREATE INDEX idx_patients_numero ON patients(numero);
CREATE INDEX idx_patients_nom ON patients(nom);
CREATE INDEX idx_patients_prenom ON patients(prenom);
```

### 3. tickets (Tickets)

```sql
CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(20) UNIQUE NOT NULL,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    service VARCHAR(50) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE' 
        CHECK (statut IN ('EN_ATTENTE', 'EN_COURS', 'TERMINE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tickets_numero ON tickets(numero);
CREATE INDEX idx_tickets_patient ON tickets(patient_id);
CREATE INDEX idx_tickets_service ON tickets(service);
CREATE INDEX idx_tickets_statut ON tickets(statut);
CREATE INDEX idx_tickets_created ON tickets(created_at);
```

### 4. consultations (Consultations)

```sql
CREATE TABLE consultations (
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

CREATE INDEX idx_consultations_patient ON consultations(patient_id);
CREATE INDEX idx_consultations_medecin ON consultations(medecin_id);
CREATE INDEX idx_consultations_date ON consultations(date);
```

### 5. paiements (Paiements)

```sql
CREATE TABLE paiements (
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

CREATE INDEX idx_paiements_patient ON paiements(patient_id);
CREATE INDEX idx_paiements_ticket ON paiements(ticket_id);
CREATE INDEX idx_paiements_caissier ON paiements(caissier_id);
CREATE INDEX idx_paiements_date ON paiements(date);
CREATE INDEX idx_paiements_numero_recu ON paiements(numero_recu);
```

### 6. audit_logs (Journal d'audit)

```sql
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    details TEXT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
```

## Contraintes et règles métier

1. **Numéro patient** : Format auto-généré (PAT-YYYYMMDD-XXXXX)
2. **Numéro ticket** : Format auto-généré par service (TKT-SERVICE-YYYYMMDD-XXXXX)
3. **Numéro reçu** : Format auto-généré (REC-YYYYMMDD-XXXXX)
4. **Cascade** : Suppression d'un patient supprime ses consultations, tickets et paiements
5. **Restriction** : Impossible de supprimer un utilisateur s'il a des consultations ou paiements

## Séquenceurs pour numéros uniques

```sql
-- Séquence pour numéros patients
CREATE SEQUENCE seq_patient_numero START 1;

-- Séquence pour numéros tickets (par service)
CREATE SEQUENCE seq_ticket_numero START 1;

-- Séquence pour numéros reçus
CREATE SEQUENCE seq_recu_numero START 1;
```

## Données initiales (Seed)

```sql
-- Utilisateur administrateur par défaut
-- Password: admin123 (hashé avec BCrypt)
INSERT INTO users (username, email, password, role, enabled) 
VALUES ('admin', 'admin@clinique.local', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ/2a', 'ADMIN', TRUE);
```
