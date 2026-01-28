# Guide des APIs - Gestion Clinique

## Authentification
- **POST** `/api/auth/login` - Se connecter
  - Body: `{"username": "user", "password": "password"}`
  - Response: JWT token

## Gestion des Utilisateurs (ADMIN uniquement)

### Créer un utilisateur
- **POST** `/api/users`
  - Body:
    ```json
    {
      "username": "medecin1",
      "email": "medecin1@clinic.com",
      "nom": "Dupont",
      "prenom": "Jean",
      "role": "MEDECIN",
      "password": "SecurePassword123"
    }
    ```
  - Response: UserDTO créé

### Lister tous les utilisateurs
- **GET** `/api/users`

### Lister les utilisateurs par rôle
- **GET** `/api/users/role/MEDECIN`
- **GET** `/api/users/role/ACCUEIL`
- **GET** `/api/users/role/CAISSIER`
- **GET** `/api/users/role/ADMIN`

### Lister les utilisateurs actifs
- **GET** `/api/users/active/list`

### Récupérer un utilisateur
- **GET** `/api/users/{id}`
- **GET** `/api/users/username/{username}`

### Modifier un utilisateur
- **PUT** `/api/users/{id}`
  - Body:
    ```json
    {
      "email": "medecin1@newclinic.com",
      "nom": "Dupont",
      "prenom": "Jean",
      "role": "MEDECIN",
      "enabled": true
    }
    ```

### Changer le mot de passe
- **POST** `/api/users/{id}/change-password`
  - Body: `{"newPassword": "NewPassword123"}`

### Désactiver un utilisateur
- **POST** `/api/users/{id}/deactivate`

### Supprimer un utilisateur
- **DELETE** `/api/users/{id}`

## Gestion des Patients

### Créer un patient
- **POST** `/api/patients`
  - Body:
    ```json
    {
      "numero": "P001",
      "nom": "Martin",
      "prenom": "Pierre",
      "dateNaissance": "1990-05-15",
      "telephone": "0123456789",
      "adresse": "123 Rue de la Paix, Ville"
    }
    ```

### Lister tous les patients
- **GET** `/api/patients`

### Rechercher des patients
- **GET** `/api/patients/search?q=martin`

### Récupérer un patient
- **GET** `/api/patients/{id}`
- **GET** `/api/patients/numero/P001`

### Modifier un patient
- **PUT** `/api/patients/{id}`

## Gestion des Consultations

### Créer une consultation
- **POST** `/api/consultations?patientId=1&medecinId=2`
  - Body:
    ```json
    {
      "diagnostic": "Grippe saisonnière",
      "prescription": "Paracétamol 500mg x 2 par jour"
    }
    ```

### Lister les consultations d'un patient
- **GET** `/api/consultations/patient/{patientId}`

### Lister les consultations d'un médecin
- **GET** `/api/consultations/medecin/{medecinId}`

### Lister toutes les consultations
- **GET** `/api/consultations`

### Récupérer une consultation
- **GET** `/api/consultations/{id}`

### Modifier une consultation
- **PUT** `/api/consultations/{id}`
  - Body:
    ```json
    {
      "diagnostic": "Grippe confirmée",
      "prescription": "Tamiflu + repos"
    }
    ```

### Supprimer une consultation
- **DELETE** `/api/consultations/{id}`

## Gestion des Tickets (File d'attente)

### Créer un ticket
- **POST** `/api/tickets?patientId=1&service=consultation`

### Lister la file d'attente d'un service
- **GET** `/api/tickets/queue/consultation`

### Récupérer le prochain ticket
- **GET** `/api/tickets/next/consultation`

### Mettre à jour le statut d'un ticket
- **PUT** `/api/tickets/{id}/statut?statut=EN_COURS`
  - Valeurs possibles: EN_ATTENTE, EN_COURS, TERMINE

## Gestion des Paiements

### Enregistrer un paiement
- **POST** `/api/paiements`
  - Body:
    ```json
    {
      "patientId": 1,
      "ticketId": 5,
      "montant": 50.00,
      "modePaiement": "ESPECES"
    }
    ```

### Lister les paiements d'un patient
- **GET** `/api/paiements/patient/{patientId}`

### Récupérer le total des recettes
- **GET** `/api/paiements/total?start=2026-01-01T00:00:00&end=2026-01-31T23:59:59`

### Lister les paiements par période
- **GET** `/api/paiements/periode?start=2026-01-01T00:00:00&end=2026-01-31T23:59:59`

## Notes d'accès

- Les endpoints nécessitent un token JWT obtenu via `/api/auth/login`
- Le token doit être passé dans l'en-tête `Authorization: Bearer {token}`
- Les rôles requis:
  - **ADMIN**: Gestion complète des utilisateurs, accès à tous les endpoints
  - **ACCUEIL**: Gestion des patients et des tickets
  - **MEDECIN**: Consultations et accès aux dossiers patients
  - **CAISSIER**: Gestion des paiements
