package ma.location.servlet;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.location.model.Contrat;
import ma.location.service.ClientService;
import ma.location.service.ContratService;
import ma.location.util.JPAUtil;

import java.io.IOException;

@WebServlet("/admin/contrats/*")
public class ContratServlet extends HttpServlet {
    private final ClientService clientService = new ClientService();
    private final ContratService contratService = new ContratService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/facture".equals(request.getPathInfo())) {
            clientService.requireAdmin(request);
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
                request.getRequestDispatcher("/WEB-INF/views/facture.jsp").forward(request, response);
            } finally {
                em.close();
            }
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        clientService.requireAdmin(request);
        try {
            if ("/demarrer".equals(request.getPathInfo())) {
                contratService.creerDepuisReservation(requiredLong(request.getParameter("reservationId")));
                response.sendRedirect(request.getContextPath() + "/accueil");
            } else if ("/cloturer".equals(request.getPathInfo())) {
                Contrat contrat = contratService.cloturer(
                        requiredLong(request.getParameter("contratId")),
                        Integer.valueOf(request.getParameter("kmRetour")));
                response.sendRedirect(request.getContextPath() + "/admin/contrats/facture?id=" + contrat.getId());
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
}
