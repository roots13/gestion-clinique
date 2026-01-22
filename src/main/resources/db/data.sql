-- Données initiales (seed)

-- Utilisateur administrateur par défaut
-- Username: admin
-- Password: admin123
-- Le mot de passe est hashé avec BCrypt (coût 10)
INSERT INTO users (username, email, password, role, enabled) 
VALUES ('admin', 'admin@clinique.local', '$2a$10$aE17PFXtIvlG3rfVcAnrMOW4XC8KmL87ZHgvEYwaFqGAo96R77KZG', 'ADMIN', TRUE)
ON CONFLICT (username) DO UPDATE SET 
    password = EXCLUDED.password,
    email = EXCLUDED.email,
    role = EXCLUDED.role,
    enabled = EXCLUDED.enabled;

-- Utilisateur accueil
-- Username: accueil
-- Password: accueil123
INSERT INTO users (username, email, password, role, enabled) 
VALUES ('accueil', 'accueil@clinique.local', '$2a$10$oKqoAY7bEiPORmF/JXpOleIH9Jt8dXegO9QLhIIViBjAEqkVo06pu', 'ACCUEIL', TRUE)
ON CONFLICT (username) DO UPDATE SET 
    password = EXCLUDED.password,
    email = EXCLUDED.email,
    role = EXCLUDED.role,
    enabled = EXCLUDED.enabled;

-- Utilisateur médecin
-- Username: medecin
-- Password: medecin123
INSERT INTO users (username, email, password, role, enabled) 
VALUES ('medecin', 'medecin@clinique.local', '$2a$10$QEE1X7hu3Ay6J8vn4oy8Xu0fzfm1XA7mqBygWRktC6sMc7o9aSGdC', 'MEDECIN', TRUE)
ON CONFLICT (username) DO UPDATE SET 
    password = EXCLUDED.password,
    email = EXCLUDED.email,
    role = EXCLUDED.role,
    enabled = EXCLUDED.enabled;

-- Utilisateur caissier
-- Username: caissier
-- Password: caissier123
INSERT INTO users (username, email, password, role, enabled) 
VALUES ('caissier', 'caissier@clinique.local', '$2a$10$zCCLBH5u/17xG6d0BQTdYOorKiSGBAX2woxhGhSKM075gHDw8AHUm', 'CAISSIER', TRUE)
ON CONFLICT (username) DO UPDATE SET 
    password = EXCLUDED.password,
    email = EXCLUDED.email,
    role = EXCLUDED.role,
    enabled = EXCLUDED.enabled;
