-- Migration : espace Vendeur + statistiques de remplissage
-- À appliquer sur une base EXISTANTE (production / VPS) sans la recréer.
--
-- Compatible MySQL 8.x ET MariaDB, et idempotent : relancer le script ne casse
-- rien. La syntaxe "ALTER ... ADD COLUMN IF NOT EXISTS" est propre à MariaDB et
-- échoue sous MySQL 8 ; on teste donc l'existence via information_schema puis on
-- exécute le DDL conditionnellement (PREPARE/EXECUTE).
--
--   mysql -u <user> -p billetterie < migration_vendeur_statistiques.sql

USE billetterie;

-- 1) Colonne de rôle sur les utilisateurs (ADMIN / VENDEUR / CLIENT).
SET @ddl := IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'client' AND COLUMN_NAME = 'role') = 0,
    "ALTER TABLE client ADD COLUMN role ENUM('ADMIN','VENDEUR','CLIENT') NOT NULL DEFAULT 'CLIENT'",
    "DO 0");
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Reprise des données existantes : déduire le rôle depuis is_admin.
UPDATE client SET role = 'ADMIN' WHERE is_admin = 1 AND role = 'CLIENT';

-- 2) Propriétaire (vendeur) d'un spectacle, pour les statistiques par vendeur.
SET @ddl := IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'spectacle' AND COLUMN_NAME = 'id_vendeur') = 0,
    "ALTER TABLE spectacle ADD COLUMN id_vendeur INT NULL",
    "DO 0");
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) Index sur id_vendeur (créé AVANT la clé étrangère pour qu'elle le réutilise
--    et éviter un index en double).
SET @ddl := IF(
    (SELECT COUNT(*) FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'spectacle' AND INDEX_NAME = 'idx_spectacle_vendeur') = 0,
    "CREATE INDEX idx_spectacle_vendeur ON spectacle(id_vendeur)",
    "DO 0");
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4) Clé étrangère spectacle.id_vendeur -> client.id.
SET @ddl := IF(
    (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'spectacle'
        AND CONSTRAINT_NAME = 'fk_spectacle_vendeur') = 0,
    "ALTER TABLE spectacle ADD CONSTRAINT fk_spectacle_vendeur FOREIGN KEY (id_vendeur) REFERENCES client(id) ON DELETE SET NULL",
    "DO 0");
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5) Compte vendeur de démonstration (mot de passe = admin123). Idempotent :
--    inséré seulement s'il n'existe pas déjà.
INSERT INTO client (pseudo, nom, prenom, numero, email, password, adresse, is_admin, role)
SELECT 'vendeur', 'Vendeur', 'Demo', '0000000000', 'vendeur@billetterie.local',
       '$2b$12$FomE0ctopyqsXWk4F70fxOcdv6VnXv/81YYKaBJrRGWjL.0N7BN7C',
       '2 rue de la Billetterie', 0, 'VENDEUR'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM client WHERE email = 'vendeur@billetterie.local');

-- 6) Affectation de quelques spectacles au vendeur de démo (pour ses stats).
--    Adapter les ids selon les spectacles présents en base.
UPDATE spectacle
SET id_vendeur = (SELECT id FROM client WHERE email = 'vendeur@billetterie.local')
WHERE id IN (1, 2, 3);
