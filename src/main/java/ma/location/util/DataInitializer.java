package ma.location.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ma.location.dao.AuthDao;
import ma.location.model.Client;
import ma.location.model.Offre;
import ma.location.model.Role;
import ma.location.model.StatutVehicule;
import ma.location.model.Utilisateur;
import ma.location.model.Vehicule;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initialiseur automatique de données de démonstration au démarrage de l'application.
 * S'exécute automatiquement via AppListener (ServletContextListener) dès le lancement de Tomcat.
 * Si les tables sont vides, insère les véhicules avec leurs photos, les comptes (Admin et Client) et une offre promotionnelle.
 * Si les véhicules existent déjà mais n'ont pas d'image, met à jour automatiquement leurs chemins de photos.
 */
public final class DataInitializer {

    private static final Logger LOGGER = Logger.getLogger(DataInitializer.class.getName());

    private DataInitializer() {
    }

    public static void initialize() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            long usersCount = count(em, Utilisateur.class);
            long vehiclesCount = count(em, Vehicule.class);
            long offersCount = count(em, Offre.class);

            AuthDao authDao = new AuthDao();
            authDao.setEntityManager(em);

            // 1. Initialiser les utilisateurs si aucun utilisateur n'existe
            if (usersCount == 0) {
                LOGGER.info("[CRMS DataInitializer] Création des comptes de test (Admin et Client)...");
                em.persist(admin(authDao));
                em.persist(client(authDao));
                LOGGER.info("[CRMS DataInitializer] Comptes créés avec succès (admin@crms.local / client@crms.local).");
            }

            // 2. Initialiser les véhicules si aucun véhicule n'existe
            if (vehiclesCount == 0) {
                LOGGER.info("[CRMS DataInitializer] Insertion du parc automobile avec photos...");
                em.persist(vehicle("Dacia", "Logan", "12345-A-6", 2024, "DIESEL", 25000, 250.0,
                        "dacia-logan.jpg", true, StatutVehicule.DISPONIBLE));

                em.persist(vehicle("Renault", "Clio", "23456-B-1", 2023, "ESSENCE", 18000, 350.0,
                        "renault-clio.jpg", false, StatutVehicule.LOUE));

                em.persist(vehicle("Peugeot", "208", "34567-D-7", 2024, "ESSENCE", 12000, 330.0,
                        "peugeot-208.jpg", true, StatutVehicule.DISPONIBLE));

                LOGGER.info("[CRMS DataInitializer] 3 véhicules insérés avec leurs photos.");
            } else {
                // Si les véhicules existent déjà, vérifier et assigner les photos si manquantes
                List<Vehicule> vehiculesSansImage = em.createQuery(
                        "SELECT v FROM Vehicule v WHERE v.image IS NULL OR v.image = ''", Vehicule.class)
                        .getResultList();

                for (Vehicule v : vehiculesSansImage) {
                    String modele = (v.getModele() != null) ? v.getModele().toLowerCase() : "";
                    if (modele.contains("clio")) {
                        v.setImage("renault-clio.jpg");
                    } else if (modele.contains("208")) {
                        v.setImage("peugeot-208.jpg");
                    } else {
                        v.setImage("dacia-logan.jpg");
                    }
                    em.merge(v);
                }
                if (!vehiculesSansImage.isEmpty()) {
                    LOGGER.info("[CRMS DataInitializer] Photos assignées à " + vehiculesSansImage.size() + " véhicules existants.");
                }
            }

            // 3. Initialiser les offres promotionnelles si aucune offre n'existe
            if (offersCount == 0) {
                LOGGER.info("[CRMS DataInitializer] Création de l'offre promotionnelle de bienvenue...");
                em.persist(offer());
                LOGGER.info("[CRMS DataInitializer] Offre promotionnelle créée.");
            }

            tx.commit();
            LOGGER.info("[CRMS DataInitializer] Initialisation automatique terminée avec succès.");
        } catch (RuntimeException exception) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "[CRMS DataInitializer] Erreur lors de l'initialisation des données", exception);
            throw new IllegalStateException("Impossible d'initialiser les données de démonstration.", exception);
        } finally {
            em.close();
        }
    }

    private static long count(EntityManager em, Class<?> entityClass) {
        return em.createQuery("select count(e) from " + entityClass.getSimpleName() + " e", Long.class)
                .getSingleResult();
    }

    private static Client admin(AuthDao authDao) {
        Client admin = new Client();
        admin.setNom("Admin");
        admin.setPrenom("Principal");
        admin.setCin("ADMIN-001");
        admin.setEmail("admin@crms.local");
        admin.setMotDePasse(authDao.hashPassword("Admin1234!"));
        admin.setTelephone("0600000000");
        admin.setAdresse("Administration Centrale");
        admin.setNumeroPermis("P000000");
        admin.setToken(token());
        admin.setRole(Role.ADMIN);
        return admin;
    }

    private static Client client(AuthDao authDao) {
        Client client = new Client();
        client.setNom("Alaoui");
        client.setPrenom("Youssef");
        client.setCin("EE123456");
        client.setEmail("client@crms.local");
        client.setMotDePasse(authDao.hashPassword("Client1234!"));
        client.setTelephone("0611223344");
        client.setAdresse("Rabat Agdal");
        client.setNumeroPermis("P123456");
        client.setToken(token());
        client.setRole(Role.USER);
        return client;
    }

    private static Vehicule vehicle(String marque, String modele, String immatriculation, int annee,
                                   String carburant, int kilometrage, double prixJour, String image,
                                   boolean disponible, StatutVehicule statut) {
        Vehicule vehicle = new Vehicule();
        vehicle.setMarque(marque);
        vehicle.setModele(modele);
        vehicle.setImmatriculation(immatriculation);
        vehicle.setAnnee(annee);
        vehicle.setCarburant(carburant);
        vehicle.setKilometrage(kilometrage);
        vehicle.setPrixJour(prixJour);
        vehicle.setImage(image);
        vehicle.setDisponible(disponible);
        vehicle.setStatut(statut);
        return vehicle;
    }

    private static Offre offer() {
        Offre offer = new Offre();
        offer.setTitre("Offre Spéciale Bienvenue");
        offer.setDescription("Réduction exceptionnelle de 10% sur votre première location.");
        offer.setPourcentageRemise(10.0);
        offer.setDateDebut(LocalDate.now());
        offer.setDateFin(LocalDate.now().plusMonths(3));
        return offer;
    }

    private static String token() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
}
