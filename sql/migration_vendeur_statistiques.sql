-- Migration : espace Vendeur + statistiques de remplissage
-- À appliquer sur une base EXISTANTE (production / VPS) sans la recréer.
-- Idempotent autant que possible : relancer le script ne casse rien.
--
--   mysql -u <user> -p billetterie < migration_vendeur_statistiques.sql

USE billetterie;

-- 1) Colonne de rôle sur les utilisateurs (ADMIN / VENDEUR / CLIENT).
ALTER TABLE client
    ADD COLUMN IF NOT EXISTS role ENUM('ADMIN','VENDEUR','CLIENT') NOT NULL DEFAULT 'CLIENT';

-- Reprise des données existantes : déduire le rôle depuis is_admin.
UPDATE client SET role = 'ADMIN'  WHERE is_admin = 1 AND role = 'CLIENT';

-- 2) Propriétaire (vendeur) d'un spectacle, pour les statistiques par vendeur.
ALTER TABLE spectacle
    ADD COLUMN IF NOT EXISTS id_vendeur INT NULL;

-- Clé étrangère + index (ignorer l'erreur si déjà présents lors d'un rejeu).
ALTER TABLE spectacle
    ADD CONSTRAINT fk_spectacle_vendeur
        FOREIGN KEY (id_vendeur) REFERENCES client(id) ON DELETE SET NULL;

CREATE INDEX idx_spectacle_vendeur ON spectacle(id_vendeur);

-- 3) (Optionnel) Compte vendeur de démonstration — mot de passe = admin123.
-- Décommenter si besoin d'un compte vendeur de test :
-- INSERT INTO client (pseudo, nom, prenom, numero, email, password, adresse, is_admin, role)
-- VALUES ('vendeur', 'Vendeur', 'Demo', '0000000000', 'vendeur@billetterie.local',
--         '$2b$12$FomE0ctopyqsXWk4F70fxOcdv6VnXv/81YYKaBJrRGWjL.0N7BN7C',
--         '2 rue de la Billetterie', 0, 'VENDEUR');
