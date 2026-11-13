CREATE DATABASE IF NOT EXISTS location_voitures CHARACTER SET utf8mb4;
USE location_voitures;

CREATE TABLE IF NOT EXISTS vehicules (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    immatriculation VARCHAR(20) NOT NULL UNIQUE,
    marque          VARCHAR(50) NOT NULL,
    modele          VARCHAR(50) NOT NULL,
    annee           INT DEFAULT 2024,
    carburant       VARCHAR(20) DEFAULT 'DIESEL',
    kilometrage     INT DEFAULT 0,
    prix_jour       DOUBLE NOT NULL,
    image           VARCHAR(100),
    disponible      BOOLEAN NOT NULL DEFAULT TRUE,
    statut          VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
);

CREATE TABLE IF NOT EXISTS utilisateurs (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom          VARCHAR(50) NOT NULL,
    prenom       VARCHAR(50) NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    telephone    VARCHAR(20),
    token        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS clients (
    id             BIGINT PRIMARY KEY,
    cin            VARCHAR(20) NOT NULL UNIQUE,
    adresse        VARCHAR(200),
    numero_permis  VARCHAR(30),
    FOREIGN KEY (id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS contrats (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id    BIGINT NOT NULL,
    vehicule_id  BIGINT NOT NULL,
    date_debut   DATE NOT NULL,
    date_fin     DATE NOT NULL,
    montant_total DOUBLE NOT NULL,
    km_depart    INT,
    km_retour    INT,
    statut       VARCHAR(20) NOT NULL DEFAULT 'EN_COURS',
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (vehicule_id) REFERENCES vehicules(id)
);

-- Données de test pour les véhicules avec photos réalistes
INSERT INTO vehicules (marque, modele, immatriculation, annee, carburant, kilometrage, prix_jour, image, disponible, statut) VALUES
('Dacia',   'Logan', '12345-A-6', 2024, 'DIESEL',  25000, 250, 'images/cars/dacia-logan.jpg',  TRUE,  'DISPONIBLE'),
('Renault', 'Clio',  '23456-B-1', 2023, 'ESSENCE', 18000, 350, 'images/cars/renault-clio.jpg', FALSE, 'LOUE'),
('Peugeot', '208',   '34567-D-7', 2024, 'ESSENCE', 12000, 330, 'images/cars/peugeot-208.jpg',  TRUE,  'DISPONIBLE')
ON DUPLICATE KEY UPDATE prix_jour=VALUES(prix_jour), image=VALUES(image);