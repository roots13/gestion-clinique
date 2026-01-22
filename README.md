# Application de Gestion Clinique

Application web complète de gestion clinique développée avec **Spring Boot 3.x** et **Java 17**.

## 📋 Fonctionnalités

### 1. Gestion des patients
- Création, modification, recherche de patients
- Historique des consultations par patient
- Numéros de patients auto-générés

### 2. Système de ticketing
- Attribution automatique de tickets aux patients
- Gestion de file d'attente par service
- Statuts : EN_ATTENTE, EN_COURS, TERMINE

### 3. Gestion de caisse
- Enregistrement des paiements
- Génération automatique de reçus
- Suivi des recettes (journalier, mensuel, annuel)

### 4. Gestion des utilisateurs et droits d'accès
- **ADMIN** : Accès total
- **ACCUEIL** : Patients + Tickets
- **MEDECIN** : Consultations + Patients
- **CAISSIER** : Caisse uniquement

### 5. Authentification et autorisation
- Authentification JWT sécurisée
- Spring Security avec rôles et permissions

### 6. Journalisation (Audit)
- Suivi de toutes les actions utilisateurs
- Logs avec IP, timestamp, détails

## 🛠️ Technologies utilisées

- **Backend** : Java 17, Spring Boot 3.2.0
- **Sécurité** : Spring Security 6.x + JWT
- **ORM** : Spring Data JPA (Hibernate)
- **Base de données** : PostgreSQL 14+
- **Frontend** : Thymeleaf
- **Build** : Maven
- **Validation** : Bean Validation (Jakarta Validation)

## 📦 Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- PostgreSQL 14+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🚀 Installation et lancement

### 1. Cloner ou télécharger le projet

```bash
cd "Gestion de clinique"
```

### 2. Configurer PostgreSQL

Créer une base de données PostgreSQL :

```sql
CREATE DATABASE clinique_db;
```

### 3. Configurer l'application

Modifier le fichier `src/main/resources/application.properties` si nécessaire :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clinique_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 4. Compiler et lancer l'application

#### Avec Maven :

```bash
# Compiler
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

#### Avec un IDE :

1. Importer le projet comme projet Maven
2. Lancer la classe `GestionCliniqueApplication`

### 5. Accéder à l'application

- **URL** : http://localhost:8080
- **Page de connexion** : http://localhost:8080/login

## 👤 Comptes de test

Les comptes suivants sont créés automatiquement au démarrage :

| Rôle     | Username  | Password     |
|----------|-----------|--------------|
| ADMIN    | admin     | admin123     |
| ACCUEIL  | accueil   | accueil123   |
| MEDECIN  | medecin   | medecin123   |
| CAISSIER | caissier  | caissier123  |

## 📡 API REST

### Authentification

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Réponse :
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@clinique.local",
  "role": "ADMIN"
}
```

### Patients

```bash
# Créer un patient
POST /api/patients
Authorization: Bearer {token}
Content-Type: application/json

{
  "nom": "Dupont",
  "prenom": "Jean",
  "dateNaissance": "1990-01-15",
  "telephone": "0123456789",
  "adresse": "123 Rue Example"
}

# Rechercher des patients
GET /api/patients/search?q=Dupont
Authorization: Bearer {token}

# Récupérer un patient
GET /api/patients/{id}
Authorization: Bearer {token}
```

### Tickets

```bash
# Créer un ticket
POST /api/tickets?patientId=1&service=CONSULTATION
Authorization: Bearer {token}

# Récupérer la file d'attente
GET /api/tickets/queue/CONSULTATION
Authorization: Bearer {token}

# Mettre à jour le statut
PUT /api/tickets/{id}/statut?statut=EN_COURS
Authorization: Bearer {token}
```

### Paiements

```bash
# Enregistrer un paiement
POST /api/paiements
Authorization: Bearer {token}
Content-Type: application/json

{
  "patientId": 1,
  "montant": 50.00,
  "modePaiement": "ESPECES",
  "ticketId": 1
}

# Récupérer le total des recettes
GET /api/paiements/total?start=2024-01-01T00:00:00&end=2024-01-31T23:59:59
Authorization: Bearer {token}
```

## 📁 Structure du projet

```
src/
├── main/
│   ├── java/com/clinique/gestion/
│   │   ├── config/          # Configuration Spring
│   │   ├── controller/      # Controllers REST et Web
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Entités JPA
│   │   ├── enums/           # Énumérations
│   │   ├── exception/       # Exceptions personnalisées
│   │   ├── repository/      # Repositories Spring Data JPA
│   │   ├── security/        # Configuration sécurité
│   │   ├── service/         # Services métier
│   │   └── util/            # Utilitaires
│   └── resources/
│       ├── db/              # Scripts SQL
│       ├── templates/        # Vues Thymeleaf
│       └── application.properties
└── pom.xml
```

## 🔒 Sécurité

- **Encodage des mots de passe** : BCrypt
- **JWT** : Expiration 24h
- **CORS** : Configuré pour le frontend
- **Validation** : Bean Validation sur tous les DTOs

## 📊 Base de données

Le schéma de base de données est créé automatiquement au démarrage (mode `update`).

Voir les fichiers :
- `src/main/resources/db/schema.sql` : Schéma complet
- `src/main/resources/db/data.sql` : Données initiales

## 🧪 Tests

```bash
# Lancer les tests
mvn test
```

## 📝 Documentation

- **Architecture** : Voir `ARCHITECTURE.md`
- **Modèle de données** : Voir `MODELE_DONNEES.md`

## 🐛 Dépannage

### Erreur de connexion à la base de données

Vérifier que PostgreSQL est démarré et que les identifiants dans `application.properties` sont corrects.

### Port 8080 déjà utilisé

Modifier le port dans `application.properties` :
```properties
server.port=8081
```

### Erreur JWT

Vérifier que la clé secrète JWT dans `application.properties` fait au moins 256 bits.

## 📄 Licence

Ce projet est fourni à des fins éducatives et de démonstration.

## 👨‍💻 Auteur

Application développée avec Spring Boot pour la gestion clinique.

---

**Note** : Pour la production, pensez à :
- Changer les mots de passe par défaut
- Configurer HTTPS
- Utiliser une base de données de production
- Configurer les logs appropriés
- Mettre en place des sauvegardes
