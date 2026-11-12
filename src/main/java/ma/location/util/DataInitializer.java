package ma.location.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ma.location.dao.AuthDao;
import ma.location.model.Client;
import ma.location.model.Offre;
import ma.location.model.Role;
import ma.location.model.Utilisateur;
import ma.location.model.Vehicule;
import ma.location.model.StatutVehicule;

import java.time.LocalDate;
import java.util.UUID;

public final class DataInitializer {

    private DataInitializer() {
    }

    public static void initialize() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            long users = count(em, Utilisateur.class);
            long vehicles = count(em, Vehicule.class);
            long offers = count(em, Offre.class);
            if (users > 0 || vehicles > 0 || offers > 0) {
                return;
            }

            AuthDao authDao = new AuthDao();
            authDao.setEntityManager(em);

            tx.begin();
            em.persist(admin(authDao));
            em.persist(client(authDao));
            em.persist(vehicle("Dacia", "Logan", "12345-A-6", 250));
            em.persist(vehicle("Renault", "Clio", "23456-B-1", 350));
            em.persist(vehicle("Peugeot", "208", "34567-D-7", 330));
            em.persist(offer());
            tx.commit();
        } catch (RuntimeException exception) {
            if (tx.isActive()) {
                tx.rollback();
            }
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
        admin.setAdresse("Administration");
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
        client.setAdresse("Rabat");
        client.setNumeroPermis("P123456");
        client.setToken(token());
        client.setRole(Role.USER);
        return client;
    }

    private static Vehicule vehicle(String marque, String modele, String immatriculation, double prixJour) {
        Vehicule vehicle = new Vehicule();
        vehicle.setMarque(marque);
        vehicle.setModele(modele);
        vehicle.setImmatriculation(immatriculation);
        vehicle.setAnnee(2024);
        vehicle.setCarburant("Essence");
        vehicle.setKilometrage(0);
        vehicle.setPrixJour(prixJour);
        vehicle.setDisponible(true);
        vehicle.setStatut(StatutVehicule.DISPONIBLE);
        return vehicle;
    }

    private static Offre offer() {
        Offre offer = new Offre();
        offer.setTitre("Offre bienvenue");
        offer.setDescription("Réduction de bienvenue sur une location.");
        offer.setPourcentageRemise(10);
        offer.setDateDebut(LocalDate.now());
        offer.setDateFin(LocalDate.now().plusMonths(3));
        return offer;
    }

    private static String token() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
}
