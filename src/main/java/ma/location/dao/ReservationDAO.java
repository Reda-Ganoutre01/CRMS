package ma.location.dao;

import jakarta.persistence.EntityManager;
import ma.location.model.Reservation;
import ma.location.model.StatutReservation;
import ma.location.model.Vehicule;
import ma.location.util.JPAUtil;

import java.time.LocalDate;
import java.util.List;

public class ReservationDAO extends GenericDAO<Reservation> {

    public ReservationDAO() {
        super(Reservation.class);
    }

    public List<Reservation> findByClient(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("L'identifiant du client est requis.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select r from Reservation r " +
                                    "join fetch r.vehicule " +
                                    "where r.client.id = :clientId " +
                                    "order by r.dateDebut desc, r.id desc",
                            Reservation.class)
                    .setParameter("clientId", clientId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean isVehiculeDisponible(Long vehiculeId, LocalDate debut, LocalDate fin) {
        if (vehiculeId == null || debut == null || fin == null || !debut.isBefore(fin)) {
            throw new IllegalArgumentException("Le véhicule et une période valide sont requis.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            Vehicule vehicule = em.find(Vehicule.class, vehiculeId);
            if (vehicule == null || !vehicule.isDisponible()) {
                return false;
            }

            Long reservationsEnConflit = em.createQuery(
                            "select count(r) from Reservation r " +
                                    "where r.vehicule.id = :vehiculeId " +
                                    "and r.statut <> :annulee " +
                                    "and r.dateDebut < :fin " +
                                    "and r.dateFin > :debut",
                            Long.class)
                    .setParameter("vehiculeId", vehiculeId)
                    .setParameter("annulee", StatutReservation.ANNULEE)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .getSingleResult();

            return reservationsEnConflit == 0;
        } finally {
            em.close();
        }
    }
}
