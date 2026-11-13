# CRMS - Gestion de location de voitures

Application web de gestion d'une agence de location de voitures, développée avec Jakarta EE, Java 17, JSP/JSTL, JPA/Hibernate et MySQL.


## Fonctionnalités

- Consultation publique du catalogue des véhicules.
- Recherche par marque, modèle ou immatriculation.
- Filtrage des véhicules disponibles à partir d'une date.
- Création et suivi des réservations par les clients.
- Validation et suppression des réservations par un administrateur.
- Création, clôture, facturation et suppression des contrats.
- Gestion des véhicules et des clients par un administrateur.
- Authentification par session, rôles `USER` et `ADMIN`.
- API REST d'authentification.
- Messages de succès et d'erreur après les actions importantes.

## Technologies

| Composant | Version |
|---|---|
| Java | 17 |
| Jakarta Servlet | 6.0 |
| Jakarta JSP | 3.1 |
| JSTL | 3.0 |
| JPA / Hibernate | Hibernate 6.4 |
| MySQL | 8.x |
| Tomcat | 10.1 ou supérieur |
| Build | Maven |

## Architecture

```text
src/main/java/ma/location/
├── model/       Entités JPA et énumérations métier
├── dao/         Accès aux données
├── service/     Règles métier et validations
├── servlet/     Contrôleurs web et endpoints REST
├── filter/      Encodage et contrôle d'authentification
└── util/        JPA, initialisation et gestion des images

src/main/webapp/
├── WEB-INF/views/   Pages JSP
├── WEB-INF/jspf/    Fragments JSP partagés
└── images/          Images et ressources statiques
```

## Prérequis

- JDK 17 configuré dans `JAVA_HOME`.
- Maven 3.9 ou supérieur.
- MySQL 8.x démarré localement.
- Tomcat 10.1 ou supérieur.

## Configuration de la base de données

La persistence est configurée dans `src/main/resources/META-INF/persistence.xml`.

Par défaut, l'application utilise :

```text
Base : location_voitures
Hôte : localhost
Port : 3306
Utilisateur : root
Mot de passe : 1234
```

Créez un utilisateur MySQL dédié pour un environnement réel et remplacez ces valeurs avant le déploiement. Hibernate utilise actuellement `hbm2ddl.auto=update`, ce qui met à jour le schéma automatiquement au démarrage.

## Installation et lancement

1. Cloner le projet :

   ```bash
   git clone https://github.com/Reda-Ganoutre01/CRMS.git
   cd CRMS
   ```

2. Vérifier la configuration MySQL dans `persistence.xml`.

3. Compiler et générer le fichier WAR :

   ```bash
   mvn clean package
   ```

4. Copier `target/location-voitures.war` dans le dossier `webapps` de Tomcat, puis démarrer Tomcat.

5. Ouvrir :

   ```text
   http://localhost:8080/location-voitures/vehicules
   ```

Le nom du fichier WAR est défini par `finalName` dans `pom.xml`.

## Parcours principaux

### Visiteur

- `/vehicules` : consulter et filtrer les véhicules.
- `/login` : se connecter.
- `/register` : créer un compte.
- `/reservations/nouvelle` : démarrer une réservation ; un visiteur est redirigé vers la connexion.

### Client connecté

- `/mes-reservations` : consulter ses réservations.
- `/profile` : modifier son profil.
- `/reservations/nouvelle` : envoyer une demande de réservation.

### Administrateur

- `/admin/vehicules` : gérer les véhicules.
- `/clients` : gérer les clients.
- `/admin/reservations` : valider ou supprimer les réservations.
- `/admin/contrats` : démarrer, clôturer, consulter et supprimer les contrats.

## Disponibilité des véhicules

Une réservation `EN_ATTENTE` ou `CONFIRMEE` bloque le véhicule pendant sa période. Un contrat `ACTIF` bloque également le véhicule pendant sa période. Les réservations `ANNULEE` ou `TERMINEE`, ainsi que les contrats `TERMINE`, ne bloquent plus les recherches futures.

Les contrats actifs dont la date de fin est dépassée sont synchronisés automatiquement lors du chargement de certaines pages. La disponibilité affichée aujourd'hui est calculée à partir des réservations et contrats actifs, et pas uniquement à partir du champ historique `disponible`.

## API

La documentation de l'API est disponible dans [`docs/API.md`](docs/API.md).

Endpoint d'authentification :

```text
POST /location-voitures/api/auth/*
```

## Tests et validation

Pour compiler le projet :

```bash
mvn -DskipTests compile
```

Pour vérifier les tests :

```bash
mvn test
```

## Structure des données métier

- `Utilisateur` / `Client` : comptes et informations client.
- `Vehicule` : véhicule, prix journalier et état courant.
- `Reservation` : demande de location et période réservée.
- `Contrat` : location confirmée et période contractuelle.
- `Offre` : réduction applicable à une période donnée.

Les statuts principaux sont :

```text
Reservation : EN_ATTENTE, CONFIRMEE, ANNULEE, TERMINEE
Contrat     : ACTIF, TERMINE
Vehicule    : DISPONIBLE, LOUE, EN_MAINTENANCE
```
