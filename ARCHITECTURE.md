# Architecture de l'Application de Gestion Clinique

## 1. Vue d'ensemble

Application web de gestion clinique développée avec **Spring Boot 3.x** et **Java 17**, utilisant une architecture en couches (Clean Architecture / MVC).

### Stack technique
- **Backend**: Java 17, Spring Boot 3.x
- **Sécurité**: Spring Security 6.x + JWT (JSON Web Tokens)
- **ORM**: Spring Data JPA (Hibernate)
- **Base de données**: PostgreSQL 14+
- **Frontend**: Thymeleaf (templates serveur)
- **Validation**: Bean Validation (Jakarta Validation)
- **Build**: Maven

## 2. Architecture en couches

```
┌─────────────────────────────────────────┐
│         Presentation Layer               │
│  (Controllers REST + Thymeleaf Views)    │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         Service Layer                    │
│  (Business Logic + Transactions)         │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         Repository Layer                 │
│  (Spring Data JPA Repositories)          │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         Database Layer                   │
│  (PostgreSQL)                            │
└─────────────────────────────────────────┘
```

### Structure des packages

```
com.clinique.gestion
├── config/              # Configuration Spring (Security, JPA, etc.)
├── controller/          # Controllers REST et Web
├── dto/                 # Data Transfer Objects
├── entity/              # Entités JPA
├── enums/               # Énumérations
├── exception/           # Exceptions personnalisées
├── mapper/              # Mappers DTO <-> Entity
├── repository/          # Repositories Spring Data JPA
├── security/            # Configuration et services de sécurité
│   ├── jwt/             # JWT (filters, providers, utils)
│   └── service/         # UserDetailsService
├── service/             # Services métier
└── util/                # Utilitaires
```

## 3. Modèle de données

### Diagramme des entités

```
┌──────────────┐
│    User      │
├──────────────┤
│ id (PK)      │
│ username     │
│ email        │
│ password     │
│ role         │
│ enabled      │
│ created_at   │
└──────┬───────┘
       │
       │ 1:N
       │
┌──────▼──────────┐
│   AuditLog      │
├─────────────────┤
│ id (PK)         │
│ user_id (FK)    │
│ action          │
│ entity_type     │
│ entity_id       │
│ details         │
│ timestamp       │
└─────────────────┘

┌──────────────┐
│   Patient    │
├──────────────┤
│ id (PK)      │
│ numero       │ (unique)
│ nom          │
│ prenom       │
│ date_naissance│
│ telephone    │
│ adresse      │
│ created_at   │
└──────┬───────┘
       │
       │ 1:N
       │
┌──────▼──────────┐     ┌──────────────┐
│  Consultation   │     │    Ticket    │
├─────────────────┤     ├──────────────┤
│ id (PK)         │     │ id (PK)      │
│ patient_id (FK) │     │ numero       │
│ medecin_id (FK) │     │ patient_id   │
│ date            │     │ service      │
│ motif           │     │ statut       │
│ diagnostic      │     │ created_at   │
│ prescription    │     │ updated_at   │
│ created_at      │     └──────────────┘
└─────────────────┘

┌──────────────┐
│   Paiement   │
├──────────────┤
│ id (PK)      │
│ patient_id   │
│ montant      │
│ mode_paiement│
│ date         │
│ caissier_id  │
│ ticket_id    │ (nullable)
│ numero_recu  │
│ created_at   │
└──────────────┘
```

### Relations

- **User** → **AuditLog** (1:N) : Un utilisateur peut avoir plusieurs logs d'audit
- **User** → **Consultation** (1:N) : Un médecin peut avoir plusieurs consultations
- **User** → **Paiement** (1:N) : Un caissier peut enregistrer plusieurs paiements
- **Patient** → **Consultation** (1:N) : Un patient peut avoir plusieurs consultations
- **Patient** → **Ticket** (1:N) : Un patient peut avoir plusieurs tickets
- **Patient** → **Paiement** (1:N) : Un patient peut avoir plusieurs paiements
- **Ticket** → **Paiement** (1:1) : Un ticket peut être associé à un paiement (optionnel)

## 4. Rôles et permissions

| Rôle        | Permissions                                                      |
|-------------|------------------------------------------------------------------|
| ADMIN       | Accès total (CRUD sur tout)                                     |
| ACCUEIL     | Créer/modifier patients, gérer tickets, consulter consultations |
| MEDECIN     | Consulter/modifier patients, créer consultations, voir tickets  |
| CAISSIER    | Enregistrer paiements, générer reçus, consulter tickets         |

## 5. Flux d'authentification JWT

```
1. Client → POST /api/auth/login (username, password)
2. Server → Vérifie credentials → Génère JWT
3. Server → Retourne JWT dans header Authorization
4. Client → Inclut JWT dans toutes les requêtes suivantes
5. Server → JwtAuthenticationFilter valide le token
6. Server → Spring Security autorise/refuse selon rôle
```

## 6. Système de ticketing

- **Attribution automatique** : Numéro séquentiel par service
- **File d'attente** : Tickets triés par date de création (plus ancien en premier)
- **Statuts** :
  - `EN_ATTENTE` : Ticket créé, en attente de traitement
  - `EN_COURS` : Ticket en cours de traitement
  - `TERMINE` : Ticket traité et fermé

## 7. Gestion de caisse

- **Enregistrement** : Paiement lié à un patient (et optionnellement un ticket)
- **Reçus** : Numéro unique généré automatiquement (format: REC-YYYYMMDD-XXXXX)
- **Rapports** : Filtrage par date (journalier, mensuel, annuel)

## 8. Journalisation (Audit)

Toutes les actions critiques sont journalisées :
- Création/modification/suppression de patients
- Création/modification de tickets
- Enregistrement de paiements
- Connexions/déconnexions
- Modifications de rôles utilisateurs

## 9. Sécurité

- **Password encoding** : BCrypt
- **JWT** : Expiration 24h, refresh token optionnel
- **CORS** : Configuré pour le frontend
- **CSRF** : Désactivé pour API REST (activé pour Thymeleaf)
- **Validation** : Bean Validation sur tous les DTOs

## 10. Gestion des exceptions

- **GlobalExceptionHandler** : Capture toutes les exceptions
- **Exceptions personnalisées** :
  - `ResourceNotFoundException`
  - `BadRequestException`
  - `UnauthorizedException`
  - `ForbiddenException`
- **Format de réponse standardisé** : `{ "error": "...", "message": "...", "timestamp": "..." }`
