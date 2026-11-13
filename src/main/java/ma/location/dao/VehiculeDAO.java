package ma.location.dao;

import ma.location.model.Vehicule;
import ma.location.util.JPAUtil;

import jakarta.persistence.EntityManager;
import ma.location.model.StatutContrat;
import ma.location.model.StatutReservation;
import java.time.LocalDate;
import java.util.List;

public class VehiculeDAO {

    // Tous les véhicules
    public List<Vehicule> findAll() {
        return findAll(null);
    }

    public List<Vehicule> findAll(String search) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select v from Vehicule v " +
                            "where :search = '' or lower(v.marque) like :pattern " +
                            "or lower(v.modele) like :pattern or lower(v.immatriculation) like :pattern " +
                            "order by v.id desc", Vehicule.class)
                    .setParameter("search", search == null ? "" : search.toLowerCase())
                    .setParameter("pattern", "%" + (search == null ? "" : search.toLowerCase()) + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Seulement les véhicules disponibles
    public List<Vehicule> findDisponibles() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select v from Vehicule v where v.disponible = true order by v.marque", Vehicule.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Vehicule> findDisponiblesEntre(LocalDate debut, String search) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select v from Vehicule v where " +
                                    "(:search = '' or lower(v.marque) like :pattern " +
                                    "or lower(v.modele) like :pattern or lower(v.immatriculation) like :pattern) " +
                                    "and not exists (select r.id from Reservation r " +
                                    "where r.vehicule.id = v.id " +
                                    "and r.statut in (:enAttente, :confirmee) " +
                                    "and r.dateDebut <= :debut and r.dateFin > :debut) " +
                                    "and not exists (select c.id from Contrat c " +
                                    "where c.vehicule.id = v.id and c.statut = :actif " +
                                    "and c.dateDebut <= :debut and c.dateFin > :debut) " +
                                    "order by v.id desc", Vehicule.class)
                    .setParameter("enAttente", StatutReservation.EN_ATTENTE)
                    .setParameter("confirmee", StatutReservation.CONFIRMEE)
                    .setParameter("actif", StatutContrat.ACTIF)
                    .setParameter("search", search == null ? "" : search.toLowerCase())
                    .setParameter("pattern", "%" + (search == null ? "" : search.toLowerCase()) + "%")
                    .setParameter("debut", debut)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public LocalDate findDateDisponibilite(Long vehiculeId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Vehicule vehicule = em.find(Vehicule.class, vehiculeId);
            LocalDate date = LocalDate.now();
            if (vehicule != null) {
                LocalDate reservationEnd = em.createQuery(
                                "select max(r.dateFin) from Reservation r " +
                                        "where r.vehicule.id = :id " +
                                        "and r.statut in (:enAttente, :confirmee)", LocalDate.class)
                        .setParameter("id", vehiculeId)
                        .setParameter("enAttente", StatutReservation.EN_ATTENTE)
                        .setParameter("confirmee", StatutReservation.CONFIRMEE)
                        .getSingleResult();
                LocalDate contractEnd = em.createQuery(
                                "select max(c.dateFin) from Contrat c " +
                                        "where c.vehicule.id = :id and c.statut = :actif", LocalDate.class)
                        .setParameter("id", vehiculeId)
                        .setParameter("actif", StatutContrat.ACTIF)
                        .getSingleResult();
                if (reservationEnd != null && reservationEnd.plusDays(1).isAfter(date)) {
                    date = reservationEnd.plusDays(1);
                }
                if (contractEnd != null && contractEnd.plusDays(1).isAfter(date)) {
                    date = contractEnd.plusDays(1);
                }
            }
            return date;
        } finally {
            em.close();
        }
    }

    // Un véhicule à partir de son id
    public Vehicule findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Vehicule.class, id);
        } finally {
            em.close();
        }
    }

    // Ajoute ou modifie  un véhicule
    public void save(Vehicule v) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(v);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // annule si une erreur est survenue
            }
            em.close();
        }
    }

    // Supprime un véhicule
    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Vehicule v = em.find(Vehicule.class, id);
            if (v != null) {
                em.remove(v);
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select count(v) from Vehicule v", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public long countDisponibles() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select count(v) from Vehicule v where v.disponible = true", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}