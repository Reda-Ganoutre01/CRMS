# Rapport de projet - CRMS

## 1. Présentation générale

**CRMS** est une application web de gestion d'une agence de location de voitures. Elle permet de publier un parc automobile, de gérer les clients, de recevoir des demandes de réservation, de transformer une réservation confirmée en contrat et de suivre la facturation.

L'application est réalisée avec Java 17 et Jakarta EE 10. Elle utilise des Servlets, des pages JSP/JSTL, JPA avec Hibernate et une base de données MySQL. L'application est empaquetée sous forme de fichier WAR et exécutée sur Apache Tomcat 10.1 ou supérieur.

## 2. Objectifs

- Centraliser la gestion des véhicules et de leur disponibilité.
- Permettre aux visiteurs de consulter le catalogue sans authentification.
- Permettre aux clients de créer un compte et d'envoyer des réservations.
- Éviter les doubles réservations sur des périodes qui se chevauchent.
- Donner à l'administrateur une vue complète sur les clients, réservations et contrats.
- Automatiser le calcul des montants et la mise à jour des contrats expirés.
- Fournir une facture consultable et imprimable pour chaque contrat.

## 3. Acteurs et droits

| Acteur | Droits principaux |
|---|---|
| Visiteur | Voir les véhicules, rechercher, filtrer par date, accéder à la connexion et à l'inscription |
| Client (`USER`) | Gérer son profil, créer une réservation et consulter ses réservations |
| Administrateur (`ADMIN`) | Gérer les véhicules, clients, réservations et contrats |

### Diagramme des cas d'utilisation

```mermaid
flowchart LR
    Visitor[Visiteur]
    Client[Client USER]
    Admin[Administrateur ADMIN]

    Catalog((Consulter le catalogue))
    Search((Rechercher un véhicule))
    Filter((Filtrer par date))
    Register((Créer un compte))
    Login((Se connecter))
    Booking((Créer une réservation))
    MyBookings((Consulter mes réservations))
    Profile((Modifier le profil))
    VehicleAdmin((Gérer les véhicules))
    ClientAdmin((Gérer les clients))
    ReservationAdmin((Valider / supprimer les réservations))
    ContractAdmin((Démarrer / clôturer les contrats))
    Invoice((Consulter la facture))

    Visitor --> Catalog
    Visitor --> Search
    Visitor --> Filter
    Visitor --> Register
    Visitor --> Login
    Visitor --> Booking
    Client --> Login
    Client --> Booking
    Client --> MyBookings
    Client --> Profile
    Admin --> VehicleAdmin
    Admin --> ClientAdmin
    Admin --> ReservationAdmin
    Admin --> ContractAdmin
    Admin --> Invoice
```

## 4. Architecture technique

L'application suit une architecture en couches :

```mermaid
flowchart TB
    Browser[Navigateur web]
    Tomcat[Apache Tomcat 10.1]
    Filters[Filtres\nEncodingFilter + AuthenticationFilter]
    Servlets[Servlets MVC\nAuth, Vehicule, Reservation, Contrat, Client, Profile]
    Services[Services métier\nClientService, ReservationService, ContratService]
    DAO[DAO\nAccès aux données]
    JPA[JPA / Hibernate]
    MySQL[(MySQL\nlocation_voitures)]

    Browser --> Tomcat
    Tomcat --> Filters
    Filters --> Servlets
    Servlets --> Services
    Services --> DAO
    DAO --> JPA
    JPA --> MySQL
    Servlets --> JSP[JSP / JSTL]
    JSP --> Browser
```

### Organisation du code

```text
src/main/java/ma/location/
├── model/       Entités JPA et énumérations
├── dao/         Requêtes et opérations de persistence
├── service/     Règles métier et validations
├── servlet/     Contrôleurs HTTP et API
├── filter/      Encodage et authentification
└── util/        JPA, initialisation, images

src/main/webapp/
├── WEB-INF/views/   Pages JSP
├── WEB-INF/jspf/    Fragments partagés
└── images/          Images statiques
```

