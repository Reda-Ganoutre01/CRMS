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
import java.time.LocalDate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet({"/admin/contrats/*", "/contrats/*"})
public class ContratServlet extends HttpServlet {
    private final ClientService clientService = new ClientService();
    private final ContratService contratService = new ContratService();
    private final ContratDAO contratDAO = new ContratDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if ("/facture".equals(path)) {
            if (!requireAdmin(request, response)) return;
            showInvoice(request, response);
            return;
        }
        if ("/modifier".equals(path)) {
            if (!requireAdmin(request, response)) return;
            showEditForm(request, response);
            return;
        }
        if ("/admin/contrats".equals(request.getServletPath()) && path == null) {
            if (!requireAdmin(request, response)) return;
            request.setAttribute("derniersContrats", contratDAO.findRecent(100));
            request.getRequestDispatcher("/WEB-INF/views/contrats/liste.jsp").forward(request, response);
            return;
        }
        if ("/contrats".equals(request.getServletPath()) && path == null) {
            if (!requireAdmin(request, response)) return;
            response.sendRedirect(request.getContextPath() + "/admin/contrats");
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void showInvoice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Contrat contrat = findContract(em, requiredLong(request.getParameter("id")));
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

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Contrat contrat = findContract(em, requiredLong(request.getParameter("id")));
            if (contrat == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Contrat introuvable.");
                return;
            }
            request.setAttribute("contrat", contrat);
            request.getRequestDispatcher("/WEB-INF/views/contrats/modifier.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!requireAdmin(request, response)) return;
        try {
            String path = request.getPathInfo();
            if ("/demarrer".equals(path)) {
                contratService.creerDepuisReservation(requiredLong(request.getParameter("reservationId")));
                response.sendRedirect(request.getContextPath() + "/admin/contrats");
            } else if ("/cloturer".equals(path)) {
                Contrat contrat = contratService.cloturer(
                        requiredLong(request.getParameter("contratId")),
                        Integer.valueOf(request.getParameter("kmRetour")));
                response.sendRedirect(request.getContextPath() + "/admin/contrats/facture?id=" + contrat.getId());
            } else if ("/modifier".equals(path)) {
                contratService.modifier(
                        requiredLong(request.getParameter("contratId")),
                        LocalDate.parse(request.getParameter("dateDebut")),
                        LocalDate.parse(request.getParameter("dateFin")),
                        Double.parseDouble(request.getParameter("montantTotal")),
                        parseOptionalInteger(request.getParameter("kmDepart")),
                        parseOptionalInteger(request.getParameter("kmRetour")));
                response.sendRedirect(request.getContextPath() + "/admin/contrats");
            } else if ("/supprimer".equals(path)) {
                contratService.supprimer(requiredLong(request.getParameter("contratId")));
                response.sendRedirect(request.getContextPath() + "/admin/contrats");
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (RuntimeException exception) {
            response.sendRedirect(request.getContextPath() + "/admin/contrats?error=" +
                    URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8));
        }
    }

    private Contrat findContract(EntityManager em, Long id) {
        return em.createQuery(
                        "select c from Contrat c join fetch c.client join fetch c.vehicule where c.id = :id",
                        Contrat.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    private Integer parseOptionalInteger(String value) {
        return value == null || value.isBlank() ? null : Integer.valueOf(value);
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
                    URLEncoder.encode("Connectez-vous avec un compte administrateur.",
                            StandardCharsets.UTF_8));
            return false;
        }
    }
}
