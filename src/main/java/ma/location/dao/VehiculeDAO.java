package ma.location.dao;

import ma.location.model.Vehicule;
import ma.location.util.JPAUtil;

import jakarta.persistence.EntityManager;
import java.util.List;

public class VehiculeDAO extends GenericDAO<Vehicule> {

    public VehiculeDAO() {
        super(Vehicule.class);
    }

    public List<Vehicule> findDisponibles() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select v from Vehicule v where v.disponible = true order by v.marque, v.modele",
                    Vehicule.class).getResultList();
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