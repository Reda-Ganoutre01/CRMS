package ma.location.servlet;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.location.dao.ContratDAO;
import ma.location.model.Contrat;
import ma.location.service.ClientService;
import ma.location.service.ContratService;
import ma.location.util.JPAUtil;

import java.io.IOException;

@WebServlet({"/admin/contrats/*", "/contrats/*"})
public class ContratServlet extends HttpServlet {
    private final ClientService clientService = new ClientService();
    private final ContratService contratService = new ContratService();
    private final ContratDAO contratDAO = new ContratDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/facture".equals(request.getPathInfo())) {
            if (!requireAdmin(request, response)) return;
            showInvoice(request, response);
            return;
        }
        if ("/admin/contrats".equals(request.getServletPath())
                && request.getPathInfo() == null) {
                if (!requireAdmin(request, response)) return;
            request.setAttribute("derniersContrats", contratDAO.findRecent(100));
            request.getRequestDispatcher("/WEB-INF/views/contrats/liste.jsp").forward(request, response);
            return;
        }
        if ("/contrats".equals(request.getServletPath()) && request.getPathInfo() == null) {
            request.setAttribute("derniersContrats", contratDAO.findRecent(100));
            request.getRequestDispatcher("/WEB-INF/views/contrats.jsp").forward(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void showInvoice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long id = requiredLong(request.getParameter("id"));
            Contrat contrat = em.createQuery(
                            "select c from Contrat c join fetch c.client join fetch c.vehicule where c.id = :id",
                            Contrat.class)
                    .setParameter("id", id)
                    .getResultStream().findFirst().orElse(null);
            if (contrat == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Contrat introuvable.");
                return;
            }
            request.setAttribute("contrat", contrat);
            request.getRequestDispatcher("/WEB-INF/views/contrats/facture.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!requireAdmin(request, response)) return;
        try {
            if ("/demarrer".equals(request.getPathInfo())) {
                contratService.creerDepuisReservation(requiredLong(request.getParameter("reservationId")));
                response.sendRedirect(request.getContextPath() + "/accueil");
            } else if ("/cloturer".equals(request.getPathInfo())) {
                Contrat contrat = contratService.cloturer(
                        requiredLong(request.getParameter("contratId")));
                response.sendRedirect(request.getContextPath() + "/admin/contrats/facture?id=" + contrat.getId());
            } else if ("/supprimer".equals(request.getPathInfo())) {
                contratDAO.delete(requiredLong(request.getParameter("contratId")));
                request.getSession().setAttribute("message", "Contrat supprimé.");
                response.sendRedirect(request.getContextPath() + "/admin/contrats");
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (RuntimeException exception) {
            response.sendRedirect(request.getContextPath() + "/accueil?error=" +
                    java.net.URLEncoder.encode(exception.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    private Long requiredLong(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Identifiant requis.");
        return Long.valueOf(value);
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
}
