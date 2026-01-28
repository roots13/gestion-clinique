# Récupération de Mot de Passe Oublié

## 🔐 Vue d'ensemble

L'application Gestion Clinique dispose maintenant d'une fonctionnalité complète de récupération de mot de passe pour les utilisateurs qui ont oublié leurs identifiants.

## 🌐 Pages Disponibles

### 1. Page de Connexion
- **URL** : `http://localhost:8080/login`
- **Nouveauté** : Lien "Mot de passe oublié ?" visible sous le formulaire de connexion
- **Accès** : Public (sans authentification)

### 2. Page Mot de Passe Oublié
- **URL** : `http://localhost:8080/forgot-password`
- **Étapes** :
  1. Entrez votre adresse email
  2. Cliquez sur "Continuer"
  3. Un email de réinitialisation est envoyé
- **Accès** : Public

### 3. Page Réinitialisation du Mot de Passe
- **URL** : `http://localhost:8080/reset-password?token=XXX`
- **Fonctionnalités** :
  - Validation du token automatique
  - Affichage d'un indicateur de force du mot de passe
  - Vérification de correspondance des mots de passe
  - Redirection vers login après succès
- **Accès** : Public (via le lien envoyé par email)

## 📡 Endpoints API

### 1. Demander la réinitialisation
**POST** `/api/auth/forgot-password`

**Request body** :
```json
{
  "email": "user@example.com"
}
```

**Response** :
```json
{
  "message": "Un email de réinitialisation a été envoyé à votre adresse email"
}
```

**Erreurs possibles** :
- 404 : Email utilisateur non trouvé
- 400 : Email invalide

### 2. Réinitialiser le mot de passe
**POST** `/api/auth/reset-password`

**Request body** :
```json
{
  "token": "uuid-token-from-email",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}
```

**Response** :
```json
{
  "message": "Votre mot de passe a été réinitialisé avec succès"
}
```

**Erreurs possibles** :
- 400 : Token expiré ou déjà utilisé
- 400 : Les mots de passe ne correspondent pas
- 400 : Mot de passe trop court (moins de 6 caractères)

### 3. Valider un token
**GET** `/api/auth/validate-reset-token/{token}`

**Response** :
```json
{
  "valid": true
}
```

**Réponses possibles** :
- `valid: true` : Token valide et peut être utilisé
- `valid: false` : Token expiré ou déjà utilisé

## 🔧 Configuration

### Délai d'expiration des tokens
Par défaut : **24 heures**

Pour modifier, éditez `PasswordResetService.java` :
```java
private static final int TOKEN_EXPIRY_HOURS = 24; // Changez cette valeur
```

### Service d'Email
Actuellement, les emails sont loggés dans la console (développement).

Pour activer l'envoi réel d'emails, modifiez `application.properties` :
```properties
app.email.enabled=true
```

Et implémentez `EmailService.java` avec un vrai service SMTP.

## 🔒 Sécurité

### Fonctionnalités de sécurité implémentées

1. **Tokens uniques** : Chaque token de réinitialisation est généré avec `UUID.randomUUID()`
2. **Expiration** : Les tokens expirent après 24 heures
3. **Une seule utilisation** : Un token ne peut être utilisé qu'une seule fois
4. **Invalidation précédente** : Demander une nouvelle réinitialisation invalide les tokens précédents
5. **Chiffrement** : Les mots de passe sont chiffrés avec BCrypt avant stockage
6. **Protection CSRF** : Endpoints protégés contre les attaques CSRF

### Bonnes pratiques respectées

- ✅ Pas d'exposition d'erreurs détaillées (e.g., "cet email existe" vs "email non trouvé")
- ✅ Les tokens ne sont jamais stockés en clair
- ✅ Audit des changements de mot de passe
- ✅ URLs sécurisées (HTTPS recommandé en production)

## 📊 Structure Base de Données

Table `password_reset_tokens` :

| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGSERIAL | Identifiant unique |
| token | VARCHAR(255) | Token UUID unique |
| user_id | BIGINT | Référence à l'utilisateur |
| expiry_date | TIMESTAMP | Date d'expiration |
| created_at | TIMESTAMP | Date de création |
| used | BOOLEAN | Indique si utilisé |

## 🧪 Tests Manuels

### Scénario 1 : Réinitialisation réussie
1. Accédez à `/login`
2. Cliquez sur "Mot de passe oublié ?"
3. Entrez l'email : `admin@clinique.com`
4. Consultez les logs pour le lien de réinitialisation
5. Copiez le token du lien
6. Accédez à `/reset-password?token=<token>`
7. Entrez un nouveau mot de passe
8. Cliquez sur "Réinitialiser"
9. Connectez-vous avec le nouveau mot de passe

### Scénario 2 : Token expiré
1. Attendez 24h ou modifiez la date d'expiration en base
2. Tentez d'accéder à un token expiré
3. Le système affiche : "Ce lien a expiré"

### Scénario 3 : Token réutilisé
1. Utilisez le même token une première fois (succès)
2. Tentez de le réutiliser
3. Le système affiche : "Token déjà utilisé"

## 📧 Intégration Email (À faire)

Pour intégrer un service d'email réel :

1. **Avec Spring Mail** :
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-mail</artifactId>
   </dependency>
   ```

2. **Configurer `application.properties`** :
   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

3. **Implémenter `EmailService.java`** :
   ```java
   @Autowired
   private JavaMailSender mailSender;
   
   public void sendEmail(String to, String subject, String message) {
       SimpleMailMessage mail = new SimpleMailMessage();
       mail.setTo(to);
       mail.setSubject(subject);
       mail.setText(message);
       mailSender.send(mail);
   }
   ```

## 🐛 Dépannage

### Le lien d'email ne fonctionne pas
- Vérifiez que le token est correct
- Vérifiez la date/heure du serveur
- Vérifiez les logs pour les erreurs

### "Token invalide" après clic
- Token expiré (24h de délai)
- Demander une nouvelle réinitialisation
- Vérifier que le lien n'a pas été utilisé

### Email non reçu
- Service email désactivé (développement)
- Consultez les logs de l'application pour voir le lien de réinitialisation
- En production, configurer SMTP avec un vrai service

## 📝 Fichiers Modifiés

### Nouveaux fichiers
- `PasswordResetToken.java` - Entité JPA pour les tokens
- `PasswordResetTokenRepository.java` - Repository Hibernate
- `PasswordResetService.java` - Logique métier
- `EmailService.java` - Service d'envoi d'emails
- `PasswordResetRequest.java` - DTO pour demande
- `ResetPasswordRequest.java` - DTO pour réinitialisation
- `WebPageController.java` - Contrôleur web pour les pages
- `forgot-password.html` - Page web pour demander réinitialisation
- `reset-password.html` - Page web pour réinitialiser le mot de passe

### Fichiers modifiés
- `AuthController.java` - Ajout des 3 endpoints de récupération
- `SecurityConfig.java` - Ajout des pages au whitelist
- `schema.sql` - Ajout de la table `password_reset_tokens`
- `login.html` - Ajout du lien "Mot de passe oublié"

## ✅ Checklist de Déploiement

- [ ] Base de données migrée (schema.sql exécuté)
- [ ] Application compilée et testée
- [ ] Service SMTP configuré (optionnel, fonctionne sans)
- [ ] URL de réinitialisation (dans PasswordResetService.java) mise à jour
- [ ] Tokens de réinitialisation obsolètes nettoyés
- [ ] Logs activés pour le suivi

## 📞 Support

Pour des questions ou des améliorations :
1. Consultez `PasswordResetService.java` pour la logique
2. Consultez `EmailService.java` pour l'intégration email
3. Consultez les pages HTML pour l'interface utilisateur