## 5. Modèle de données

### Diagramme de classes métier

```mermaid
classDiagram
    class Utilisateur {
        +Long id
        +String nom
        +String prenom
        +String email
        +String motDePasse
        +String telephone
        +String token
        +Role role
    }

    class Client {
        +String cin
        +String adresse
        +String numeroPermis
    }

    class Vehicule {
        +Long id
        +String immatriculation
        +String marque
        +String modele
        +int annee
        +String carburant
        +int kilometrage
        +String image
        +double prixJour
        +boolean disponible
        +StatutVehicule statut
    }

    class Reservation {
        +Long id
        +LocalDate dateDebut
        +LocalDate dateFin
        +double montantTotal
        +StatutReservation statut
    }

    class Contrat {
        +Long id
        +LocalDate dateDebut
        +LocalDate dateFin
        +double montantTotal
        +StatutContrat statut
        +long getNbJours()
    }

    class Offre {
        +Long id
        +String titre
        +String description
        +double pourcentageRemise
        +LocalDate dateDebut
        +LocalDate dateFin
    }

    Utilisateur <|-- Client
    Client "1" --> "0..*" Reservation : effectue
    Client "1" --> "0..*" Contrat : signe
    Vehicule "1" --> "0..*" Reservation : concerne
    Vehicule "1" --> "0..*" Contrat : est loué dans
    Offre "0..1" --> "0..*" Reservation : applique
```

### Modèle relationnel simplifié

```mermaid
erDiagram
    UTILISATEURS ||--o| CLIENTS : "est"
    CLIENTS ||--o{ RESERVATIONS : effectue
    CLIENTS ||--o{ CONTRATS : signe
    VEHICULES ||--o{ RESERVATIONS : concerne
    VEHICULES ||--o{ CONTRATS : concerne
    OFFRES ||--o{ RESERVATIONS : applique

    UTILISATEURS {
        bigint id PK
        varchar nom
        varchar prenom
        varchar email UK
        varchar mot_de_passe
        varchar telephone
        varchar token
        varchar role
    }
    CLIENTS {
        bigint id PK,FK
        varchar cin UK
        varchar adresse
        varchar numero_permis
    }
    VEHICULES {
        bigint id PK
        varchar immatriculation UK
        varchar marque
        varchar modele
        int annee
        varchar carburant
        int kilometrage
        decimal prix_jour
        boolean disponible
        varchar statut
    }
    RESERVATIONS {
        bigint id PK
        bigint client_id FK
        bigint vehicule_id FK
        bigint offre_id FK
        date date_debut
        date date_fin
        decimal montant_total
        varchar statut
    }
    CONTRATS {
        bigint id PK
        bigint client_id FK
        bigint vehicule_id FK
        date date_debut
        date date_fin
        decimal montant_total
        varchar statut
    }
    OFFRES {
        bigint id PK
        varchar titre
        varchar description
        decimal pourcentage_remise
        date date_debut
        date date_fin
    }
```

## 6. États métier

```mermaid
stateDiagram-v2
    [*] --> EN_ATTENTE: Client crée une réservation
    EN_ATTENTE --> CONFIRMEE: Administrateur valide
    EN_ATTENTE --> ANNULEE: Annulation / suppression
    CONFIRMEE --> TERMINEE: Contrat démarré
    CONFIRMEE --> ANNULEE: Annulation
    TERMINEE --> [*]
    ANNULEE --> [*]
```

```mermaid
stateDiagram-v2
    [*] --> ACTIF: Contrat créé
    ACTIF --> TERMINE: Clôture manuelle
    ACTIF --> TERMINE: Date de fin dépassée
    TERMINE --> [*]
```

Les états utilisés par le domaine sont :

