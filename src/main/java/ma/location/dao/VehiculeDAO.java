package ma.location.dao;

import ma.location.model.Vehicule;
import ma.location.util.JPAUtil;

import jakarta.persistence.EntityManager;
import java.util.List;

public class VehiculeDAO {

    // Tous les véhicules
    public List<Vehicule> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select v from Vehicule v order by v.id desc", Vehicule.class)
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