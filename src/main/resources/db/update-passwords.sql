-- Script pour mettre à jour les mots de passe des utilisateurs existants
-- À exécuter manuellement dans la base de données si les utilisateurs existent déjà

-- Mise à jour du mot de passe admin (admin123)
UPDATE users 
SET password = '$2a$10$aE17PFXtIvlG3rfVcAnrMOW4XC8KmL87ZHgvEYwaFqGAo96R77KZG'
WHERE username = 'admin';

-- Mise à jour du mot de passe accueil (accueil123)
UPDATE users 
SET password = '$2a$10$oKqoAY7bEiPORmF/JXpOleIH9Jt8dXegO9QLhIIViBjAEqkVo06pu'
WHERE username = 'accueil';

-- Mise à jour du mot de passe medecin (medecin123)
UPDATE users 
SET password = '$2a$10$QEE1X7hu3Ay6J8vn4oy8Xu0fzfm1XA7mqBygWRktC6sMc7o9aSGdC'
WHERE username = 'medecin';

-- Mise à jour du mot de passe caissier (caissier123)
UPDATE users 
SET password = '$2a$10$zCCLBH5u/17xG6d0BQTdYOorKiSGBAX2woxhGhSKM075gHDw8AHUm'
WHERE username = 'caissier';