```text
Reservation : EN_ATTENTE, CONFIRMEE, ANNULEE, TERMINEE
Contrat     : ACTIF, TERMINE
Vehicule    : DISPONIBLE, LOUE, EN_MAINTENANCE
Utilisateur : USER, ADMIN
```

## 7. Processus de réservation

```mermaid
sequenceDiagram
    actor Client
    participant UI as JSP véhicules
    participant Filter as AuthenticationFilter
    participant RS as ReservationServlet
    participant Service as ReservationService
    participant DB as MySQL

    Client->>UI: Choisit un véhicule et une période
    Client->>RS: GET /reservations/nouvelle
    alt Non authentifié
        Filter-->>Client: Redirection vers /login?redirect=...
        Client->>Filter: Connexion
        Filter-->>Client: Retour vers la réservation
    end
    Client->>RS: POST dates et véhicule
    RS->>Service: creer(client, véhicule, offre, période)
    Service->>DB: Vérifie les réservations qui se chevauchent
    Service->>DB: Vérifie les contrats actifs qui se chevauchent
    alt Période disponible
        Service->>DB: Enregistre EN_ATTENTE
        Service-->>RS: Réservation créée
        RS-->>Client: Message de succès
    else Période occupée
        Service-->>RS: Erreur de disponibilité
        RS-->>Client: Message d'erreur
    end
```

La règle de chevauchement est :

```text
dateDebutExistante < dateFinDemandee
ET
dateFinExistante > dateDebutDemandee
```

Une réservation `EN_ATTENTE` ou `CONFIRMEE`, ainsi qu'un contrat `ACTIF`, bloque le véhicule uniquement sur la période concernée. Les réservations `ANNULEE` et `TERMINEE` ne bloquent pas une nouvelle réservation.

## 8. Processus administratif

```mermaid
sequenceDiagram
    actor Admin
    participant UI as Interface admin
    participant Servlet as Servlets admin
    participant Service as Services métier
    participant DB as MySQL

    Admin->>UI: Consulte les réservations
    Admin->>Servlet: Valide une réservation
    Servlet->>Service: confirmer(id, admin)
    Service->>DB: Vérifie les conflits
    Service->>DB: Passe la réservation à CONFIRMEE
    Servlet-->>Admin: Alerte de succès
    Admin->>Servlet: Démarre le contrat
    Servlet->>Service: creerDepuisReservation(id)
    Service->>DB: Crée un contrat ACTIF
    Service->>DB: Met à jour le véhicule
    Servlet-->>Admin: Contrat affiché
    Admin->>Servlet: Clôture le contrat
    Servlet->>Service: cloturer(id)
    Service->>DB: Passe le contrat à TERMINE
    Service->>DB: Rend le véhicule disponible
    Servlet-->>Admin: Facture imprimable
```

## 9. Disponibilité des véhicules

La disponibilité présentée dans le catalogue est calculée selon deux notions :

1. **Disponibilité actuelle** : le véhicule est considéré indisponible si une réservation `EN_ATTENTE` ou `CONFIRMEE`, ou un contrat `ACTIF`, couvre la date du jour.
2. **Disponibilité à partir d'une date** : le filtre exclut les véhicules dont une réservation ou un contrat actif couvre la date demandée.

Les contrats actifs dont `dateFin <= aujourd'hui` sont automatiquement clôturés lors de certains chargements applicatifs. Cette synchronisation remet également le véhicule à l'état disponible.

## 10. Authentification et sécurité

- Les mots de passe sont hachés avec BCrypt.
- Le rôle est stocké dans la session après authentification.
- `AuthenticationFilter` protège les routes privées.
- Les routes publiques comprennent le catalogue, la connexion, l'inscription, la déconnexion et les ressources statiques.
- Les opérations d'administration vérifient explicitement le rôle `ADMIN` dans les servlets et services.
- Les redirections de connexion conservent la destination demandée après authentification.
- Les réponses et messages affichés utilisent les mécanismes JSP d'échappement lorsque cela est nécessaire.

