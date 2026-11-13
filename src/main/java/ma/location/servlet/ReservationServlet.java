package ma.location.servlet;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.location.dao.ReservationDAO;
import ma.location.model.Client;
import ma.location.model.Offre;
import ma.location.model.Reservation;
import ma.location.model.Utilisateur;
import ma.location.model.Vehicule;
import ma.location.service.ClientService;
import ma.location.service.ReservationService;
import ma.location.util.JPAUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.net.URLEncoder;
import java.util.List;

@WebServlet({"/reservations/*", "/mes-reservations", "/admin/reservations",
        "/admin/reservations/valider", "/admin/reservations/supprimer"})
public class ReservationServlet extends HttpServlet {
    private final ClientService clientService = new ClientService();
    private final ReservationService reservationService = new ReservationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/admin/reservations".equals(request.getServletPath())) {
                if (!requireAdmin(request, response)) return;
            EntityManager em = JPAUtil.getEntityManager();
            try {
                request.setAttribute("reservations", em.createQuery(
                                "select r from Reservation r join fetch r.client join fetch r.vehicule " +
                                        "order by r.id desc", Reservation.class)
                        .getResultList());
                request.getRequestDispatcher("/WEB-INF/views/admin-reservations.jsp").forward(request, response);
            } finally {
                em.close();
            }
            return;
        }
        if ("/mes-reservations".equals(request.getServletPath())) {
            if (!requireClient(request, response)) return;
            showClientReservations(request, response);
            return;
        }
        if ("/reservations".equals(request.getServletPath())) {
            if (!requireClient(request, response)) return;
            EntityManager em = JPAUtil.getEntityManager();
            try {
                if (!"/nouvelle".equals(request.getPathInfo())) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                Long vehicleId = requiredLong(request.getParameter("vehiculeId"), "Véhicule requis.");
                Vehicule vehicle = em.find(Vehicule.class, vehicleId);
                if (vehicle == null) throw new IllegalArgumentException("Véhicule introuvable.");
                List<Offre> offers = em.createQuery("select o from Offre o order by o.dateDebut desc", Offre.class)
                        .getResultList();
                request.setAttribute("vehicule", vehicle);
                request.setAttribute("offres", offers);
                request.getRequestDispatcher("/WEB-INF/views/reservations/form.jsp").forward(request, response);
            } finally {
                em.close();
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/admin/reservations/valider".equals(request.getServletPath())) {
            validateReservation(request, response);
            return;
        }
        if ("/admin/reservations/supprimer".equals(request.getServletPath())) {
            deleteReservation(request, response);
            return;
        }
        if ("/reservations".equals(request.getServletPath())
                && "/creer".equals(request.getPathInfo())) {
            createReservation(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void createReservation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireClient(request, response)) return;
        try {
            Long clientId = clientService.currentUserId(request);
            Long vehicleId = requiredLong(request.getParameter("vehiculeId"), "Véhicule requis.");
            Long offerId = optionalLong(request.getParameter("offreId"));
            Reservation reservation = reservationService.creer(clientId, vehicleId, offerId,
                    LocalDate.parse(request.getParameter("dateDebut")),
                    LocalDate.parse(request.getParameter("dateFin")));
            request.getSession().setAttribute("message", "Réservation envoyée avec succès.");
            response.sendRedirect(request.getContextPath() + "/mes-reservations");
        } catch (RuntimeException exception) {
            showReservationFormWithError(request, response, exception.getMessage());
        }
    }

    private void showReservationFormWithError(HttpServletRequest request,
                                              HttpServletResponse response,
                                              String message)
            throws ServletException, IOException {
        request.setAttribute("error", message == null ? "La réservation est invalide." : message);
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long vehicleId = requiredLong(request.getParameter("vehiculeId"), "Véhicule requis.");
            Vehicule vehicle = em.find(Vehicule.class, vehicleId);
            if (vehicle == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Véhicule introuvable.");
                return;
            }
            request.setAttribute("vehicule", vehicle);
            request.setAttribute("offres", em.createQuery(
                            "select o from Offre o order by o.dateDebut desc", Offre.class)
                    .getResultList());
            request.getRequestDispatcher("/WEB-INF/views/reservations/form.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }

    private void showClientReservations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Reservation> reservations = new ReservationDAO()
                .findByClient(clientService.currentUserId(request));
        request.setAttribute("reservations", reservations);
        request.getRequestDispatcher("/WEB-INF/views/reservations/liste.jsp").forward(request, response);
    }

    private void validateReservation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!requireAdmin(request, response)) return;
        try {
            Long reservationId = requiredLong(request.getParameter("reservationId"), "Réservation requise.");
            Utilisateur admin = currentUser(request);
            reservationService.confirmer(reservationId, admin);
            request.getSession().setAttribute("message", "Réservation validée.");
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
        } catch (RuntimeException exception) {
            request.getSession().setAttribute("error",
                    exception.getMessage() == null ? "Validation impossible." : exception.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/reservations");
        }
    }

    private void deleteReservation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!requireAdmin(request, response)) return;
        try {
            new ReservationDAO().delete(requiredLong(request.getParameter("reservationId"), "Réservation requise."));
            request.getSession().setAttribute("message", "Réservation supprimée.");
        } catch (RuntimeException exception) {
            request.getSession().setAttribute("error", "Suppression impossible pour cette réservation.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/reservations");
    }

    private Utilisateur currentUser(HttpServletRequest request) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Utilisateur.class, clientService.currentUserId(request));
        } finally {
            em.close();
        }
    }

    private Long requiredLong(String value, String message) {
        Long result = optionalLong(value);
        if (result == null) throw new IllegalArgumentException(message);
        return result;
    }

    private Long optionalLong(String value) {
        return value == null || value.isBlank() ? null : Long.valueOf(value);
    }

    private void redirectError(HttpServletResponse response, HttpServletRequest request,
                               String path, String message) throws IOException {
        response.sendRedirect(request.getContextPath() + path + (path.contains("?") ? "&" : "?") + "error=" +
                java.net.URLEncoder.encode(message == null ? "Erreur" : message,
                        java.nio.charset.StandardCharsets.UTF_8));
    }

    private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            clientService.requireAdmin(request);
            return true;
        } catch (SecurityException exception) {
            response.sendRedirect(request.getContextPath() + "/login?error=" +
                    java.net.URLEncoder.encode("Connectez-vous avec un compte administrateur.",
                            java.nio.charset.StandardCharsets.UTF_8));
            return false;
        }
    }

    private boolean requireClient(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            clientService.requireClient(request);
            return true;
        } catch (SecurityException exception) {
            String target = clientService.isAuthenticated(request)
                    && clientService.hasRole(request, ma.location.model.Role.ADMIN)
                    ? "/accueil"
                    : "/login";
            if ("/login".equals(target)) {
                String returnPath = request.getRequestURI().substring(request.getContextPath().length());
                if (request.getQueryString() != null) returnPath += "?" + request.getQueryString();
                target += "?redirect=" + URLEncoder.encode(returnPath, java.nio.charset.StandardCharsets.UTF_8);
            }
            response.sendRedirect(request.getContextPath() + target +
                    (target.contains("?") ? "&" : "?") + "error=" +
                    URLEncoder.encode(exception.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
            return false;
        }
    }
}
