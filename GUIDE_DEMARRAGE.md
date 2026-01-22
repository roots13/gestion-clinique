# Guide de Démarrage Rapide

## 🚀 Démarrage en 5 minutes

### Étape 1 : Installer PostgreSQL

1. Télécharger et installer PostgreSQL depuis https://www.postgresql.org/download/
2. Noter le mot de passe du superutilisateur `postgres` (ou créer un utilisateur dédié)

### Étape 2 : Créer la base de données

Ouvrir PostgreSQL (pgAdmin ou psql) et exécuter :

```sql
CREATE DATABASE clinique_db;
```

### Étape 3 : Configurer l'application

Modifier `src/main/resources/application.properties` si nécessaire :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clinique_db
spring.datasource.username=postgres
spring.datasource.password=VOTRE_MOT_DE_PASSE
```

### Étape 4 : Lancer l'application

#### Option A : Avec Maven (ligne de commande)

```bash
# Dans le répertoire du projet
mvn clean install
mvn spring-boot:run
```

#### Option B : Avec un IDE

1. **IntelliJ IDEA** :
   - File → Open → Sélectionner le dossier du projet
   - Maven → Reload Project
   - Run → Run 'GestionCliniqueApplication'

2. **Eclipse** :
   - File → Import → Maven → Existing Maven Projects
   - Sélectionner le dossier du projet
   - Run As → Spring Boot App

3. **VS Code** :
   - Ouvrir le dossier du projet
   - Installer l'extension "Extension Pack for Java"
   - Run → Run Without Debugging

### Étape 5 : Accéder à l'application

1. Ouvrir un navigateur
2. Aller sur : http://localhost:8080
3. Se connecter avec :
   - **Username** : `admin`
   - **Password** : `admin123`

## ✅ Vérification

Si tout fonctionne correctement, vous devriez voir :
- La page de connexion s'affiche
- Vous pouvez vous connecter avec les identifiants admin
- Le dashboard s'affiche après connexion

## 🔧 Problèmes courants

### Erreur : "Connection refused" ou "Connection to localhost:5432 refused"

**Solution** : Vérifier que PostgreSQL est démarré
- Windows : Services → PostgreSQL
- Linux/Mac : `sudo systemctl start postgresql` ou `brew services start postgresql`

### Erreur : "Database 'clinique_db' does not exist"

**Solution** : Créer la base de données (voir Étape 2)

### Erreur : "Port 8080 already in use"

**Solution** : Changer le port dans `application.properties` :
```properties
server.port=8081
```

### Erreur : "JWT secret key too short"

**Solution** : La clé secrète JWT dans `application.properties` doit faire au moins 256 bits (32 caractères). La clé par défaut est déjà configurée correctement.

## 📝 Prochaines étapes

1. **Explorer l'API REST** : Utiliser Postman ou curl pour tester les endpoints
2. **Créer des patients** : Via l'API ou l'interface web
3. **Gérer les tickets** : Créer des tickets pour les patients
4. **Enregistrer des paiements** : Tester le module caisse

## 🔐 Sécurité en production

⚠️ **Important** : Avant de déployer en production :

1. Changer tous les mots de passe par défaut
2. Générer une nouvelle clé secrète JWT (au moins 256 bits)
3. Configurer HTTPS
4. Désactiver le mode `update` de Hibernate (utiliser `validate`)
5. Configurer des sauvegardes de base de données
6. Activer les logs de production

## 📚 Documentation complète

- **Architecture** : Voir `ARCHITECTURE.md`
- **Modèle de données** : Voir `MODELE_DONNEES.md`
- **README** : Voir `README.md`

## 💡 Astuces

- Les données de test sont créées automatiquement au premier démarrage
- Le schéma de base de données est créé automatiquement (mode `update`)
- Les logs sont visibles dans la console
- Pour réinitialiser la base : Supprimer et recréer la base de données
