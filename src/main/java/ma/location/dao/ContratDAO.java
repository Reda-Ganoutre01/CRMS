package ma.location.dao;

import ma.location.model.Client;
import ma.location.model.Contrat;
import ma.location.model.Statut;
import ma.location.model.Vehicule;
import ma.location.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ContratDAO extends GenericDAO<Contrat> {

    public ContratDAO() {
        super(Contrat.class);
    }

    @Override
    public List<Contrat> findAll() {
        return findRecent(Integer.MAX_VALUE);
    }

    /** Contrats les plus récents, avec client et véhicule chargés. */
    public List<Contrat> findRecent(int max) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select c from Contrat c join fetch c.client join fetch c.vehicule order by c.id desc",
                            Contrat.class)
                    .setMaxResults(max)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Crée un contrat, calcule le montant et rend le véhicule indisponible (une seule transaction). */
    public Contrat creer(Long clientId, Long vehiculeId, LocalDate debut, LocalDate fin) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Client client = em.find(Client.class, clientId);
            Vehicule vehicule = em.find(Vehicule.class, vehiculeId);
            if (client == null || vehicule == null) {
                throw new IllegalArgumentException("Client ou véhicule introuvable.");
            }
            if (!vehicule.isDisponible()) {
                throw new IllegalStateException("Ce véhicule n'est pas disponible.");
            }
            long jours = Math.max(1, ChronoUnit.DAYS.between(debut, fin));
            Contrat contrat = new Contrat(client, vehicule, debut, fin, jours * vehicule.getPrixJour());
            vehicule.setDisponible(false);
            em.persist(contrat);
            tx.commit();
            return contrat;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /** Clôture (TERMINE) ou annule (ANNULE) un contrat en cours et libère le véhicule. */
    public void cloturer(Long id, Statut nouveauStatut) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Contrat contrat = em.find(Contrat.class, id);
            if (contrat == null) {
                throw new IllegalArgumentException("Contrat introuvable.");
            }
            if (contrat.getStatut() != Statut.EN_COURS) {
                throw new IllegalStateException("Ce contrat est déjà clôturé.");
            }
            contrat.setStatut(nouveauStatut);
            contrat.getVehicule().setDisponible(true);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Contrat contrat = em.find(Contrat.class, id);
            if (contrat != null) {
                if (contrat.getStatut() == Statut.EN_COURS) {
                    contrat.getVehicule().setDisponible(true);
                }
                em.remove(contrat);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public long countEnCours() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select count(c) from Contrat c where c.statut = :s", Long.class)
                    .setParameter("s", Statut.EN_COURS).getSingleResult();
        } finally {
            em.close();
        }
    }

    /** Chiffre d'affaires : somme des contrats non annulés. */
    public double chiffreAffaires() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Double total = em.createQuery("select sum(c.montantTotal) from Contrat c where c.statut <> :s", Double.class)
                    .setParameter("s", Statut.ANNULE).getSingleResult();
            return total == null ? 0 : total;
        } finally {
            em.close();
        }
    }
}