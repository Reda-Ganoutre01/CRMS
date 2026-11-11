CREATE DATABASE IF NOT EXISTS location_voitures CHARACTER SET utf8mb4;
USE location_voitures;

CREATE TABLE voiture (
                         id         INT AUTO_INCREMENT PRIMARY KEY,
                         marque     VARCHAR(50) NOT NULL,
                         modele     VARCHAR(50) NOT NULL,
                         matricule  VARCHAR(20) NOT NULL UNIQUE,
                         prix_jour  DOUBLE NOT NULL,
                         disponible BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE client (
                        id        INT AUTO_INCREMENT PRIMARY KEY,
                        nom       VARCHAR(50) NOT NULL,
                        prenom    VARCHAR(50) NOT NULL,
                        cin       VARCHAR(20) NOT NULL UNIQUE,
                        telephone VARCHAR(20)
);

CREATE TABLE contrat (
                         id         INT AUTO_INCREMENT PRIMARY KEY,
                         client_id  INT NOT NULL,
                         voiture_id INT NOT NULL,
                         date_debut DATE NOT NULL,
                         date_fin   DATE NOT NULL,
                         total      DOUBLE NOT NULL,
                         statut     VARCHAR(20) NOT NULL DEFAULT 'EN_COURS',
                         FOREIGN KEY (client_id)  REFERENCES client(id),
                         FOREIGN KEY (voiture_id) REFERENCES voiture(id)
);

-- Données de test
INSERT INTO voiture (marque, modele, matricule, prix_jour) VALUES
                                                               ('Dacia',   'Logan',  '12345-A-6', 250),
                                                               ('Renault', 'Clio',   '23456-B-1', 350),
                                                               ('Peugeot', '208',    '34567-D-7', 330);

INSERT INTO client (nom, prenom, cin, telephone) VALUES
                                                     ('Alaoui', 'Youssef', 'EE123456', '0661123456'),
                                                     ('Benani', 'Salma',   'EE654321', '0662987654');