## 11. Routes principales

| Route | Méthode | Accès | Rôle |
|---|---|---|---|
| `/vehicules` | GET | Public | Catalogue et filtres |
| `/login` | GET/POST | Public | Connexion |
| `/register` | GET/POST | Public | Création de compte |
| `/logout` | GET/POST | Authentifié | Déconnexion |
| `/mes-reservations` | GET | Client | Réservations personnelles |
| `/reservations/nouvelle` | GET/POST | Client | Création de réservation |
| `/admin/reservations` | GET | Admin | Liste des réservations |
| `/admin/reservations/valider` | POST | Admin | Validation |
| `/admin/contrats` | GET | Admin | Liste des contrats |
| `/admin/contrats/demarrer` | POST | Admin | Création d'un contrat |
| `/admin/contrats/cloturer` | POST | Admin | Clôture et facture |
| `/clients` | GET | Admin | Gestion des clients |
| `/profile` | GET/POST | Client | Gestion du profil |

La documentation de l'API d'authentification est disponible dans [`docs/API.md`](docs/API.md). Les endpoints REST décrits comme futurs dans ce document doivent être distingués des routes web JSP réellement disponibles.

## 12. Installation et déploiement

### Prérequis

- JDK 17.
- Maven 3.9 ou supérieur.
- MySQL 8.x.
- Tomcat 10.1 ou supérieur.

### Configuration

La persistence est définie dans `src/main/resources/META-INF/persistence.xml`. La configuration locale par défaut utilise :

```text
URL      jdbc:mysql://localhost:3306/location_voitures
Utilisateur root
Mot de passe 1234
```

Pour un déploiement réel, il faut remplacer les identifiants par un compte MySQL dédié et ne pas conserver de mot de passe sensible dans le dépôt.

### Compilation

```bash
mvn clean package
```

Le fichier généré est :

```text
target/location-voitures.war
```

Copier ce fichier dans `TOMCAT_HOME/webapps`, démarrer Tomcat et ouvrir :

```text
http://localhost:8080/location-voitures/vehicules
```

## 13. Tests et validation

Commandes prévues :

```bash
mvn -DskipTests compile
mvn test
```

Scénarios fonctionnels à vérifier :

1. Un visiteur peut ouvrir `/vehicules` sans connexion.
2. Un visiteur qui réserve est redirigé vers `/login`, puis revient à la réservation.
3. Un nouveau compte reçoit le rôle `USER` et ouvre `/mes-reservations`.
4. Un utilisateur `USER` ne peut pas accéder au tableau de bord administrateur.
5. Une date couverte par une réservation confirmée exclut le véhicule.
6. Une réservation terminée ne bloque plus une période future.
7. Une réservation confirmée peut être transformée en contrat par un administrateur.
8. La clôture d'un contrat rend le véhicule disponible et affiche la facture.
9. Les messages de succès et d'erreur sont affichés après les actions.

## 14. Limites et améliorations possibles

- Ajouter des tests automatisés unitaires et d'intégration.
- Remplacer les identifiants de développement par une configuration externe obligatoire.
- Ajouter une tâche planifiée pour clôturer les contrats expirés sans attendre le chargement d'une page.
- Ajouter une gestion explicite des annulations côté client et administrateur.
- Ajouter une pagination aux grandes listes.
- Compléter les ressources REST annoncées dans la documentation API.
- Ajouter une journalisation métier et un audit des actions administratives.
- Ajouter des contraintes et index SQL sur les dates et les clés étrangères.

## 15. Conclusion

CRMS fournit un flux complet de location : consultation publique, authentification, réservation, validation administrative, contractualisation, clôture et facturation. La séparation entre modèle, DAO, services et servlets facilite l'évolution de l'application et permet d'ajouter progressivement des tests, une API REST plus complète et des fonctions avancées de suivi.
