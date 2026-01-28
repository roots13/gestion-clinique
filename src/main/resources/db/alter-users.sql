-- Migration pour ajouter les colonnes nom et prenom à la table users
ALTER TABLE users ADD COLUMN IF NOT EXISTS nom VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS prenom VARCHAR(100);
