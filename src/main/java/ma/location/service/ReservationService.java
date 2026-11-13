package ma.location.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ma.location.model.Client;
import ma.location.model.Offre;
import ma.location.model.Reservation;
import ma.location.model.Role;
import ma.location.model.StatutReservation;
import ma.location.model.Vehicule;
import ma.location.model.Utilisateur;
import ma.location.util.JPAUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReservationService {

    public Reservation creer(Long clientId, Long vehiculeId, Long offreId,
                             LocalDate debut, LocalDate fin) {
        validatePeriod(debut, fin);

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Client client = em.find(Client.class, clientId);
            Vehicule vehicule = em.find(Vehicule.class, vehiculeId);
            Offre offre = offreId == null ? null : em.find(Offre.class, offreId);
            if (client == null) {
                throw new IllegalArgumentException("Client introuvable.");
            }
            if (vehicule == null) {
                throw new IllegalArgumentException("Véhicule introuvable.");
            }
            if (offreId != null && offre == null) {
                throw new IllegalArgumentException("Offre introuvable.");
            }
            if (!vehicule.isDisponible() || hasOverlappingReservation(em, vehiculeId, debut, fin)) {
                throw new IllegalStateException("Le véhicule n'est pas disponible pour cette période.");
            }
            if (offre != null && (debut.isBefore(offre.getDateDebut()) || fin.isAfter(offre.getDateFin()))) {
                throw new IllegalArgumentException("L'offre n'est pas applicable à cette période.");
            }

            long jours = ChronoUnit.DAYS.between(debut, fin);
            double montant = jours * vehicule.getPrixJour();
            if (offre != null) {
                montant *= 1 - (offre.getPourcentageRemise() / 100);
            }

            Reservation reservation = new Reservation();
            reservation.setClient(client);
            reservation.setVehicule(vehicule);
            reservation.setOffre(offre);
            reservation.setDateDebut(debut);
            reservation.setDateFin(fin);
            reservation.setMontantTotal(montant);
            reservation.setStatut(StatutReservation.EN_ATTENTE);
            em.persist(reservation);
            tx.commit();
            return reservation;
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            throw exception;
        } finally {
            em.close();
        }
    }

    public Reservation confirmer(Long reservationId, Utilisateur administrateur) {
        requireAdmin(administrateur);

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Reservation reservation = em.find(Reservation.class, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Réservation introuvable.");
            }

            if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
                throw new IllegalStateException("Seule une réservation en attente peut être confirmée.");
            }
            if (hasOverlappingReservation(em, reservation.getVehicule().getId(),
                    reservation.getDateDebut(), reservation.getDateFin(), reservationId)) {
                throw new IllegalStateException("Le véhicule n'est plus disponible pour cette période.");
            }
            reservation.setStatut(StatutReservation.CONFIRMEE);
            tx.commit();
            return reservation;
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            throw exception;
        } finally {
            em.close();
        }
    }

    public Reservation annuler(Long reservationId, Utilisateur administrateur) {
        requireAdmin(administrateur);

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Reservation reservation = em.find(Reservation.class, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Réservation introuvable.");
            }
            if (reservation.getStatut() == StatutReservation.ANNULEE
                    || reservation.getStatut() == StatutReservation.TERMINEE) {
                throw new IllegalStateException("Cette réservation ne peut plus être annulée.");
            }
            reservation.setStatut(StatutReservation.ANNULEE);
            tx.commit();
            return reservation;
        } catch (RuntimeException exception) {
            if (tx.isActive()) tx.rollback();
            throw exception;
        } finally {
            em.close();
        }
    }

    private boolean hasOverlappingReservation(EntityManager em, Long vehiculeId,
                                               LocalDate debut, LocalDate fin) {
        return hasOverlappingReservation(em, vehiculeId, debut, fin, null);
    }

    private boolean hasOverlappingReservation(EntityManager em, Long vehiculeId,
                                               LocalDate debut, LocalDate fin, Long excludedId) {
        String query = "select count(r) from Reservation r " +
                "where r.vehicule.id = :vehiculeId " +
                "and r.statut <> :annulee " +
                "and r.dateDebut < :fin and r.dateFin > :debut";
        if (excludedId != null) query += " and r.id <> :excludedId";
        var typedQuery = em.createQuery(query, Long.class)
                .setParameter("vehiculeId", vehiculeId)
                .setParameter("annulee", StatutReservation.ANNULEE)
                .setParameter("debut", debut)
                .setParameter("fin", fin);
        if (excludedId != null) typedQuery.setParameter("excludedId", excludedId);
        return typedQuery.getSingleResult() > 0;
    }

    private void validatePeriod(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || !debut.isBefore(fin)) {
            throw new IllegalArgumentException("La période de réservation est invalide.");
        }
    }

    private void requireAdmin(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getRole() != Role.ADMIN) {
            throw new SecurityException("Accès réservé à un administrateur.");
        }
    }
}
