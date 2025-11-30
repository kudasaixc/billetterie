-- Billetterie database template
-- MySQL 8.x compatible script to initialize the schema and seed demo data
-- Import this file in MySQL (e.g., with `mysql -u root -p < billetterie_template.sql`)

DROP DATABASE IF EXISTS billetterie;
CREATE DATABASE billetterie DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE billetterie;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- TABLE CLIENT
CREATE TABLE client (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pseudo VARCHAR(50) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    numero VARCHAR(20),
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    adresse VARCHAR(255),
    is_admin TINYINT(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- TABLE SPECTACLE
CREATE TABLE spectacle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(150) NOT NULL,
    lieu VARCHAR(150),
    affiche VARCHAR(255),
    tags VARCHAR(255),
    duree INT,
    description_courte TEXT,
    description_longue TEXT,
    langue VARCHAR(50),
    age_minimum INT,
    photos VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- TABLE REPRESENTATION (prix PAR REPRESENTATION + places_disponibles)
CREATE TABLE representation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_spectacle INT NOT NULL,
    date_heure DATETIME NOT NULL,
    salle VARCHAR(150),
    prix DOUBLE NOT NULL,
    places_disponibles INT NOT NULL,
    CONSTRAINT fk_representation_spectacle
        FOREIGN KEY (id_spectacle) REFERENCES spectacle(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- TABLE BILLET (plusieurs billets générés par commande)
CREATE TABLE billet (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    id_representation INT NOT NULL,
    id_client INT NOT NULL,
    prix DOUBLE NOT NULL,
    date_achat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_billet_representation
        FOREIGN KEY (id_representation) REFERENCES representation(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_billet_client
        FOREIGN KEY (id_client) REFERENCES client(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- DEMO DATA --------------------------------------------------------------
INSERT INTO spectacle (id, titre, lieu, tags, duree, description_courte, description_longue, langue, age_minimum) VALUES
(1, 'L2B - Live Tour 2025', 'Accor Arena', 'concert,rap', 120,
 'Concert explosif de L2B à Paris.',
 'L2B revient avec un show complet mêlant scénographie et lourds morceaux.', 'FR', 12),
(2, 'Tiakola - Mélo Tour', 'Zénith Paris', 'concert,rap', 110,
 'Tiakola en live au Zénith.',
 'Un concert immersif mêlant voix, mélo et performance scénique.', 'FR', 12),
(3, 'Gazo - KMT Show', 'Arena La Défense', 'concert,rap', 105,
 'Le show KMT de Gazo.',
 'Ambiance drill, lights de fou, expérience brutale et musicale.', 'FR', 14),
(4, 'La Mano - European Tour', 'Zénith Strasbourg', 'concert,rap', 100,
 'La Mano débarque en tournée européenne.',
 'Un concert intense, authentique, produit pour la scène.', 'FR', 12),
(5, 'Genezio - Mode Speed Tour', 'Dôme Marseille', 'concert,rap', 95,
 'Genezio présente son projet Mode Speed.',
 'Performance 100% énergie, maitrise scénique au top.', 'FR', 12),
(6, 'Gims - Ceinture Noire Live', 'Parc des Princes', 'concert,pop', 140,
 'Gims en show monumental.',
 'Un des plus gros shows de France, lumières et pyro au programme.', 'FR', 8),
(7, 'Ballet Modern Fusion', 'Opéra Garnier', 'danse,contemporain', 90,
 'Un ballet moderne mêlant classique et contemporain.',
 'Fusion parfaite entre danse académique et mouvements modernes.', 'FR', 6),
(8, 'Street Dance Revolution', 'Théâtre du Châtelet', 'danse,urbain', 80,
 'Show street dance avec crew internationaux.',
 'Performance urbaine spectaculaire, rythmes et acrobaties.', 'FR', 8),
(9, 'Casse-Noisette 2.0', 'Opéra de Lyon', 'danse,classique', 120,
 'Version revisitée du ballet classique.',
 'Scénographie moderne inspirée du ballet original.', 'FR', 6),
(10, 'Paul Mirabel - Parce que', 'Théâtre de l''Œuvre', 'humour,standup', 85,
 'Spectacle sensible et drôle de Paul Mirabel.',
 'Du calme, du malaise, mais surtout du rire.', 'FR', 12),
(11, 'Redouane Bougheraba - On m’appelle Marseille', 'Zénith Montpellier', 'humour,standup', 90,
 'Redouane en tournée dans toute la France.',
 'Impro, vannes, Marseille… du lourd.', 'FR', 14),
(12, 'Inès Reg - Hors Normes', 'Palais des Congrès', 'humour,standup', 95,
 'Nouveau spectacle d’Inès Reg.',
 'Un stand-up frais, moderne, avec du jeu de scène.', 'FR', 8);

ALTER TABLE spectacle AUTO_INCREMENT = 13;

INSERT INTO representation (id, id_spectacle, date_heure, salle, prix, places_disponibles) VALUES
-- L2B
(1, 1, '2025-12-10 20:00:00', 'Accor Arena', 65, 100),
(2, 1, '2025-12-11 20:00:00', 'Accor Arena', 70, 100),

-- Tiakola
(3, 2, '2025-12-05 20:00:00', 'Zénith Paris', 55, 100),
(4, 2, '2025-12-06 20:00:00', 'Zénith Paris', 60, 100),

-- Gazo
(5, 3, '2025-12-20 21:00:00', 'Paris La Défense Arena', 85, 120),
(6, 3, '2025-12-21 21:00:00', 'Paris La Défense Arena', 90, 120),

-- La Mano
(7, 4, '2025-12-02 20:00:00', 'Zénith Strasbourg', 50, 100),
(8, 4, '2025-12-03 20:00:00', 'Zénith Strasbourg', 55, 100),

-- Genezio
(9, 5, '2025-12-14 20:00:00', 'Dôme de Marseille', 48, 100),
(10, 5, '2025-12-15 20:00:00', 'Dôme de Marseille', 52, 100),

-- Gims
(11, 6, '2025-12-30 20:45:00', 'Parc des Princes', 95, 150),
(12, 6, '2025-12-31 20:45:00', 'Parc des Princes', 120, 150),

-- Ballet / Danse
(13, 7, '2025-12-08 19:00:00', 'Opéra Garnier', 75, 80),
(14, 8, '2025-12-03 20:00:00', 'Théâtre du Châtelet', 50, 90),
(15, 9, '2025-12-26 19:30:00', 'Opéra de Lyon', 60, 100),

-- Humour / Standup
(16, 10, '2025-12-01 20:00:00', 'Théâtre de l''Œuvre', 35, 150),
(17, 11, '2025-12-18 20:30:00', 'Zénith Montpellier', 40, 120),
(18, 12, '2025-12-22 20:30:00', 'Palais des Congrès', 32, 200);

ALTER TABLE representation AUTO_INCREMENT = 19;

-- CLIENT DEMO ACCOUNT (password = admin123)
INSERT INTO client (id, pseudo, nom, prenom, numero, email, password, adresse, is_admin) VALUES
(1, 'admin', 'Admin', 'User', '0000000000', 'admin@billetterie.local', '$2b$12$FomE0ctopyqsXWk4F70fxOcdv6VnXv/81YYKaBJrRGWjL.0N7BN7C', '1 rue de la Billetterie', 1);

ALTER TABLE client AUTO_INCREMENT = 2;

-- INDEXES ---------------------------------------------------------------
CREATE INDEX idx_client_pseudo ON client(pseudo);
CREATE INDEX idx_client_nom_prenom ON client(nom, prenom);
CREATE UNIQUE INDEX idx_client_email_unique ON client(email);

CREATE INDEX idx_spectacle_titre ON spectacle(titre);
CREATE INDEX idx_spectacle_tags ON spectacle(tags);

CREATE INDEX idx_rep_spectacle ON representation(id_spectacle);
CREATE INDEX idx_rep_date ON representation(date_heure);
CREATE INDEX idx_rep_prix ON representation(prix);

CREATE UNIQUE INDEX idx_billet_numero_unique ON billet(numero);
CREATE INDEX idx_billet_rep ON billet(id_representation);
CREATE INDEX idx_billet_client ON billet(id_client);
CREATE INDEX idx_billet_date ON billet(date_achat);
