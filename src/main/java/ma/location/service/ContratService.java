package ma.location.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ma.location.model.Contrat;
import ma.location.model.Reservation;
import ma.location.model.StatutContrat;
import ma.location.model.StatutReservation;
import ma.location.model.StatutVehicule;
import ma.location.model.Vehicule;
import ma.location.util.JPAUtil;
import java.time.LocalDate;

public class ContratService {

    public void cloturerContratsExpires() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            var contrats = em.createQuery(
                            "select c from Contrat c where c.statut = :actif and c.dateFin <= :today",
                            Contrat.class)
                    .setParameter("actif", StatutContrat.ACTIF)
                    .setParameter("today", LocalDate.now())
                    .getResultList();
            for (Contrat contrat : contrats) {
                contrat.setStatut(StatutContrat.TERMINE);
                Vehicule vehicule = contrat.getVehicule();
                vehicule.setDisponible(true);
                vehicule.setStatut(StatutVehicule.DISPONIBLE);
            }
            tx.commit();
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            throw exception;
        } finally {
            em.close();
        }
    }

    public double calculerSoldeFinal(Contrat contrat) {
        if (contrat == null) {
            throw new IllegalArgumentException("Contrat requis.");
        }
        return contrat.getMontantTotal();
    }

    public Contrat creerDepuisReservation(Long reservationId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Reservation reservation = em.find(Reservation.class, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Réservation introuvable.");
            }
            if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
                throw new IllegalStateException("La réservation doit être confirmée.");
            }
            Long existing = em.createQuery(
                            "select count(c) from Contrat c where c.client.id = :clientId " +
                                    "and c.vehicule.id = :vehiculeId and c.dateDebut = :dateDebut",
                            Long.class)
                    .setParameter("clientId", reservation.getClient().getId())
                    .setParameter("vehiculeId", reservation.getVehicule().getId())
                    .setParameter("dateDebut", reservation.getDateDebut())
                    .getSingleResult();
            if (existing > 0) {
                throw new IllegalStateException("Un contrat existe déjà pour cette réservation.");
            }

            Vehicule vehicule = reservation.getVehicule();
            if (!vehicule.isDisponible()) {
                throw new IllegalStateException("Le véhicule n'est plus disponible.");
            }
            Contrat contrat = new Contrat(
                    reservation.getClient(),
                    vehicule,
                    reservation.getDateDebut(),
                    reservation.getDateFin(),
                    reservation.getMontantTotal());
            vehicule.setDisponible(false);
            vehicule.setStatut(StatutVehicule.LOUE);
            reservation.setStatut(StatutReservation.TERMINEE);
            em.persist(contrat);
            tx.commit();
            return contrat;
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            throw exception;
        } finally {
            em.close();
        }
    }

    public Contrat cloturer(Long contratId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Contrat contrat = em.find(Contrat.class, contratId);
            if (contrat == null) {
                throw new IllegalArgumentException("Contrat introuvable.");
            }
            if (contrat.getStatut() != StatutContrat.ACTIF) {
                throw new IllegalStateException("Le contrat est déjà clôturé.");
            }
            contrat.setMontantTotal(calculerSoldeFinal(contrat));
            contrat.setStatut(StatutContrat.TERMINE);
            Vehicule vehicule = contrat.getVehicule();
            vehicule.setDisponible(true);
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
            tx.commit();
            return contrat;
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            throw exception;
        } finally {
            em.close();
        }
    }
}
